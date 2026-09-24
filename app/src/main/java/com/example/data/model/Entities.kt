package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val username: String,
    val phone: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val coverUrl: String? = null,
    val bio: String = "",
    val jobInfo: String = "Digital Creator",
    val university: String = "Dhaka University",
    val college: String? = null,
    val school: String? = null,
    val addressDivision: String = "ঢাকা",
    val addressDistrict: String = "Dhaka",
    val addressUpazila: String = "Dhanmondi",
    val unionCity: String? = null,
    val villageWard: String? = null,
    val addressDetails: String? = null,
    val permanentDivision: String? = null,
    val permanentDistrict: String? = null,
    val permanentUpazila: String? = null,
    val permanentUnionCity: String? = null,
    val permanentVillageWard: String? = null,
    val sameAddress: Boolean = true,
    val dob: String? = null,
    val gender: String? = null,
    val interests: String? = null,
    val userType: String = "ছাত্র / শিক্ষার্থী",
    val myReferralCode: String = "KLQ" + (1000..9999).random(),
    val referredByCode: String? = null,
    val contactSyncEnabled: Boolean = true,
    val acceptedTerms: Boolean = true,
    val whatsapp: String? = null,
    val userLevel: String = "Starter", // Starter, Scout Leader, Pro Earner, Elite Freelancer
    val specialBadge: String = "VVIP",
    val isVerified: Boolean = false,
    val trustScore: Double = 5.00,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_phone_contacts")
data class UserPhoneContactEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val contactName: String,
    val contactPhone: String,
    val matchedUserId: String? = null,
    val isConnected: Boolean = false,
    val syncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String? = null,
    val isAuthorVerified: Boolean = false,
    val authorBadge: String = "VVIP",
    val title: String? = null,
    val content: String,
    val mediaUrl: String? = null,
    val mediaType: String = "none", // none, image, video
    val visibility: String = "public", // public, circle, private
    val circleType: String? = null, // contact, school, varsity, college, area
    val circleName: String? = null,
    val unionName: String? = null,
    val villageName: String? = null,
    val isShared: Boolean = false,
    val originalPostId: String? = null,
    val originalAuthorName: String? = null,
    val originalContent: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isSavedFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "post_comments")
data class PostCommentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String? = null,
    val isVerified: Boolean = false,
    val commentText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val creatorId: String,
    val creatorName: String,
    val creatorUsername: String,
    val creatorAvatarUrl: String? = null,
    val isCreatorVerified: Boolean = false,
    val caption: String,
    val videoUrl: String,
    val posterUrl: String? = null,
    val audioTitle: String = "Original Audio - KLIQ Creator",
    val musicAuthor: String = "KLIQ Sounds",
    val hashtags: String = "#KLIQ #Viral #BanglaTech",
    val likesCount: Int = 128,
    val commentsCount: Int = 42,
    val sharesCount: Int = 19,
    val tipsTotal: Double = 0.0,
    val isLikedByMe: Boolean = false,
    val isFavoritedByMe: Boolean = false,
    val isFollowingCreator: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scout_gigs")
data class ScoutGigEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String? = null,
    val title: String,
    val description: String,
    val category: String, // Shop & Price Audit, Local Verification, Photo & Proof, Stock Check, Survey
    val location: String,
    val rewardCash: Double,
    val escrowStatus: String = "locked", // locked, released, refunded
    val remainingHours: Int = 6,
    val claimedBy: String? = null,
    val claimedByName: String? = null,
    val status: String = "open", // open, in_progress, completed, cancelled
    val proofImageUrl: String? = null,
    val proofNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "skill_sessions")
data class SkillSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val instructorId: String,
    val instructorName: String,
    val instructorAvatar: String? = null,
    val instructorRating: Double = 5.0,
    val reviewsCount: Int = 24,
    val title: String,
    val category: String, // Photography, Content Creation, Market Research, Graphic Design
    val fee: Double = 0.0,
    val sessionType: String = "1-on-1 Mentorship", // 1-on-1 Mentorship, Live Workshop, Video Course
    val description: String,
    val topics: String = "Fundamentals, Monetization, Live Case Studies",
    val status: String = "active", // active, paused
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val userId: String,
    val cashBalance: Double = 50.00, // ৳৫০ Instant Signup Bonus
    val coinBalance: Int = 200, // Free engagement coins
    val escrowLocked: Double = 0.00,
    val totalEarned: Double = 50.00,
    val totalWithdrawn: Double = 0.00,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: String, // signup_bonus, scout_reward, skill_sale, creator_tip, cashout, coin_convert
    val amount: Double,
    val direction: String, // in, out
    val method: String, // bKash, Nagad, Rocket, Escrow Release, Coin Exchange, Internal Tip
    val accountNumber: String? = null,
    val status: String = "completed", // completed, pending, rejected
    val referenceId: String? = null,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val participantOne: String,
    val participantTwo: String,
    val participantName: String,
    val participantAvatar: String? = null,
    val isVerified: Boolean = false,
    val lastMessage: String? = null,
    val unreadCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val messageText: String,
    val attachmentUrl: String? = null,
    val status: String = "sent", // sent, delivered, read
    val isEscrowContract: Boolean = false,
    val escrowOfferAmount: Double? = null,
    val escrowOfferStatus: String? = null, // pending, locked, released
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * User Task Entity
 * Stores user-created tasks, scout gigs/micro-tasks, to-dos, and assignments locally.
 */
@Entity(tableName = "user_tasks")
data class UserTaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val description: String = "",
    val category: String = "General", // General, Scout Gig, Social Bounty, Review, Verification, Delivery
    val priority: String = "Medium", // Low, Medium, High, Urgent
    val status: String = "pending", // pending, in_progress, completed, cancelled
    val rewardAmount: Double = 0.0,
    val rewardType: String = "BDT", // BDT, Coins, Points
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val proofUrl: String? = null,
    val proofNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

/**
 * Social Interaction Entity
 * Tracks likes, bookmarks, shares, tips, follows, and connections locally.
 */
@Entity(tableName = "social_interactions")
data class SocialInteractionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val targetType: String, // post, reel, user, gig, comment
    val targetId: String,
    val interactionType: String, // like, bookmark, share, tip, follow, view
    val value: Double = 0.0, // Tip amount or engagement weight if applicable
    val extraMetadata: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

