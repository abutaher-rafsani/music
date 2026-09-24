package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.KliqDatabase
import com.example.data.model.ScoutGigEntity
import com.example.data.model.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Data Repository for Cloud-based Management of User Profiles and Gig Listings using Cloud Firestore.
 * Supports real-time snapshot synchronization, offline persistence fallback, and local Room synchronization.
 */
class FirestoreDataRepository(
    private val context: Context,
    private val localDatabase: KliqDatabase? = null
) {
    companion object {
        private const val TAG = "FirestoreDataRepo"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_GIGS = "scout_gigs"
    }

    private val db: FirebaseFirestore? by lazy {
        initFirestore()
    }

    private fun initFirestore(): FirebaseFirestore? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val apiKey = try {
                    val key = BuildConfig.FIREBASE_API_KEY
                    if (key.isNotBlank()) key else "AIzaSyCP4PhE8-rWtg1rzm_Zh0ovfbo7hzpVOEk"
                } catch (t: Throwable) {
                    "AIzaSyCP4PhE8-rWtg1rzm_Zh0ovfbo7hzpVOEk"
                }

                val projectId = try {
                    val pid = BuildConfig.FIREBASE_PROJECT_ID
                    if (pid.isNotBlank()) pid else "kliqbd"
                } catch (t: Throwable) {
                    "kliqbd"
                }

                val appId = try {
                    val aid = BuildConfig.FIREBASE_APP_ID
                    if (aid.isNotBlank()) aid else "1:247236350256:web:c7b4156f88a7045b9738f5"
                } catch (t: Throwable) {
                    "1:247236350256:web:c7b4156f88a7045b9738f5"
                }

                val options = FirebaseOptions.Builder()
                    .setApiKey(apiKey)
                    .setProjectId(projectId)
                    .setApplicationId(appId)
                    .setDatabaseUrl("https://kliqbd-default-rtdb.firebaseio.com")
                    .setStorageBucket("kliqbd.firebasestorage.app")
                    .setGcmSenderId("247236350256")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore initialization notice: ${e.message}")
            try {
                FirebaseFirestore.getInstance()
            } catch (t: Throwable) {
                null
            }
        }
    }

    // ==========================================
    // USER PROFILES MANAGEMENT
    // ==========================================

    /**
     * Real-time stream of a specific user profile by user ID.
     */
    fun getUserProfileFlow(userId: String): Flow<UserEntity?> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            // Fallback to local Room database if Firestore unavailable
            val localUser = localDatabase?.userDao()?.getUserById(userId)
            trySend(localUser)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_USERS)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error listening to user profile: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toUserEntity()
                    trySend(user)
                } else {
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time stream of all user profiles in the system.
     */
    fun getAllUserProfilesFlow(): Flow<List<UserEntity>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_USERS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error listening to users collection: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val users = snapshot.documents.mapNotNull { it.toUserEntity() }
                    trySend(users)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Fetch a user profile once (suspend).
     */
    suspend fun getUserProfile(userId: String): Result<UserEntity?> {
        return try {
            val firestore = db
            if (firestore == null) {
                val localUser = localDatabase?.userDao()?.getUserById(userId)
                return Result.success(localUser)
            }
            val snapshot = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            val user = snapshot.toUserEntity() ?: localDatabase?.userDao()?.getUserById(userId)
            Result.success(user)
        } catch (e: Throwable) {
            val localUser = localDatabase?.userDao()?.getUserById(userId)
            Result.success(localUser)
        }
    }

    /**
     * Create or update a user profile in Firestore and sync to local Room database.
     */
    suspend fun saveUserProfile(user: UserEntity): Result<Unit> {
        return try {
            // 1. Sync to local database first
            localDatabase?.userDao()?.insertUser(user)

            // 2. Sync to Cloud Firestore
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_USERS)
                    .document(user.id)
                    .set(user.toMap(), SetOptions.merge())
                    .await()
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice saving profile to Firestore: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Partially update specific user profile fields (e.g. bio, location, trust score).
     */
    suspend fun updateUserProfileFields(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .update(updates)
                    .await()
            }
            // Update local Room entity if possible
            val current = localDatabase?.userDao()?.getUserById(userId)
            if (current != null) {
                var updated = current
                if (updates.containsKey("bio")) updated = updated.copy(bio = updates["bio"] as? String ?: updated.bio)
                if (updates.containsKey("fullName")) updated = updated.copy(fullName = updates["fullName"] as? String ?: updated.fullName)
                if (updates.containsKey("jobInfo")) updated = updated.copy(jobInfo = updates["jobInfo"] as? String ?: updated.jobInfo)
                if (updates.containsKey("addressDistrict")) updated = updated.copy(addressDistrict = updates["addressDistrict"] as? String ?: updated.addressDistrict)
                if (updates.containsKey("addressUpazila")) updated = updated.copy(addressUpazila = updates["addressUpazila"] as? String ?: updated.addressUpazila)
                if (updates.containsKey("trustScore")) updated = updated.copy(trustScore = (updates["trustScore"] as? Number)?.toDouble() ?: updated.trustScore)
                localDatabase.userDao().updateUser(updated)
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice updating profile fields: ${e.message}")
            Result.success(Unit)
        }
    }

    // ==========================================
    // GIG LISTINGS MANAGEMENT
    // ==========================================

    /**
     * Real-time stream of scout gig listings with optional category and status filters.
     */
    fun getGigListingsFlow(
        category: String? = null,
        status: String? = null
    ): Flow<List<ScoutGigEntity>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var query: Query = firestore.collection(COLLECTION_GIGS)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        if (!category.isNullOrBlank() && category != "All") {
            query = query.whereEqualTo("category", category)
        }

        if (!status.isNullOrBlank() && status != "All") {
            query = query.whereEqualTo("status", status)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error listening to scout gigs: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val gigs = snapshot.documents.mapNotNull { it.toScoutGigEntity() }
                trySend(gigs)
            }
        }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time stream of a single scout gig by ID.
     */
    fun getGigByIdFlow(gigId: String): Flow<ScoutGigEntity?> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            val localGig = localDatabase?.scoutGigDao()?.getGigById(gigId)
            trySend(localGig)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_GIGS)
            .document(gigId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error listening to gig $gigId: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toScoutGigEntity())
                } else {
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Fetch a specific gig once (suspend).
     */
    suspend fun getGigById(gigId: String): Result<ScoutGigEntity?> {
        return try {
            val firestore = db
            if (firestore == null) {
                val localGig = localDatabase?.scoutGigDao()?.getGigById(gigId)
                return Result.success(localGig)
            }
            val snapshot = firestore.collection(COLLECTION_GIGS)
                .document(gigId)
                .get()
                .await()
            val gig = snapshot.toScoutGigEntity() ?: localDatabase?.scoutGigDao()?.getGigById(gigId)
            Result.success(gig)
        } catch (e: Throwable) {
            val localGig = localDatabase?.scoutGigDao()?.getGigById(gigId)
            Result.success(localGig)
        }
    }

    /**
     * Create a new gig listing in Firestore and sync to Room.
     */
    suspend fun createGigListing(gig: ScoutGigEntity): Result<String> {
        return try {
            // Local persistence
            localDatabase?.scoutGigDao()?.insertGig(gig)

            // Firestore sync
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_GIGS)
                    .document(gig.id)
                    .set(gig.toMap(), SetOptions.merge())
                    .await()
            }
            Result.success(gig.id)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice creating gig in Firestore: ${e.message}")
            Result.success(gig.id)
        }
    }

    /**
     * Update an entire gig listing in Firestore and Room.
     */
    suspend fun updateGigListing(gig: ScoutGigEntity): Result<Unit> {
        return try {
            localDatabase?.scoutGigDao()?.updateGig(gig)
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_GIGS)
                    .document(gig.id)
                    .set(gig.toMap(), SetOptions.merge())
                    .await()
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice updating gig in Firestore: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Claim a gig listing for a user. Sets status to 'in_progress'.
     */
    suspend fun claimGigListing(gigId: String, claimantId: String, claimantName: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "claimedBy" to claimantId,
                "claimedByName" to claimantName,
                "status" to "in_progress"
            )

            // Local update
            val current = localDatabase?.scoutGigDao()?.getGigById(gigId)
            if (current != null) {
                localDatabase.scoutGigDao().updateGig(
                    current.copy(
                        claimedBy = claimantId,
                        claimedByName = claimantName,
                        status = "in_progress"
                    )
                )
            }

            // Cloud Firestore update
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_GIGS)
                    .document(gigId)
                    .update(updates)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice claiming gig: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Submit completion proof notes and image URL for a claimed gig.
     */
    suspend fun submitGigProof(
        gigId: String,
        proofNotes: String,
        proofImageUrl: String?
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to "completed",
                "escrowStatus" to "released",
                "proofNotes" to proofNotes,
                "proofImageUrl" to (proofImageUrl ?: "")
            )

            // Local update
            val current = localDatabase?.scoutGigDao()?.getGigById(gigId)
            if (current != null) {
                localDatabase.scoutGigDao().updateGig(
                    current.copy(
                        status = "completed",
                        escrowStatus = "released",
                        proofNotes = proofNotes,
                        proofImageUrl = proofImageUrl
                    )
                )
            }

            // Cloud Firestore update
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_GIGS)
                    .document(gigId)
                    .update(updates)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice submitting gig proof: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Delete a gig listing from Firestore and Room.
     */
    suspend fun deleteGigListing(gigId: String): Result<Unit> {
        return try {
            localDatabase?.scoutGigDao()?.deleteGigById(gigId)
            val firestore = db
            if (firestore != null) {
                firestore.collection(COLLECTION_GIGS)
                    .document(gigId)
                    .delete()
                    .await()
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Log.w(TAG, "Notice deleting gig: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Seed initial profiles to Firestore if not already present.
     */
    suspend fun seedInitialProfiles(users: List<UserEntity>) {
        try {
            val firestore = db ?: return
            for (user in users) {
                firestore.collection(COLLECTION_USERS)
                    .document(user.id)
                    .set(user.toMap(), SetOptions.merge())
                    .await()
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Notice seeding profiles to Firestore: ${e.message}")
        }
    }

    /**
     * Seed initial gigs to Firestore if not already present.
     */
    suspend fun seedInitialGigs(gigs: List<ScoutGigEntity>) {
        try {
            val firestore = db ?: return
            for (gig in gigs) {
                firestore.collection(COLLECTION_GIGS)
                    .document(gig.id)
                    .set(gig.toMap(), SetOptions.merge())
                    .await()
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Notice seeding gigs to Firestore: ${e.message}")
        }
    }

    // ==========================================
    // SERIALIZATION HELPERS
    // ==========================================

    private fun DocumentSnapshot.toUserEntity(): UserEntity? {
        if (!exists()) return null
        return try {
            UserEntity(
                id = id,
                fullName = getString("fullName") ?: "",
                username = getString("username") ?: "",
                phone = getString("phone") ?: "",
                email = getString("email"),
                avatarUrl = getString("avatarUrl"),
                coverUrl = getString("coverUrl"),
                bio = getString("bio") ?: "",
                jobInfo = getString("jobInfo") ?: "Digital Creator",
                university = getString("university") ?: "Dhaka University",
                college = getString("college"),
                school = getString("school"),
                addressDivision = getString("addressDivision") ?: "ঢাকা",
                addressDistrict = getString("addressDistrict") ?: "Dhaka",
                addressUpazila = getString("addressUpazila") ?: "Dhanmondi",
                unionCity = getString("unionCity"),
                villageWard = getString("villageWard"),
                addressDetails = getString("addressDetails"),
                permanentDivision = getString("permanentDivision"),
                permanentDistrict = getString("permanentDistrict"),
                permanentUpazila = getString("permanentUpazila"),
                permanentUnionCity = getString("permanentUnionCity"),
                permanentVillageWard = getString("permanentVillageWard"),
                sameAddress = getBoolean("sameAddress") ?: true,
                dob = getString("dob"),
                gender = getString("gender"),
                interests = getString("interests"),
                userType = getString("userType") ?: "ছাত্র / শিক্ষার্থী",
                myReferralCode = getString("myReferralCode") ?: ("KLQ" + (1000..9999).random()),
                referredByCode = getString("referredByCode"),
                contactSyncEnabled = getBoolean("contactSyncEnabled") ?: true,
                acceptedTerms = getBoolean("acceptedTerms") ?: true,
                whatsapp = getString("whatsapp"),
                userLevel = getString("userLevel") ?: "Starter",
                specialBadge = getString("specialBadge") ?: "VVIP",
                isVerified = getBoolean("isVerified") ?: false,
                trustScore = getDouble("trustScore") ?: 5.00,
                createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun UserEntity.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "fullName" to fullName,
        "username" to username,
        "phone" to phone,
        "email" to email,
        "avatarUrl" to avatarUrl,
        "coverUrl" to coverUrl,
        "bio" to bio,
        "jobInfo" to jobInfo,
        "university" to university,
        "college" to college,
        "school" to school,
        "addressDivision" to addressDivision,
        "addressDistrict" to addressDistrict,
        "addressUpazila" to addressUpazila,
        "unionCity" to unionCity,
        "villageWard" to villageWard,
        "addressDetails" to addressDetails,
        "permanentDivision" to permanentDivision,
        "permanentDistrict" to permanentDistrict,
        "permanentUpazila" to permanentUpazila,
        "permanentUnionCity" to permanentUnionCity,
        "permanentVillageWard" to permanentVillageWard,
        "sameAddress" to sameAddress,
        "dob" to dob,
        "gender" to gender,
        "interests" to interests,
        "userType" to userType,
        "myReferralCode" to myReferralCode,
        "referredByCode" to referredByCode,
        "contactSyncEnabled" to contactSyncEnabled,
        "acceptedTerms" to acceptedTerms,
        "whatsapp" to whatsapp,
        "userLevel" to userLevel,
        "specialBadge" to specialBadge,
        "isVerified" to isVerified,
        "trustScore" to trustScore,
        "createdAt" to createdAt
    )

    private fun DocumentSnapshot.toScoutGigEntity(): ScoutGigEntity? {
        if (!exists()) return null
        return try {
            ScoutGigEntity(
                id = id,
                creatorId = getString("creatorId") ?: "",
                creatorName = getString("creatorName") ?: "",
                creatorAvatar = getString("creatorAvatar"),
                title = getString("title") ?: "",
                description = getString("description") ?: "",
                category = getString("category") ?: "General",
                location = getString("location") ?: "Dhaka",
                rewardCash = getDouble("rewardCash") ?: 0.0,
                escrowStatus = getString("escrowStatus") ?: "locked",
                remainingHours = getLong("remainingHours")?.toInt() ?: 6,
                claimedBy = getString("claimedBy"),
                claimedByName = getString("claimedByName"),
                status = getString("status") ?: "open",
                proofImageUrl = getString("proofImageUrl"),
                proofNotes = getString("proofNotes"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun ScoutGigEntity.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "creatorId" to creatorId,
        "creatorName" to creatorName,
        "creatorAvatar" to creatorAvatar,
        "title" to title,
        "description" to description,
        "category" to category,
        "location" to location,
        "rewardCash" to rewardCash,
        "escrowStatus" to escrowStatus,
        "remainingHours" to remainingHours,
        "claimedBy" to claimedBy,
        "claimedByName" to claimedByName,
        "status" to status,
        "proofImageUrl" to proofImageUrl,
        "proofNotes" to proofNotes,
        "createdAt" to createdAt
    )

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
        suspendCancellableCoroutine { cont ->
            addOnSuccessListener { result ->
                if (cont.isActive) cont.resume(result)
            }
            addOnFailureListener { exception ->
                if (cont.isActive) cont.resumeWithException(exception)
            }
            addOnCanceledListener {
                cont.cancel()
            }
        }
}
