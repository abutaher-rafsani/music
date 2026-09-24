package com.example.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.example.data.KliqDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class KliqRepository(private val context: Context) {
    private val db = KliqDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val postDao = db.postDao()
    private val reelDao = db.reelDao()
    private val scoutDao = db.scoutGigDao()
    private val skillDao = db.skillSessionDao()
    private val walletDao = db.walletDao()
    private val chatDao = db.chatDao()
    private val contactsDao = db.contactsDao()
    private val userTaskDao = db.userTaskDao()
    private val socialInteractionDao = db.socialInteractionDao()

    // Firestore Data Repository for Cloud Profiles and Gigs
    val firestoreRepository = FirestoreDataRepository(context, db)

    // Multi-Step Authentication & Profile Repository (api_auth.php)
    val authRepository = AuthRepository(context, db, firestoreRepository)

    // Current user ID
    var currentUserId = "usr_kliq_master_01"

    suspend fun ensureInitialized() {
        // Real empty database installation - no mock data seeded
    }

    suspend fun resetAllData() {
        db.withTransaction {
            userDao.clearAll()
            postDao.clearAll()
            reelDao.clearAll()
            scoutDao.clearAll()
            skillDao.clearAll()
            walletDao.clearWallets()
            walletDao.clearTransactions()
            chatDao.clearConversations()
            chatDao.clearMessages()
            contactsDao.clearAll()
            userTaskDao.clearUserTasks(currentUserId)
            socialInteractionDao.clearUserInteractions(currentUserId)
        }
    }

    private suspend fun seedInitialData() {
        // 1. Current Master User
        val masterUser = UserEntity(
            id = currentUserId,
            fullName = "Tanvir Ahmed",
            username = "tanvir_kliq",
            phone = "+8801711223344",
            email = "tanvir@kliq.app",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
            bio = "🚀 Building the next-gen creator gig economy on KLIQ. Tech Enthusiast & Digital Scout Leader.",
            jobInfo = "Full-Stack Dev & Scout Lead",
            university = "Dhaka University (CSE)",
            addressDistrict = "Dhaka",
            addressUpazila = "Dhanmondi",
            whatsapp = "+8801711223344",
            userLevel = "Scout Leader",
            specialBadge = "VVIP",
            isVerified = true,
            trustScore = 5.00
        )
        userDao.insertUser(masterUser)

        // Seed peer users
        val peers = listOf(
            UserEntity(
                id = "usr_nabil",
                fullName = "Nabil Rahman",
                username = "nabil_dev",
                phone = "+8801819998877",
                email = "nabil@gmail.com",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                bio = "UI/UX Designer & Mobile Architect",
                jobInfo = "Senior Designer",
                university = "BUET",
                addressDistrict = "Dhaka",
                addressUpazila = "Gulshan",
                userLevel = "Elite Freelancer",
                specialBadge = "PRO",
                isVerified = true
            ),
            UserEntity(
                id = "usr_samia",
                fullName = "Samia Khan",
                username = "samia_creator",
                phone = "+8801912345678",
                email = "samia@kliq.me",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                bio = "Content Creator | Scout Bounty Hunter",
                jobInfo = "Digital Marketer",
                university = "North South University",
                addressDistrict = "Dhaka",
                addressUpazila = "Banani",
                userLevel = "Pro Earner",
                specialBadge = "VERIFIED",
                isVerified = true
            ),
            UserEntity(
                id = "usr_arif",
                fullName = "Arif Hossain",
                username = "arif_scout",
                phone = "+8801671122445",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                bio = "Field Auditor & Mystery Shopper in Dhaka Metro",
                jobInfo = "Local Scout",
                university = "Jahangirnagar University",
                addressDistrict = "Dhaka",
                addressUpazila = "Mirpur",
                userLevel = "Starter",
                isVerified = false
            )
        )
        peers.forEach { userDao.insertUser(it) }

        // 2. Initial Wallet with ৳৫০ Signup Bonus and 200 Free coins
        val wallet = WalletEntity(
            userId = currentUserId,
            cashBalance = 2450.00,
            coinBalance = 650,
            escrowLocked = 350.00,
            totalEarned = 2800.00,
            totalWithdrawn = 0.00
        )
        walletDao.insertWallet(wallet)

        // Seed wallet transactions
        val txs = listOf(
            WalletTransactionEntity(
                userId = currentUserId,
                type = "signup_bonus",
                amount = 50.00,
                direction = "in",
                method = "bKash",
                status = "completed",
                referenceId = "TXN_BONUS_001",
                title = "৳৫০ ইনস্ট্যান্ট সাইনআপ বোনাস (Welcome Bonus)",
                createdAt = System.currentTimeMillis() - 86400000L * 3
            ),
            WalletTransactionEntity(
                userId = currentUserId,
                type = "scout_reward",
                amount = 350.00,
                direction = "in",
                method = "Escrow Release",
                status = "completed",
                referenceId = "SC_AUDIT_DHAKA_78",
                title = "Scout Reward: Dhanmondi Pharmacy Stock Audit",
                createdAt = System.currentTimeMillis() - 86400000L * 2
            ),
            WalletTransactionEntity(
                userId = currentUserId,
                type = "skill_sale",
                amount = 2050.00,
                direction = "in",
                method = "bKash",
                status = "completed",
                referenceId = "SKILL_SESSION_99",
                title = "Mentorship Fee: Mobile App Design 1-on-1",
                createdAt = System.currentTimeMillis() - 86400000L * 1
            )
        )
        txs.forEach { walletDao.insertTransaction(it) }

        // 3. Posts
        val posts = listOf(
            PostEntity(
                id = "post_01",
                authorId = "usr_samia",
                authorName = "Samia Khan",
                authorUsername = "samia_creator",
                authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                isAuthorVerified = true,
                authorBadge = "VVIP",
                title = "Just completed the Dhanmondi Superstore Audit! 🛒",
                content = "Scout gigs on KLIQ are insane! Verified prices of 15 essential items in under 20 minutes and earned ৳250 directly into my bKash wallet. The escrow release was instant! 💸✨",
                mediaUrl = "https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=800&auto=format&fit=crop&q=80",
                mediaType = "image",
                visibility = "public",
                circleType = "area",
                circleName = "Dhanmondi Ward 15",
                unionName = "Dhaka South",
                villageName = "Dhanmondi R/A",
                likesCount = 84,
                commentsCount = 16,
                sharesCount = 5,
                createdAt = System.currentTimeMillis() - 3600000L * 2
            ),
            PostEntity(
                id = "post_02",
                authorId = "usr_nabil",
                authorName = "Nabil Rahman",
                authorUsername = "nabil_dev",
                authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                isAuthorVerified = true,
                authorBadge = "PRO",
                title = "Obsidian Glassmorphic Design Token System Released",
                content = "Crafted a deep obsidian dark UI tokens for KLIQ! Electric Cyan CTAs paired with metallic silver typography and zero noise. High contrast, ultra responsive. What do you guys think?",
                mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
                mediaType = "image",
                visibility = "circle",
                circleType = "varsity",
                circleName = "BUET Alumni",
                likesCount = 142,
                commentsCount = 38,
                sharesCount = 12,
                createdAt = System.currentTimeMillis() - 3600000L * 5
            ),
            PostEntity(
                id = "post_03",
                authorId = currentUserId,
                authorName = "Tanvir Ahmed",
                authorUsername = "tanvir_kliq",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                isAuthorVerified = true,
                authorBadge = "VVIP",
                title = "Welcome to KLIQ Ecosystem 🇧🇩",
                content = "Connect with university classmates, discover high-paying scout verification bounties in your locality, and cashout instantly to bKash, Nagad or Rocket!",
                mediaUrl = "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=800&auto=format&fit=crop&q=80",
                mediaType = "image",
                visibility = "public",
                circleType = "school",
                circleName = "Dhaka College Alumni",
                likesCount = 230,
                commentsCount = 49,
                sharesCount = 28,
                createdAt = System.currentTimeMillis() - 3600000L * 12
            )
        )
        postDao.insertPosts(posts)

        // Post comments
        val comments = listOf(
            PostCommentEntity(
                postId = "post_01",
                userId = "usr_nabil",
                userName = "Nabil Rahman",
                userAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                isVerified = true,
                commentText = "Great audit! Did you check the digital price tags as well?"
            ),
            PostCommentEntity(
                postId = "post_01",
                userId = "usr_samia",
                userName = "Samia Khan",
                userAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                isVerified = true,
                commentText = "Yes! Submitted photo proof of shelf labels. Got verified in 10 mins."
            )
        )
        comments.forEach { postDao.insertComment(it) }

        // 4. Reels (Vertical Video Hub)
        val reels = listOf(
            ReelEntity(
                id = "reel_101",
                creatorId = "usr_samia",
                creatorName = "Samia Khan",
                creatorUsername = "samia_creator",
                creatorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                isCreatorVerified = true,
                caption = "How I made ৳1,200 today doing 3 simple shop audits in Gulshan! 🔥 #KLIQ #ScoutGig #EarnFromHome",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-young-woman-skater-showing-a-trick-41551-large.mp4",
                posterUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop&q=80",
                audioTitle = "KLIQ Beat Drop - Dhaka Vibes",
                musicAuthor = "KLIQ Sounds Official",
                hashtags = "#KLIQ #ScoutGigs #DhakaEarnings #StudentLife",
                likesCount = 542,
                commentsCount = 89,
                sharesCount = 64,
                tipsTotal = 250.0
            ),
            ReelEntity(
                id = "reel_102",
                creatorId = "usr_nabil",
                creatorName = "Nabil Rahman",
                creatorUsername = "nabil_dev",
                creatorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                isCreatorVerified = true,
                caption = "Obsidian Glassmorphic UI in Action. 60fps buttery smooth gestures on Android Compose! ⚡ #TechBD #DevLife",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hands-of-a-man-typing-on-a-computer-keyboard-41334-large.mp4",
                posterUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=80",
                audioTitle = "Cyberpunk Synthwave Vol 2",
                musicAuthor = "Neon City",
                hashtags = "#AndroidDev #JetpackCompose #Fintech #UIUX",
                likesCount = 890,
                commentsCount = 120,
                sharesCount = 95,
                tipsTotal = 400.0
            ),
            ReelEntity(
                id = "reel_103",
                creatorId = "usr_arif",
                creatorName = "Arif Hossain",
                creatorUsername = "arif_scout",
                creatorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                isCreatorVerified = false,
                caption = "Local verification in Mirpur 10 completed! Don't forget to enable GPS before capturing proof. 📍 #KLIQAudit",
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-man-walking-down-the-street-using-his-cell-phone-41481-large.mp4",
                posterUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=800&auto=format&fit=crop&q=80",
                audioTitle = "Urban Grooves Bangladesh",
                musicAuthor = "Deshi HipHop",
                hashtags = "#Mirpur #Verification #GigEconomy",
                likesCount = 210,
                commentsCount = 35,
                sharesCount = 18,
                tipsTotal = 50.0
            )
        )
        reelDao.insertReels(reels)

        // 5. Scout Gigs & Micro-Tasks
        val scoutGigs = listOf(
            ScoutGigEntity(
                id = "gig_scout_01",
                creatorId = "usr_nabil",
                creatorName = "Nabil Brands Ltd",
                creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                title = "Shop & Price Audit: Agora Superstore Dhanmondi 27",
                description = "Visit Agora Superstore on Road 27. Take 3 clear photos of beverage shelf prices (Coca-Cola, Sprite, Kinley Water) and record the promotional shelf tag discounts.",
                category = "Shop & Price Audit",
                location = "Road 27, Dhanmondi, Dhaka",
                rewardCash = 180.00,
                escrowStatus = "locked",
                remainingHours = 5,
                status = "open"
            ),
            ScoutGigEntity(
                id = "gig_scout_02",
                creatorId = "usr_samia",
                creatorName = "Samia Commerce",
                creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                title = "Local Verification: Check Business Signboard in Banani 11",
                description = "Verify if 'Apex Tech Hub' office exists at House 45, Road 11, Banani. Snap front entrance board with timestamp proof.",
                category = "Local Verification",
                location = "Road 11, Banani, Dhaka",
                rewardCash = 250.00,
                escrowStatus = "locked",
                remainingHours = 4,
                status = "open"
            ),
            ScoutGigEntity(
                id = "gig_scout_03",
                creatorId = "usr_arif",
                creatorName = "Pharma Retail Audit",
                creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                title = "Stock & Expiry Check: Lazz Pharma Mirpur",
                description = "Check presence of Baby Food Brand X at Mirpur 2 branch. Upload 2 photos of expiration date labels.",
                category = "Stock Check",
                location = "Mirpur 2, Dhaka",
                rewardCash = 150.00,
                escrowStatus = "locked",
                remainingHours = 8,
                status = "open"
            ),
            ScoutGigEntity(
                id = "gig_scout_04",
                creatorId = currentUserId,
                creatorName = "Tanvir Ahmed",
                creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                title = "Survey: University Cafeteria Digital Payment Adoption",
                description = "Survey 5 students regarding mobile payment (bKash/Nagad) preferences at DU TSC area.",
                category = "Survey",
                location = "TSC, Dhaka University",
                rewardCash = 300.00,
                escrowStatus = "locked",
                remainingHours = 12,
                status = "open"
            )
        )
        scoutDao.insertGigs(scoutGigs)

        // 6. Skill Sessions & Mentorship
        val skillSessions = listOf(
            SkillSessionEntity(
                id = "skill_01",
                instructorId = "usr_nabil",
                instructorName = "Nabil Rahman",
                instructorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                instructorRating = 5.0,
                reviewsCount = 48,
                title = "Mastering Figma to Mobile UI Architecture",
                category = "Graphic Design",
                fee = 450.00,
                sessionType = "1-on-1 Mentorship",
                description = "Comprehensive 1-on-1 walkthrough of auto-layout, design tokens, component architecture and handover to Android Compose.",
                topics = "Design Tokens, Dark Mode System, Component Variants, Handover Specs"
            ),
            SkillSessionEntity(
                id = "skill_02",
                instructorId = "usr_samia",
                instructorName = "Samia Khan",
                instructorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                instructorRating = 4.9,
                reviewsCount = 62,
                title = "Short Video Mastery & Monetization on Reels",
                category = "Content Creation",
                fee = 350.00,
                sessionType = "Live Workshop",
                description = "Learn scriptwriting, hook mechanics, lighting with smartphone, and how to attract brand deals in Bangladesh.",
                topics = "Viral Hooks, CapCut Secrets, Brand Pitching, Audio Sync"
            ),
            SkillSessionEntity(
                id = "skill_03",
                instructorId = currentUserId,
                instructorName = "Tanvir Ahmed",
                instructorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                instructorRating = 5.0,
                reviewsCount = 31,
                title = "Micro-Task Scouting & Field Bounty Hunting Strategies",
                category = "Market Research",
                fee = 200.00,
                sessionType = "1-on-1 Mentorship",
                description = "Optimize your route, capture audit-grade proof, and scale your daily earnings from ৳500 to ৳3000/day.",
                topics = "Route Planning, Proof Validation, Dispute Handling, Fast Cashouts"
            )
        )
        skillDao.insertSessions(skillSessions)

        // 7. Phonebook Contacts
        val contacts = listOf(
            UserPhoneContactEntity(
                userId = currentUserId,
                contactName = "Nabil Rahman",
                contactPhone = "01819998877",
                matchedUserId = "usr_nabil",
                isConnected = true
            ),
            UserPhoneContactEntity(
                userId = currentUserId,
                contactName = "Samia Khan",
                contactPhone = "01912345678",
                matchedUserId = "usr_samia",
                isConnected = true
            ),
            UserPhoneContactEntity(
                userId = currentUserId,
                contactName = "Arif Hossain",
                contactPhone = "01671122445",
                matchedUserId = "usr_arif",
                isConnected = false
            ),
            UserPhoneContactEntity(
                userId = currentUserId,
                contactName = "Kazi Fahim (Campus)",
                contactPhone = "01755667788",
                matchedUserId = null,
                isConnected = false
            ),
            UserPhoneContactEntity(
                userId = currentUserId,
                contactName = "Sadia Afrin",
                contactPhone = "01300112233",
                matchedUserId = null,
                isConnected = false
            )
        )
        contactsDao.insertContacts(contacts)

        // 8. Conversations & Messages
        val convs = listOf(
            ConversationEntity(
                id = "conv_samia",
                participantOne = currentUserId,
                participantTwo = "usr_samia",
                participantName = "Samia Khan",
                participantAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                isVerified = true,
                lastMessage = "Thanks for releasing the scout bounty! ৳250 credited.",
                unreadCount = 1,
                updatedAt = System.currentTimeMillis() - 1200000L
            ),
            ConversationEntity(
                id = "conv_nabil",
                participantOne = currentUserId,
                participantTwo = "usr_nabil",
                participantName = "Nabil Rahman",
                participantAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                isVerified = true,
                lastMessage = "I locked the ৳450 design contract in escrow. Let me know when ready!",
                unreadCount = 0,
                updatedAt = System.currentTimeMillis() - 7200000L
            )
        )
        chatDao.insertConversations(convs)

        val messages = listOf(
            MessageEntity(
                conversationId = "conv_samia",
                senderId = "usr_samia",
                recipientId = currentUserId,
                messageText = "Hey Tanvir! I just completed the Dhanmondi Superstore audit.",
                status = "read",
                createdAt = System.currentTimeMillis() - 1800000L
            ),
            MessageEntity(
                conversationId = "conv_samia",
                senderId = currentUserId,
                recipientId = "usr_samia",
                messageText = "Saw the proofs, look very crisp and clear!",
                status = "read",
                createdAt = System.currentTimeMillis() - 1500000L
            ),
            MessageEntity(
                conversationId = "conv_samia",
                senderId = "usr_samia",
                recipientId = currentUserId,
                messageText = "Thanks for releasing the scout bounty! ৳250 credited.",
                status = "delivered",
                createdAt = System.currentTimeMillis() - 1200000L
            ),
            MessageEntity(
                conversationId = "conv_nabil",
                senderId = "usr_nabil",
                recipientId = currentUserId,
                messageText = "Here is the custom gig proposal for the obsidian mobile design system.",
                status = "read",
                isEscrowContract = true,
                escrowOfferAmount = 450.00,
                escrowOfferStatus = "locked",
                createdAt = System.currentTimeMillis() - 7200000L
            )
        )
        chatDao.insertMessages(messages)

        // 9. User Tasks (Locally persisted tasks)
        val initialTasks = listOf(
            UserTaskEntity(
                id = "task_01",
                userId = currentUserId,
                title = "Complete Dhanmondi Superstore Price Audit",
                description = "Upload 3 aisle photos and check price tags of fresh produce. Reward in escrow.",
                category = "Scout Gig",
                priority = "High",
                status = "completed",
                rewardAmount = 250.0,
                rewardType = "BDT",
                isCompleted = true,
                proofUrl = "https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=600&auto=format&fit=crop&q=80",
                proofNotes = "All 3 aisles verified and geotagged at Dhanmondi 27.",
                completedAt = System.currentTimeMillis() - 3600000L
            ),
            UserTaskEntity(
                id = "task_02",
                userId = currentUserId,
                title = "Review Mobile Design Tokens & Palette",
                description = "Inspect the updated pure white theme and high contrast typography on Android Compose.",
                category = "Review",
                priority = "Medium",
                status = "in_progress",
                rewardAmount = 50.0,
                rewardType = "Coins",
                isCompleted = false
            ),
            UserTaskEntity(
                id = "task_03",
                userId = currentUserId,
                title = "Engage in BUET & Dhaka Varsity Alumni Circle",
                description = "Share campus study tips and scout opportunities with varsity peers.",
                category = "Social Bounty",
                priority = "Low",
                status = "pending",
                rewardAmount = 20.0,
                rewardType = "Coins",
                isCompleted = false
            )
        )
        userTaskDao.insertTasks(initialTasks)

        // 10. Social Interactions (Locally persisted interactions)
        val initialInteractions = listOf(
            SocialInteractionEntity(
                userId = currentUserId,
                targetType = "post",
                targetId = "post_01",
                interactionType = "like"
            ),
            SocialInteractionEntity(
                userId = currentUserId,
                targetType = "post",
                targetId = "post_02",
                interactionType = "bookmark"
            ),
            SocialInteractionEntity(
                userId = currentUserId,
                targetType = "reel",
                targetId = "reel_01",
                interactionType = "tip",
                value = 50.0,
                extraMetadata = "Instant Creator Tip via bKash escrow"
            )
        )
        initialInteractions.forEach { socialInteractionDao.recordInteraction(it) }

        // Seed to Cloud Firestore asynchronously
        try {
            firestoreRepository.seedInitialProfiles(listOf(masterUser) + peers)
            firestoreRepository.seedInitialGigs(scoutGigs)
        } catch (e: Throwable) {
            // Non-fatal if offline
        }
    }

    // ================= DATA FLOWS =================
    suspend fun checkUsernameAvailability(username: String): com.example.data.remote.CheckUsernameResponse =
        authRepository.checkUsernameAvailability(username)

    suspend fun registerUser(request: com.example.data.remote.RegisterRequest): com.example.data.remote.AuthResponse {
        val response = authRepository.registerUser(request)
        if (response.success && response.user != null) {
            currentUserId = response.user.id.toString()
        }
        return response
    }

    suspend fun loginUser(request: com.example.data.remote.LoginRequest): com.example.data.remote.AuthResponse {
        val response = authRepository.loginUser(request)
        if (response.success && response.user != null) {
            currentUserId = response.user.id.toString()
        }
        return response
    }

    fun getCurrentUserFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    fun getFirestoreUserProfileFlow(userId: String = currentUserId): Flow<UserEntity?> = firestoreRepository.getUserProfileFlow(userId)
    suspend fun getCurrentUser(): UserEntity? = userDao.getUserById(currentUserId)
    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
        firestoreRepository.saveUserProfile(user)
    }

    fun getAllPosts(): Flow<List<PostEntity>> = postDao.getAllPosts()
    fun getPostsByCircle(circle: String): Flow<List<PostEntity>> = postDao.getPostsByCircle(circle)
    fun getFavoritePosts(): Flow<List<PostEntity>> = postDao.getFavoritePosts()

    fun getAllReels(): Flow<List<ReelEntity>> = reelDao.getAllReels()
    fun getAllGigs(): Flow<List<ScoutGigEntity>> = scoutDao.getAllGigs()
    fun getFirestoreGigsFlow(category: String? = null, status: String? = null): Flow<List<ScoutGigEntity>> =
        firestoreRepository.getGigListingsFlow(category, status)
    fun getGigsByCategory(cat: String): Flow<List<ScoutGigEntity>> = scoutDao.getGigsByCategory(cat)

    fun getAllSessions(): Flow<List<SkillSessionEntity>> = skillDao.getAllSessions()

    fun getWalletFlow(): Flow<WalletEntity?> = walletDao.getWalletFlow(currentUserId)
    fun getTransactionsFlow(): Flow<List<WalletTransactionEntity>> = walletDao.getTransactionsFlow(currentUserId)

    fun getConversationsFlow(): Flow<List<ConversationEntity>> = chatDao.getConversationsFlow()
    fun getMessagesFlow(convId: String): Flow<List<MessageEntity>> = chatDao.getMessagesFlow(convId)

    fun getContactsFlow(): Flow<List<UserPhoneContactEntity>> = contactsDao.getContactsFlow(currentUserId)

    // ================= ACTIONS =================
    suspend fun createPost(
        title: String?,
        content: String,
        mediaUrl: String?,
        mediaType: String,
        visibility: String,
        circleType: String?,
        circleName: String?
    ) {
        val user = userDao.getUserById(currentUserId) ?: return
        val post = PostEntity(
            authorId = user.id,
            authorName = user.fullName,
            authorUsername = user.username,
            authorAvatarUrl = user.avatarUrl,
            isAuthorVerified = user.isVerified,
            authorBadge = user.specialBadge,
            title = title,
            content = content,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            visibility = visibility,
            circleType = circleType,
            circleName = circleName,
            createdAt = System.currentTimeMillis()
        )
        postDao.insertPost(post)
    }

    suspend fun toggleLikePost(postId: String, currentLiked: Boolean) {
        if (currentLiked) {
            postDao.unlikePost(postId)
        } else {
            postDao.likePost(postId)
        }
    }

    suspend fun toggleFavoritePost(postId: String, currentFav: Boolean) {
        postDao.toggleFavorite(postId, !currentFav)
    }

    suspend fun addComment(postId: String, text: String) {
        val user = userDao.getUserById(currentUserId) ?: return
        val comment = PostCommentEntity(
            postId = postId,
            userId = user.id,
            userName = user.fullName,
            userAvatarUrl = user.avatarUrl,
            isVerified = user.isVerified,
            commentText = text,
            createdAt = System.currentTimeMillis()
        )
        postDao.insertComment(comment)
        postDao.incrementComments(postId)
    }

    fun getCommentsForPost(postId: String): Flow<List<PostCommentEntity>> = postDao.getCommentsForPost(postId)

    // Tip post author 50 coins
    suspend fun tipPost(postId: String): Boolean {
        return db.withTransaction {
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false
            if (wallet.coinBalance < 50) return@withTransaction false

            val updated = wallet.copy(
                coinBalance = wallet.coinBalance - 50,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updated)

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "creator_tip",
                amount = 0.0,
                direction = "out",
                method = "Internal Tip",
                title = "Tipped 50 Coins to Post #$postId",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
    }

    // Reels actions
    suspend fun toggleLikeReel(reelId: String, currentLiked: Boolean) {
        if (currentLiked) {
            reelDao.unlikeReel(reelId)
        } else {
            reelDao.likeReel(reelId)
        }
    }

    suspend fun toggleReelFavorite(reelId: String, currentFav: Boolean) {
        reelDao.toggleReelFavorite(reelId, !currentFav)
    }

    suspend fun toggleFollowCreator(reelId: String, currentFollowing: Boolean) {
        reelDao.toggleFollowCreator(reelId, !currentFollowing)
    }

    suspend fun tipReelCreator(reelId: String, tipAmount: Double): Boolean {
        return db.withTransaction {
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false
            if (wallet.cashBalance < tipAmount) return@withTransaction false

            val updatedWallet = wallet.copy(
                cashBalance = wallet.cashBalance - tipAmount,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)
            reelDao.addTipToReel(reelId, tipAmount)

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "creator_tip",
                amount = tipAmount,
                direction = "out",
                method = "Internal Tip",
                title = "Gift Tip to Reel Creator: ৳${String.format("%.2f", tipAmount)}",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
    }

    // Claim Scout Gig
    suspend fun claimGig(gigId: String): Boolean {
        val user = userDao.getUserById(currentUserId) ?: return false
        val gig = scoutDao.getGigById(gigId) ?: return false
        if (gig.status != "open") return false

        val updated = gig.copy(
            claimedBy = user.id,
            claimedByName = user.fullName,
            status = "in_progress"
        )
        scoutDao.updateGig(updated)
        firestoreRepository.claimGigListing(gigId, user.id, user.fullName)
        return true
    }

    // Submit Proof & Release Escrow
    suspend fun submitGigProof(gigId: String, proofNotes: String, proofImageUrl: String): Boolean {
        val result = db.withTransaction {
            val gig = scoutDao.getGigById(gigId) ?: return@withTransaction false
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false

            // Update gig to completed
            val updatedGig = gig.copy(
                status = "completed",
                escrowStatus = "released",
                proofNotes = proofNotes,
                proofImageUrl = proofImageUrl
            )
            scoutDao.updateGig(updatedGig)

            // Credit cash balance
            val updatedWallet = wallet.copy(
                cashBalance = wallet.cashBalance + gig.rewardCash,
                totalEarned = wallet.totalEarned + gig.rewardCash,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)

            // Add immutable transaction
            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "scout_reward",
                amount = gig.rewardCash,
                direction = "in",
                method = "Escrow Release",
                referenceId = "GIG_RELEASE_${gig.id.take(8)}",
                title = "Scout Reward Earned: ${gig.title}",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
        if (result) {
            firestoreRepository.submitGigProof(gigId, proofNotes, proofImageUrl)
        }
        return result
    }

    // Create New Scout Gig
    suspend fun createScoutGig(
        title: String,
        description: String,
        category: String,
        location: String,
        rewardCash: Double
    ): Boolean {
        var createdGig: ScoutGigEntity? = null
        val result = db.withTransaction {
            val user = userDao.getUserById(currentUserId) ?: return@withTransaction false
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false

            if (wallet.cashBalance < rewardCash) return@withTransaction false

            // Lock bounty into escrow
            val updatedWallet = wallet.copy(
                cashBalance = wallet.cashBalance - rewardCash,
                escrowLocked = wallet.escrowLocked + rewardCash,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)

            val gig = ScoutGigEntity(
                creatorId = user.id,
                creatorName = user.fullName,
                creatorAvatar = user.avatarUrl,
                title = title,
                description = description,
                category = category,
                location = location,
                rewardCash = rewardCash,
                escrowStatus = "locked",
                remainingHours = 12,
                status = "open"
            )
            scoutDao.insertGig(gig)
            createdGig = gig

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "scout_reward",
                amount = rewardCash,
                direction = "out",
                method = "Escrow Release",
                title = "Escrow Locked for Scout Gig: $title",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
        if (result && createdGig != null) {
            firestoreRepository.createGigListing(createdGig!!)
        }
        return result
    }

    // Book Skill Session
    suspend fun bookSkillSession(sessionId: String): Boolean {
        return db.withTransaction {
            val sessions = skillDao.getAllSessions()
            // In room flow, let's query first or take first
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false
            // Session fee
            val fee = 350.00
            if (wallet.cashBalance < fee) return@withTransaction false

            val updatedWallet = wallet.copy(
                cashBalance = wallet.cashBalance - fee,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "skill_sale",
                amount = fee,
                direction = "out",
                method = "bKash",
                title = "Booked 1-on-1 Mentorship Session",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
    }

    // Offer Your Skill
    suspend fun offerSkillSession(
        title: String,
        category: String,
        fee: Double,
        sessionType: String,
        description: String,
        topics: String
    ) {
        val user = userDao.getUserById(currentUserId) ?: return
        val session = SkillSessionEntity(
            instructorId = user.id,
            instructorName = user.fullName,
            instructorAvatar = user.avatarUrl,
            instructorRating = 5.0,
            reviewsCount = 0,
            title = title,
            category = category,
            fee = fee,
            sessionType = sessionType,
            description = description,
            topics = topics
        )
        skillDao.insertSession(session)
    }

    // Instant Cashout Modal (bKash, Nagad, Rocket, Bank Wire, Crypto USDT)
    suspend fun requestCashout(
        amount: Double,
        method: String,
        accountNumber: String
    ): Result<String> {
        return db.withTransaction {
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction Result.failure(Exception("Wallet not found"))
            if (amount < 50.00) {
                return@withTransaction Result.failure(Exception("নূন্যতম ক্যাশআউট ৳ ৫০.০০ (Minimum ৳50.00)"))
            }
            if (wallet.cashBalance < amount) {
                return@withTransaction Result.failure(Exception("অপর্যাপ্ত ব্যালেন্স (Insufficient balance)"))
            }

            val fee = when (method) {
                "bKash", "Nagad", "Rocket" -> amount * 0.015 // 1.5% MFS cashout fee
                else -> 0.0
            }
            val netWithdrawn = amount

            val updatedWallet = wallet.copy(
                cashBalance = wallet.cashBalance - amount,
                totalWithdrawn = wallet.totalWithdrawn + netWithdrawn,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)

            val txId = "OUT_${System.currentTimeMillis().toString().takeLast(6)}"
            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "cashout",
                amount = amount,
                direction = "out",
                method = method,
                accountNumber = accountNumber,
                referenceId = txId,
                title = "ক্যাশআউট সফল ($method): ৳${String.format("%.2f", amount)}",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            Result.success("ক্যাশআউট সফল! ট্রানজ্যাকশন আইডি: $txId")
        }
    }

    // Coin Exchange: 1,000 engagement coins into ৳১০ cash
    suspend fun convertCoinsToCash(): Result<String> {
        return db.withTransaction {
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction Result.failure(Exception("Wallet not found"))
            if (wallet.coinBalance < 500) {
                return@withTransaction Result.failure(Exception("কয়েন রূপান্তরের জন্য কমপক্ষে ৫০০ কয়েন প্রয়োজন"))
            }

            // Let's allow converting 500 coins to ৳5 or 1000 coins to ৳10
            val coinsToConvert = if (wallet.coinBalance >= 1000) 1000 else 500
            val cashToAdd = if (coinsToConvert == 1000) 10.00 else 5.00

            val updatedWallet = wallet.copy(
                coinBalance = wallet.coinBalance - coinsToConvert,
                cashBalance = wallet.cashBalance + cashToAdd,
                totalEarned = wallet.totalEarned + cashToAdd,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "coin_convert",
                amount = cashToAdd,
                direction = "in",
                method = "Coin Exchange",
                title = "কয়েন এক্সচেঞ্জ: $coinsToConvert কয়েন = ৳${String.format("%.2f", cashToAdd)}",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            Result.success("$coinsToConvert কয়েন সফলভাবে ৳${String.format("%.2f", cashToAdd)} এ রূপান্তরিত হয়েছে!")
        }
    }

    // Phonebook Connector & vCard Hub
    suspend fun inviteContact(contactId: String): Boolean {
        return db.withTransaction {
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false
            val updated = wallet.copy(
                coinBalance = wallet.coinBalance + 50,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updated)

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "signup_bonus",
                amount = 0.0,
                direction = "in",
                method = "Coin Exchange",
                title = "বন্ধু ইনভাইট বোনাস (+50 Coins)",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
    }

    fun generateVCardString(contacts: List<UserPhoneContactEntity>): String {
        val builder = StringBuilder()
        for (c in contacts) {
            builder.append("BEGIN:VCARD\r\n")
            builder.append("VERSION:3.0\r\n")
            builder.append("FN:${c.contactName} (KLIQ)\r\n")
            builder.append("TEL;TYPE=CELL:${c.contactPhone}\r\n")
            builder.append("NOTE:Connected via KLIQ Ecosystem\r\n")
            builder.append("END:VCARD\r\n")
        }
        return builder.toString()
    }

    // Chat actions
    suspend fun sendMessage(
        convId: String,
        recipientId: String,
        text: String,
        attachmentUrl: String? = null,
        isEscrowOffer: Boolean = false,
        escrowAmount: Double? = null
    ) {
        val msg = MessageEntity(
            conversationId = convId,
            senderId = currentUserId,
            recipientId = recipientId,
            messageText = text,
            attachmentUrl = attachmentUrl,
            status = "sent",
            isEscrowContract = isEscrowOffer,
            escrowOfferAmount = escrowAmount,
            escrowOfferStatus = if (isEscrowOffer) "locked" else null
        )
        chatDao.insertMessage(msg)

        val conv = chatDao.getConversationById(convId)
        if (conv != null) {
            chatDao.updateConversation(
                conv.copy(
                    lastMessage = text,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        // Trigger active real-time push notification
        try {
            val notificationHelper = com.example.util.NotificationHelper(context)
            val senderUser = userDao.getUserById(currentUserId)
            val senderName = senderUser?.fullName ?: "KLIQ User"
            notificationHelper.showPushNotification(
                title = "💬 New message from $senderName",
                message = text
            )
        } catch (e: Exception) {
            // Non-fatal
        }
    }

    suspend fun markMessagesAsRead(convId: String) {
        chatDao.markMessagesAsRead(convId)
    }

    // In-chat Escrow Accept & Release
    suspend fun releaseEscrowInChat(messageId: String, amount: Double): Boolean {
        return db.withTransaction {
            val wallet = walletDao.getWallet(currentUserId) ?: return@withTransaction false
            chatDao.updateEscrowStatus(messageId, "released")

            val updatedWallet = wallet.copy(
                cashBalance = wallet.cashBalance + amount,
                totalEarned = wallet.totalEarned + amount,
                updatedAt = System.currentTimeMillis()
            )
            walletDao.updateWallet(updatedWallet)

            val tx = WalletTransactionEntity(
                userId = currentUserId,
                type = "scout_reward",
                amount = amount,
                direction = "in",
                method = "Escrow Release",
                title = "Chat Escrow Contract Released: ৳${String.format("%.2f", amount)}",
                status = "completed"
            )
            walletDao.insertTransaction(tx)
            true
        }
    }

    // ==========================================
    // USER TASKS (Local Persistence Flow)
    // ==========================================
    fun getUserTasksFlow(userId: String = currentUserId): Flow<List<UserTaskEntity>> {
        return userTaskDao.getAllTasksFlow(userId)
    }

    fun getPendingTasksFlow(userId: String = currentUserId): Flow<List<UserTaskEntity>> {
        return userTaskDao.getTasksByStatusFlow(userId, completed = false)
    }

    fun getCompletedTasksFlow(userId: String = currentUserId): Flow<List<UserTaskEntity>> {
        return userTaskDao.getTasksByStatusFlow(userId, completed = true)
    }

    fun getTasksByCategoryFlow(category: String, userId: String = currentUserId): Flow<List<UserTaskEntity>> {
        return userTaskDao.getTasksByCategoryFlow(userId, category)
    }

    suspend fun createUserTask(
        title: String,
        description: String = "",
        category: String = "General",
        priority: String = "Medium",
        rewardAmount: Double = 0.0,
        rewardType: String = "BDT",
        dueDate: Long? = null,
        proofUrl: String? = null,
        proofNotes: String? = null,
        userId: String = currentUserId
    ): UserTaskEntity {
        val task = UserTaskEntity(
            userId = userId,
            title = title,
            description = description,
            category = category,
            priority = priority,
            rewardAmount = rewardAmount,
            rewardType = rewardType,
            dueDate = dueDate,
            proofUrl = proofUrl,
            proofNotes = proofNotes
        )
        userTaskDao.insertTask(task)
        return task
    }

    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        userTaskDao.updateTaskCompletion(taskId, isCompleted)
    }

    suspend fun deleteTask(taskId: String) {
        userTaskDao.deleteTaskById(taskId)
    }

    // ==========================================
    // SOCIAL INTERACTIONS (Local Persistence Flow)
    // ==========================================
    fun getUserInteractionsFlow(userId: String = currentUserId): Flow<List<SocialInteractionEntity>> {
        return socialInteractionDao.getUserInteractionsFlow(userId)
    }

    fun getInteractionsForTargetFlow(targetType: String, targetId: String): Flow<List<SocialInteractionEntity>> {
        return socialInteractionDao.getInteractionsForTargetFlow(targetType, targetId)
    }

    fun getInteractionCountFlow(targetId: String, type: String): Flow<Int> {
        return socialInteractionDao.countInteractionsFlow(targetId, type)
    }

    suspend fun recordSocialInteraction(
        targetType: String,
        targetId: String,
        interactionType: String,
        value: Double = 0.0,
        extraMetadata: String? = null,
        userId: String = currentUserId
    ) {
        val interaction = SocialInteractionEntity(
            userId = userId,
            targetType = targetType,
            targetId = targetId,
            interactionType = interactionType,
            value = value,
            extraMetadata = extraMetadata
        )
        socialInteractionDao.recordInteraction(interaction)
    }

    suspend fun removeSocialInteraction(
        targetId: String,
        interactionType: String,
        userId: String = currentUserId
    ) {
        socialInteractionDao.removeInteraction(userId, targetId, interactionType)
    }
}

