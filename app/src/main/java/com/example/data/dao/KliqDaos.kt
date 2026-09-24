package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :identifier OR phone = :identifier OR email = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE myReferralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>

    @Query("DELETE FROM users")
    suspend fun clearAll()
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE circleType = :circleType ORDER BY createdAt DESC")
    fun getPostsByCircle(circleType: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSavedFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: String): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET likesCount = likesCount + 1, isLikedByMe = 1 WHERE id = :postId")
    suspend fun likePost(postId: String)

    @Query("UPDATE posts SET likesCount = CASE WHEN likesCount > 0 THEN likesCount - 1 ELSE 0 END, isLikedByMe = 0 WHERE id = :postId")
    suspend fun unlikePost(postId: String)

    @Query("UPDATE posts SET isSavedFavorite = :isFav WHERE id = :postId")
    suspend fun toggleFavorite(postId: String, isFav: Boolean)

    @Query("UPDATE posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementComments(postId: String)

    @Query("SELECT * FROM post_comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: String): Flow<List<PostCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: PostCommentEntity)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY createdAt DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE id = :id LIMIT 1")
    suspend fun getReelById(id: String): ReelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity)

    @Update
    suspend fun updateReel(reel: ReelEntity)

    @Query("UPDATE reels SET likesCount = likesCount + 1, isLikedByMe = 1 WHERE id = :reelId")
    suspend fun likeReel(reelId: String)

    @Query("UPDATE reels SET likesCount = CASE WHEN likesCount > 0 THEN likesCount - 1 ELSE 0 END, isLikedByMe = 0 WHERE id = :reelId")
    suspend fun unlikeReel(reelId: String)

    @Query("UPDATE reels SET isFavoritedByMe = :isFav WHERE id = :reelId")
    suspend fun toggleReelFavorite(reelId: String, isFav: Boolean)

    @Query("UPDATE reels SET isFollowingCreator = :follow WHERE id = :reelId")
    suspend fun toggleFollowCreator(reelId: String, follow: Boolean)

    @Query("UPDATE reels SET tipsTotal = tipsTotal + :tipAmount WHERE id = :reelId")
    suspend fun addTipToReel(reelId: String, tipAmount: Double)

    @Query("DELETE FROM reels")
    suspend fun clearAll()
}

@Dao
interface ScoutGigDao {
    @Query("SELECT * FROM scout_gigs ORDER BY createdAt DESC")
    fun getAllGigs(): Flow<List<ScoutGigEntity>>

    @Query("SELECT * FROM scout_gigs WHERE category = :category ORDER BY createdAt DESC")
    fun getGigsByCategory(category: String): Flow<List<ScoutGigEntity>>

    @Query("SELECT * FROM scout_gigs WHERE id = :id LIMIT 1")
    suspend fun getGigById(id: String): ScoutGigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGigs(gigs: List<ScoutGigEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGig(gig: ScoutGigEntity)

    @Update
    suspend fun updateGig(gig: ScoutGigEntity)

    @Query("DELETE FROM scout_gigs WHERE id = :id")
    suspend fun deleteGigById(id: String)

    @Query("DELETE FROM scout_gigs")
    suspend fun clearAll()
}

@Dao
interface SkillSessionDao {
    @Query("SELECT * FROM skill_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<SkillSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SkillSessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SkillSessionEntity)

    @Query("DELETE FROM skill_sessions")
    suspend fun clearAll()
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    fun getWalletFlow(userId: String): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    suspend fun getWallet(userId: String): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsFlow(userId: String): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransactionEntity)

    @Query("DELETE FROM wallets")
    suspend fun clearWallets()

    @Query("DELETE FROM wallet_transactions")
    suspend fun clearTransactions()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conv: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(convs: List<ConversationEntity>)

    @Update
    suspend fun updateConversation(conv: ConversationEntity)

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY createdAt ASC")
    fun getMessagesFlow(convId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(msgs: List<MessageEntity>)

    @Query("UPDATE messages SET status = 'read' WHERE conversationId = :convId AND status != 'read'")
    suspend fun markMessagesAsRead(convId: String)

    @Query("UPDATE messages SET escrowOfferStatus = :status WHERE id = :msgId")
    suspend fun updateEscrowStatus(msgId: String, status: String)

    @Query("DELETE FROM conversations")
    suspend fun clearConversations()

    @Query("DELETE FROM messages")
    suspend fun clearMessages()
}

@Dao
interface ContactsDao {
    @Query("SELECT * FROM user_phone_contacts WHERE userId = :userId ORDER BY contactName ASC")
    fun getContactsFlow(userId: String): Flow<List<UserPhoneContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<UserPhoneContactEntity>)

    @Update
    suspend fun updateContact(contact: UserPhoneContactEntity)

    @Query("DELETE FROM user_phone_contacts")
    suspend fun clearAll()
}

@Dao
interface UserTaskDao {
    @Query("SELECT * FROM user_tasks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTasksFlow(userId: String): Flow<List<UserTaskEntity>>

    @Query("SELECT * FROM user_tasks WHERE userId = :userId AND isCompleted = :completed ORDER BY createdAt DESC")
    fun getTasksByStatusFlow(userId: String, completed: Boolean): Flow<List<UserTaskEntity>>

    @Query("SELECT * FROM user_tasks WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getTasksByCategoryFlow(userId: String, category: String): Flow<List<UserTaskEntity>>

    @Query("SELECT * FROM user_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): UserTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: UserTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<UserTaskEntity>)

    @Update
    suspend fun updateTask(task: UserTaskEntity)

    @Query("UPDATE user_tasks SET isCompleted = :completed, status = CASE WHEN :completed = 1 THEN 'completed' ELSE 'pending' END, completedAt = CASE WHEN :completed = 1 THEN :time ELSE NULL END WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: String, completed: Boolean, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM user_tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("DELETE FROM user_tasks WHERE userId = :userId")
    suspend fun clearUserTasks(userId: String)
}

@Dao
interface SocialInteractionDao {
    @Query("SELECT * FROM social_interactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserInteractionsFlow(userId: String): Flow<List<SocialInteractionEntity>>

    @Query("SELECT * FROM social_interactions WHERE targetType = :targetType AND targetId = :targetId ORDER BY createdAt DESC")
    fun getInteractionsForTargetFlow(targetType: String, targetId: String): Flow<List<SocialInteractionEntity>>

    @Query("SELECT * FROM social_interactions WHERE userId = :userId AND targetId = :targetId AND interactionType = :type LIMIT 1")
    suspend fun getSpecificInteraction(userId: String, targetId: String, type: String): SocialInteractionEntity?

    @Query("SELECT COUNT(*) FROM social_interactions WHERE targetId = :targetId AND interactionType = :type")
    fun countInteractionsFlow(targetId: String, type: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordInteraction(interaction: SocialInteractionEntity)

    @Query("DELETE FROM social_interactions WHERE userId = :userId AND targetId = :targetId AND interactionType = :type")
    suspend fun removeInteraction(userId: String, targetId: String, type: String)

    @Query("DELETE FROM social_interactions WHERE userId = :userId")
    suspend fun clearUserInteractions(userId: String)
}

