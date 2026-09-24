package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.KliqDatabase
import com.example.data.model.UserEntity
import com.example.data.model.WalletEntity
import com.example.data.model.WalletTransactionEntity
import com.example.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository(
    private val context: Context,
    private val db: KliqDatabase,
    private val firestoreRepo: FirestoreDataRepository,
    private val authApiService: AuthApiService? = null
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    private val userDao = db.userDao()
    private val walletDao = db.walletDao()

    /**
     * Step 1: Live Username Availability Check (matches api_auth.php action=check_username)
     */
    suspend fun checkUsernameAvailability(rawUsername: String): CheckUsernameResponse = withContext(Dispatchers.IO) {
        val username = rawUsername.trim().lowercase().replace(Regex("[^a-z0-9_]"), "")

        if (username.length < 3) {
            return@withContext CheckUsernameResponse(
                available = false,
                message = "কমপক্ষে ৩ অক্ষরের হতে হবে"
            )
        }

        // 1. Try remote API if configured
        if (authApiService != null) {
            try {
                val response = authApiService.checkUsername(username = username)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Remote checkUsername fallback to local: ${e.message}")
            }
        }

        // 2. Check local database
        val existingLocal = userDao.getUserByUsername(username)
        if (existingLocal != null) {
            return@withContext CheckUsernameResponse(
                available = false,
                message = "ইউজারনেমটি আগে থেকেই রয়েছে"
            )
        }

        // 3. Check Firestore
        try {
            val firestoreUser = firestoreRepo.getUserProfile(username).getOrNull()
            if (firestoreUser != null) {
                return@withContext CheckUsernameResponse(
                    available = false,
                    message = "ইউজারনেমটি আগে থেকেই রয়েছে"
                )
            }
        } catch (e: Exception) {
            // Ignore if offline
        }

        return@withContext CheckUsernameResponse(
            available = true,
            message = "ইউজারনেমটি ফাঁকা আছে"
        )
    }

    /**
     * Multi-Step Registration (Step 1 to 4) - matches api_auth.php action=register
     */
    suspend fun registerUser(request: RegisterRequest): AuthResponse = withContext(Dispatchers.IO) {
        val fullName = request.fullName.trim()
        val username = request.username.trim().lowercase().replace(Regex("[^a-z0-9_]"), "")
        val phone = request.phone.trim()
        val email = request.email.trim()
        val password = request.password

        // Basic validation
        if (fullName.isBlank() || username.isBlank() || phone.isBlank() || password.isBlank()) {
            return@withContext AuthResponse(
                success = false,
                message = "বাধ্যতামূলক তথ্যগুলো পূরণ করুন!"
            )
        }

        if (password.length < 6) {
            return@withContext AuthResponse(
                success = false,
                message = "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে!"
            )
        }

        // Uniqueness check: Username, Phone, Email
        val existingByUsername = userDao.getUserByUsername(username)
        val existingByPhone = userDao.getUserByPhone(phone)
        val existingByEmail = if (email.isNotBlank()) userDao.getUserByEmail(email) else null

        if (existingByUsername != null || existingByPhone != null || existingByEmail != null) {
            return@withContext AuthResponse(
                success = false,
                message = "এই ইউজারনেম, ফোন বা ইমেইল দিয়ে ইতোমধ্যে অ্যাকাউন্ট রয়েছে!"
            )
        }

        // Generate unique referral code (e.g. TAN8492 or KLQ5821)
        val cleanName = fullName.replace(Regex("[^a-zA-Z]"), "")
        val prefix = if (cleanName.length >= 3) {
            cleanName.substring(0, 3).uppercase()
        } else {
            "KLQ"
        }
        val myRefCode = prefix + (1000..9999).random()

        // Profile Photo Processing
        val defaultAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80"
        val finalAvatarUrl = if (request.profilePhotoUrl.isNotBlank()) request.profilePhotoUrl else defaultAvatar

        // Address resolution
        val pres = request.presentAddress ?: AddressData()
        val perm = if (request.sameAddress) pres else (request.permanentAddress ?: pres)

        val newUserId = "usr_" + UUID.randomUUID().toString().take(12)

        val newUser = UserEntity(
            id = newUserId,
            fullName = fullName,
            username = username,
            phone = phone,
            email = if (email.isNotBlank()) email else null,
            avatarUrl = finalAvatarUrl,
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
            bio = request.bio.ifBlank { "🚀 KLIQ ক্রিয়েটর ও আর্নিং মেম্বার" },
            jobInfo = request.jobInfo.ifBlank { "ডিজিটাল ক্রিয়েটর" },
            university = request.university.ifBlank { "ঢাকা বিশ্ববিদ্যালয়" },
            college = request.college,
            school = request.school,
            addressDivision = pres.division,
            addressDistrict = pres.district,
            addressUpazila = pres.upazila ?: "মিরপুর",
            unionCity = pres.unionCity,
            villageWard = pres.villageWard,
            addressDetails = pres.details,
            permanentDivision = perm.division,
            permanentDistrict = perm.district,
            permanentUpazila = perm.upazila,
            permanentUnionCity = perm.unionCity,
            permanentVillageWard = perm.villageWard,
            sameAddress = request.sameAddress,
            dob = request.dob,
            gender = request.gender ?: "male",
            interests = if (request.interests.isNotEmpty()) request.interests.joinToString(", ") else "প্রযুক্তি, স্কিল ডেভেলপমেন্ট",
            userType = request.userType,
            myReferralCode = myRefCode,
            referredByCode = request.refCode.ifBlank { null },
            contactSyncEnabled = request.contactSyncEnabled,
            acceptedTerms = request.acceptedTerms,
            userLevel = "Starter",
            specialBadge = "Member",
            isVerified = false,
            trustScore = 5.00
        )

        // 1. Insert user into Room database
        userDao.insertUser(newUser)

        // 2. Initialize Wallet with ৳50 Signup Bonus
        val initialWallet = WalletEntity(
            userId = newUserId,
            cashBalance = 50.00,
            coinBalance = 0,
            escrowLocked = 0.0,
            totalEarned = 50.00,
            totalWithdrawn = 0.0,
            updatedAt = System.currentTimeMillis()
        )
        walletDao.insertWallet(initialWallet)

        // 3. Record Bonus Transaction
        val bonusTransaction = WalletTransactionEntity(
            userId = newUserId,
            type = "signup_bonus",
            amount = 50.00,
            direction = "in",
            method = "Signup Reward",
            title = "অ্যাকাউন্ট খোলার ৫০ টাকা বোনাস",
            referenceId = "BONUS_${myRefCode}",
            status = "completed"
        )
        walletDao.insertTransaction(bonusTransaction)

        // 4. Sync to Cloud Firestore asynchronously
        try {
            firestoreRepo.saveUserProfile(newUser)
        } catch (e: Exception) {
            Log.w(TAG, "Notice syncing new user to Firestore: ${e.message}")
        }

        AuthResponse(
            success = true,
            message = "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে এবং ৫০ টাকা বোনাস যোগ হয়েছে!",
            user = AuthUserData(
                id = newUserId,
                fullName = fullName,
                username = username,
                phone = phone,
                email = email,
                balance = 50.00,
                coin = 0,
                profilePhotoUrl = finalAvatarUrl,
                myReferralCode = myRefCode
            )
        )
    }

    /**
     * User Login (matches api_auth.php action=login)
     */
    suspend fun loginUser(request: LoginRequest): AuthResponse = withContext(Dispatchers.IO) {
        val identifier = request.identifier.trim()
        val password = request.password

        if (identifier.isBlank() || password.isBlank()) {
            return@withContext AuthResponse(
                success = false,
                message = "ইউজারনেম/মোবাইল এবং পাসওয়ার্ড প্রদান করুন!"
            )
        }

        // Try remote API if configured
        if (authApiService != null) {
            try {
                val response = authApiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Remote login fallback: ${e.message}")
            }
        }

        // Find in local database by username, phone, or email
        val user = userDao.getUserByIdentifier(identifier)
        if (user != null) {
            val wallet = walletDao.getWallet(user.id)
            return@withContext AuthResponse(
                success = true,
                message = "লগইন সফল হয়েছে!",
                user = AuthUserData(
                    id = user.id,
                    fullName = user.fullName,
                    username = user.username,
                    phone = user.phone,
                    email = user.email,
                    balance = wallet?.cashBalance ?: 50.00,
                    coin = wallet?.coinBalance ?: 0,
                    profilePhotoUrl = user.avatarUrl,
                    myReferralCode = user.myReferralCode
                )
            )
        }

        // Default demo account fallback for immediate testing
        if (identifier == "+8801711223344" || identifier == "tanvir_kliq" || identifier == "tanvir@kliq.app") {
            val masterUser = userDao.getUserById("usr_kliq_master_01")
            val wallet = walletDao.getWallet("usr_kliq_master_01")
            return@withContext AuthResponse(
                success = true,
                message = "লগইন সফল হয়েছে!",
                user = AuthUserData(
                    id = "usr_kliq_master_01",
                    fullName = masterUser?.fullName ?: "Tanvir Ahmed",
                    username = masterUser?.username ?: "tanvir_kliq",
                    phone = "+8801711223344",
                    email = "tanvir@kliq.app",
                    balance = wallet?.cashBalance ?: 450.00,
                    coin = wallet?.coinBalance ?: 1250,
                    profilePhotoUrl = masterUser?.avatarUrl,
                    myReferralCode = "TAN8492"
                )
            )
        }

        return@withContext AuthResponse(
            success = false,
            message = "ভুল ইউজারনেম/মোবাইল বা পাসওয়ার্ড!"
        )
    }
}
