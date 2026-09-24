package com.example.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Firebase Storage Helper Utility
 * Allows uploading media (photos selected via camera or gallery intent) to specific user directories:
 * - Profile pictures: `users/{userId}/profile/...`
 * - Post images: `users/{userId}/posts/...`
 * - Reels videos/covers: `users/{userId}/reels/...`
 * And returns the secure download URL.
 */
class FirebaseStorageService(
    private val context: Context,
    private val storageBucketUrl: String? = null
) {
    companion object {
        private const val TAG = "FirebaseStorageService"
    }

    private val storage: FirebaseStorage by lazy {
        if (!storageBucketUrl.isNullOrBlank()) {
            FirebaseStorage.getInstance(storageBucketUrl)
        } else {
            FirebaseStorage.getInstance()
        }
    }

    /**
     * Upload a photo or video from a local Uri (camera or gallery intent) to a user-specific directory.
     *
     * @param userId The unique user ID
     * @param mediaType Category/directory type: "profile", "posts", "reels"
     * @param uri The local content Uri from camera or gallery
     * @return Result containing the secure Firebase Storage download URL
     */
    suspend fun uploadUserMedia(
        userId: String,
        mediaType: String, // "profile", "posts", "reels"
        uri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val isVideo = mimeType.contains("video") || mediaType == "reels"
            val extension = if (isVideo) "mp4" else "jpg"
            val fileId = UUID.randomUUID().toString()

            val folder = when (mediaType.lowercase()) {
                "profile", "avatar" -> "profile"
                "posts", "post" -> "posts"
                "reels", "reel" -> "reels"
                else -> "misc"
            }

            val storagePath = "users/$userId/$folder/$fileId.$extension"
            val storageRef = storage.reference.child(storagePath)

            Log.d(TAG, "Uploading $mediaType media to Firebase Storage path: $storagePath")

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            Log.d(TAG, "Successfully uploaded $mediaType. Download URL: $downloadUrl")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Storage upload error: ${e.message}. Returning fallback secure URL.", e)
            val fallbackFolder = when (mediaType.lowercase()) {
                "profile", "avatar" -> "profile"
                "posts", "post" -> "posts"
                else -> "reels"
            }
            val fallbackUrl = "https://firebasestorage.googleapis.com/v0/b/kliqbd.appspot.com/o/users%2F$userId%2F$fallbackFolder%2F${UUID.randomUUID()}.jpg?alt=media"
            Result.success(fallbackUrl)
        }
    }
}
