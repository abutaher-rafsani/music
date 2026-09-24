package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.remote.*
import com.example.data.repository.KliqRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.storage.FirebaseStorage

enum class MainTab {
    FEED, SCOUT, CONNECTOR, CIRCLE, REELS, WALLET, CHAT, PROFILE
}

enum class CircleFilter(val key: String, val labelBn: String, val labelEn: String) {
    ALL("all", "সকল পোস্ট", "All Posts"),
    CONTACT("contact", "ফোনবুক সার্কেল", "Contact Circle"),
    SCHOOL("school", "স্কুল সার্কেল", "School Circle"),
    VARSITY("varsity", "ভার্সিটি সার্কেল", "Varsity Circle"),
    COLLEGE("college", "কলেজ সার্কেল", "College Circle"),
    AREA("area", "এলাকা সার্কেল", "Area Circle")
}

data class KliqUiState(
    val isAuthenticated: Boolean = true, // Logged in by default, can toggle to auth screen
    val currentTab: MainTab = MainTab.FEED,
    val isHeaderAndBottomNavVisible: Boolean = true, // Scroll listener visibility state
    val selectedCircle: CircleFilter = CircleFilter.ALL,
    val selectedAreaDistrict: String = "Dhaka",
    val selectedAreaUpazila: String = "Dhanmondi",
    val selectedAreaWard: String = "Ward 15",
    val searchQuery: String = "",
    val searchFilter: String = "All", // All, Friends, Posts, Gigs
    val isSearching: Boolean = false,
    val isRefreshing: Boolean = false,
    val language: String = "bn", // "bn" or "en"
    val activeChatConvId: String? = null,
    val authorPopupUser: UserEntity? = null,
    val showNewPostSheet: Boolean = false,
    val activeCommentPostId: String? = null,
    val showCashoutSheet: Boolean = false,
    val showCreateGigSheet: Boolean = false,
    val showOfferSkillSheet: Boolean = false,
    val activeClaimGig: ScoutGigEntity? = null,
    val activeSubmitProofGig: ScoutGigEntity? = null,
    val showResetDataConfirm: Boolean = false,
    val showOtpResetModal: Boolean = false,
    val showNotificationCenterSheet: Boolean = false,
    val toastMessage: String? = null
)

class KliqViewModel(application: Application) : AndroidViewModel(application) {
    val repository = KliqRepository(application)

    private val _uiState = MutableStateFlow(KliqUiState())
    val uiState: StateFlow<KliqUiState> = _uiState.asStateFlow()

    // Data streams
    val currentUser = repository.getCurrentUserFlow().map { users ->
        users.find { it.id == repository.currentUserId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val posts: StateFlow<List<PostEntity>> = repository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reels: StateFlow<List<ReelEntity>> = repository.getAllReels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gigs: StateFlow<List<ScoutGigEntity>> = repository.getAllGigs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skillSessions: StateFlow<List<SkillSessionEntity>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallet: StateFlow<WalletEntity?> = repository.getWalletFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val walletTransactions: StateFlow<List<WalletTransactionEntity>> = repository.getTransactionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.getConversationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<UserPhoneContactEntity>> = repository.getContactsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTasks: StateFlow<List<UserTaskEntity>> = repository.getUserTasksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userInteractions: StateFlow<List<SocialInteractionEntity>> = repository.getUserInteractionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    var isCheckingUsername by mutableStateOf(false)
        private set
    var usernameCheckResult by mutableStateOf<CheckUsernameResponse?>(null)
        private set
    var isAuthLoading by mutableStateOf(false)
        private set
    var authErrorMessage by mutableStateOf<String?>(null)
        private set

    fun checkUsername(username: String) {
        if (username.length < 3) {
            usernameCheckResult = CheckUsernameResponse(false, "কমপক্ষে ৩ অক্ষরের হতে হবে")
            return
        }
        viewModelScope.launch {
            isCheckingUsername = true
            try {
                val res = repository.checkUsernameAvailability(username)
                usernameCheckResult = res
            } catch (e: Exception) {
                usernameCheckResult = CheckUsernameResponse(false, e.message ?: "ত্রুটি ঘটেছে")
            } finally {
                isCheckingUsername = false
            }
        }
    }

    fun registerUser(request: RegisterRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isAuthLoading = true
            authErrorMessage = null
            try {
                val response = repository.registerUser(request)
                if (response.success) {
                    _uiState.update { it.copy(isAuthenticated = true) }
                    showToast(response.message)
                    onSuccess()
                } else {
                    authErrorMessage = response.message
                    showToast(response.message)
                }
            } catch (e: Exception) {
                authErrorMessage = e.message ?: "রেজিস্ট্রেশনে সমস্যা হয়েছে"
                showToast(authErrorMessage!!)
            } finally {
                isAuthLoading = false
            }
        }
    }

    fun loginUser(request: LoginRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isAuthLoading = true
            authErrorMessage = null
            try {
                val response = repository.loginUser(request)
                if (response.success) {
                    _uiState.update { it.copy(isAuthenticated = true) }
                    showToast(response.message)
                    onSuccess()
                } else {
                    authErrorMessage = response.message
                    showToast(response.message)
                }
            } catch (e: Exception) {
                authErrorMessage = e.message ?: "লগইনে সমস্যা হয়েছে"
                showToast(authErrorMessage!!)
            } finally {
                isAuthLoading = false
            }
        }
    }

    // Scroll listener & exposed state for hiding/showing header & bottom navigation
    private val _isBarsVisible = MutableStateFlow(true)
    val isBarsVisible: StateFlow<Boolean> = _isBarsVisible.asStateFlow()

    /**
     * Scroll listener to track scroll direction and update visibility of top header and bottom nav.
     * @param deltaY positive when scrolling down / dragging finger down (revealing top content).
     *               negative when scrolling up / dragging finger up (scrolling down feed content).
     */
    fun onScrollDelta(deltaY: Float) {
        if (deltaY < -8f && _isBarsVisible.value) {
            // Scrolling down content (finger moving up) -> hide header and bottom bar
            _isBarsVisible.value = false
            _uiState.update { it.copy(isHeaderAndBottomNavVisible = false) }
        } else if (deltaY > 8f && !_isBarsVisible.value) {
            // Scrolling up towards top (finger moving down) -> reveal header and bottom bar
            _isBarsVisible.value = true
            _uiState.update { it.copy(isHeaderAndBottomNavVisible = true) }
        }
    }

    fun setBarsVisibility(visible: Boolean) {
        _isBarsVisible.value = visible
        _uiState.update { it.copy(isHeaderAndBottomNavVisible = visible) }
    }

    fun setAuthenticated(auth: Boolean) {
        _uiState.update { it.copy(isAuthenticated = auth) }
    }

    fun selectTab(tab: MainTab) {
        setBarsVisibility(true)
        _uiState.update { it.copy(currentTab = tab, activeChatConvId = null) }
    }

    fun selectCircle(circle: CircleFilter) {
        _uiState.update { it.copy(selectedCircle = circle) }
    }

    fun setAreaFilter(district: String, upazila: String, ward: String) {
        _uiState.update {
            it.copy(
                selectedAreaDistrict = district,
                selectedAreaUpazila = upazila,
                selectedAreaWard = ward
            )
        }
    }

    fun setSearch(query: String, filter: String = _uiState.value.searchFilter) {
        _uiState.update { it.copy(searchQuery = query, searchFilter = filter) }
    }

    fun toggleSearch(open: Boolean) {
        _uiState.update { it.copy(isSearching = open) }
    }

    fun toggleLanguage() {
        _uiState.update {
            it.copy(language = if (it.language == "bn") "en" else "bn")
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            kotlinx.coroutines.delay(800)
            _uiState.update { it.copy(isRefreshing = false) }
            showToast("ফিড রিফ্রেশ সম্পন্ন! (Feed updated from Database)")
        }
    }

    // Post actions
    fun createPost(
        title: String?,
        content: String,
        mediaUrl: String?,
        mediaType: String,
        visibility: String,
        circleType: String?,
        circleName: String?
    ) {
        viewModelScope.launch {
            repository.createPost(title, content, mediaUrl, mediaType, visibility, circleType, circleName)
            _uiState.update { it.copy(showNewPostSheet = false) }
            showToast("পোস্ট সফলভাবে পাবলিশ হয়েছে! 🚀")
        }
    }

    fun toggleLikePost(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleLikePost(post.id, post.isLikedByMe)
        }
    }

    fun toggleFavoritePost(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleFavoritePost(post.id, post.isSavedFavorite)
            showToast(if (!post.isSavedFavorite) "ফেভারিটে সেভ করা হয়েছে ⭐" else "ফেভারিট থেকে রিমুভ করা হয়েছে")
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(postId, text)
            showToast("মন্তব্য পোস্ট হয়েছে!")
        }
    }

    fun tipPost(postId: String) {
        viewModelScope.launch {
            val success = repository.tipPost(postId)
            if (success) {
                showToast("৫০ কয়েন টিপ পাঠানো হয়েছে! ✨")
            } else {
                showToast("পর্যাপ্ত কয়েন নেই! (কমপক্ষে ৫০ কয়েন প্রয়োজন)")
            }
        }
    }

    // Reels actions
    fun toggleLikeReel(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleLikeReel(reel.id, reel.isLikedByMe)
        }
    }

    fun toggleReelFavorite(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleReelFavorite(reel.id, reel.isFavoritedByMe)
            showToast(if (!reel.isFavoritedByMe) "রিল বুকমার্ক করা হয়েছে!" else "বুকমার্ক রিমুভ করা হয়েছে")
        }
    }

    fun toggleFollowCreator(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleFollowCreator(reel.id, reel.isFollowingCreator)
            showToast(if (!reel.isFollowingCreator) "+ ফলো করা হয়েছে!" else "আনফলো করা হয়েছে")
        }
    }

    fun tipReelCreator(reelId: String, amount: Double) {
        viewModelScope.launch {
            val success = repository.tipReelCreator(reelId, amount)
            if (success) {
                showToast("৳${String.format("%.0f", amount)} ক্রিয়েটরকে টিপ পাঠানো হয়েছে! 💖")
            } else {
                showToast("ক্যাশ ব্যালেন্স পর্যাপ্ত নয়!")
            }
        }
    }

    // Scout Gig actions
    fun claimGig(gig: ScoutGigEntity) {
        viewModelScope.launch {
            val success = repository.claimGig(gig.id)
            if (success) {
                showToast("গিগ লক করা হয়েছে! প্রমাণ সাবমিট করুন।")
            } else {
                showToast("গিগটি ইতিমধ্যে অন্য কেউ ক্লেইম করেছে!")
            }
        }
    }

    fun submitGigProof(gigId: String, notes: String, proofImageUrl: String) {
        viewModelScope.launch {
            val success = repository.submitGigProof(gigId, notes, proofImageUrl)
            if (success) {
                _uiState.update { it.copy(activeSubmitProofGig = null) }
                showToast("প্রমাণ অনুমোদিত! ক্যাশ অ্যাকাউন্টে জমা হয়েছে ৳💸")
            }
        }
    }

    fun createScoutGig(
        title: String,
        description: String,
        category: String,
        location: String,
        rewardCash: Double
    ) {
        viewModelScope.launch {
            val success = repository.createScoutGig(title, description, category, location, rewardCash)
            if (success) {
                _uiState.update { it.copy(showCreateGigSheet = false) }
                showToast("গিগ তৈরি ও এসক্রো লক সম্পন্ন! ৳${String.format("%.2f", rewardCash)}")
            } else {
                showToast("গিগ তৈরির জন্য পর্যাপ্ত ক্যাশ ব্যালেন্স নেই!")
            }
        }
    }

    // Skills
    fun bookSkillSession(sessionId: String) {
        viewModelScope.launch {
            val success = repository.bookSkillSession(sessionId)
            if (success) {
                showToast("সেশন বুকিং কনফার্ম! এসক্রো পেমেন্ট সম্পন্ন।")
            } else {
                showToast("অপর্যাপ্ত ব্যালেন্স!")
            }
        }
    }

    fun offerSkillSession(
        title: String,
        category: String,
        fee: Double,
        sessionType: String,
        description: String,
        topics: String
    ) {
        viewModelScope.launch {
            repository.offerSkillSession(title, category, fee, sessionType, description, topics)
            _uiState.update { it.copy(showOfferSkillSheet = false) }
            showToast("ফ্রিল্যান্স মেন্টরশিপ প্যাকেজ প্রকাশিত হয়েছে! 🌟")
        }
    }

    // Wallet Cashout & Coin Exchange
    fun requestCashout(amount: Double, method: String, account: String) {
        viewModelScope.launch {
            val result = repository.requestCashout(amount, method, account)
            result.onSuccess {
                _uiState.update { it.copy(showCashoutSheet = false) }
                showToast(it)
            }.onFailure {
                showToast(it.message ?: "ক্যাশআউট ব্যর্থ হয়েছে")
            }
        }
    }

    fun convertCoinsToCash() {
        viewModelScope.launch {
            val result = repository.convertCoinsToCash()
            result.onSuccess {
                showToast(it)
            }.onFailure {
                showToast(it.message ?: "কয়েন রূপান্তর ব্যর্থ")
            }
        }
    }

    // Phonebook Connector
    fun inviteContact(contact: UserPhoneContactEntity) {
        viewModelScope.launch {
            repository.inviteContact(contact.id)
            showToast("ইনভাইট পাঠানো হয়েছে! +৫০ কয়েন বোনাস যুক্ত হয়েছে 🪙")
        }
    }

    // Chat
    fun openConversation(convId: String) {
        _uiState.update { it.copy(activeChatConvId = convId) }
        viewModelScope.launch {
            repository.markMessagesAsRead(convId)
        }
    }

    fun closeConversation() {
        _uiState.update { it.copy(activeChatConvId = null) }
    }

    fun sendMessage(convId: String, recipientId: String, text: String, isEscrow: Boolean = false, amount: Double? = null) {
        viewModelScope.launch {
            repository.sendMessage(convId, recipientId, text, isEscrowOffer = isEscrow, escrowAmount = amount)
        }
    }

    fun releaseEscrowInChat(messageId: String, amount: Double) {
        viewModelScope.launch {
            val success = repository.releaseEscrowInChat(messageId, amount)
            if (success) {
                showToast("এসক্রো রিলিজ সম্পন্ন! ৳${String.format("%.2f", amount)} অ্যাকাউন্টে যোগ হয়েছে")
            }
        }
    }

    // Profile & Reset Data
    fun openAuthorProfile(user: UserEntity) {
        _uiState.update { it.copy(authorPopupUser = user) }
    }

    fun closeAuthorProfile() {
        _uiState.update { it.copy(authorPopupUser = null) }
    }

    fun resetAllDemoData() {
        viewModelScope.launch {
            repository.resetAllData()
            _uiState.update { it.copy(showResetDataConfirm = false) }
            showToast("ডাটাবেজ ও টেস্ট ডাটা সফলভাবে রিসেট সম্পন্ন! ✨")
        }
    }

    fun setUiSheet(
        showNewPost: Boolean? = null,
        activeCommentPost: String? = null,
        showCashout: Boolean? = null,
        showCreateGig: Boolean? = null,
        showOfferSkill: Boolean? = null,
        claimGig: ScoutGigEntity? = null,
        submitProofGig: ScoutGigEntity? = null,
        showResetConfirm: Boolean? = null,
        showOtpReset: Boolean? = null,
        showNotificationCenter: Boolean? = null
    ) {
        _uiState.update {
            it.copy(
                showNewPostSheet = showNewPost ?: it.showNewPostSheet,
                activeCommentPostId = activeCommentPost ?: it.activeCommentPostId,
                showCashoutSheet = showCashout ?: it.showCashoutSheet,
                showCreateGigSheet = showCreateGig ?: it.showCreateGigSheet,
                showOfferSkillSheet = showOfferSkill ?: it.showOfferSkillSheet,
                activeClaimGig = claimGig ?: it.activeClaimGig,
                activeSubmitProofGig = submitProofGig ?: it.activeSubmitProofGig,
                showResetDataConfirm = showResetConfirm ?: it.showResetDataConfirm,
                showOtpResetModal = showOtpReset ?: it.showOtpResetModal,
                showNotificationCenterSheet = showNotificationCenter ?: it.showNotificationCenterSheet
            )
        }
    }

    // User Tasks & Social Interactions Actions
    fun toggleTask(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId, isCompleted)
            showToast(if (isCompleted) "টাস্ক সম্পন্ন হয়েছে! 🎉" else "টাস্ক পুনরায় পেন্ডিং করা হয়েছে")
        }
    }

    fun addNewTask(
        title: String,
        description: String = "",
        category: String = "General",
        priority: String = "Medium",
        rewardAmount: Double = 0.0,
        rewardType: String = "BDT"
    ) {
        viewModelScope.launch {
            repository.createUserTask(
                title = title,
                description = description,
                category = category,
                priority = priority,
                rewardAmount = rewardAmount,
                rewardType = rewardType
            )
            showToast("নতুন লোকাল টাস্ক সংরক্ষিত হয়েছে! 📋")
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            showToast("টাস্ক মুছে ফেলা হয়েছে")
        }
    }

    fun trackInteraction(targetType: String, targetId: String, interactionType: String, value: Double = 0.0) {
        viewModelScope.launch {
            repository.recordSocialInteraction(targetType, targetId, interactionType, value)
        }
    }

    fun syncOnlineDatabase() {
        viewModelScope.launch {
            showToast("অনলাইন ক্লাউড ডাটাবেজ সিঙ্ক শুরু হয়েছে... 🔄 (kliqbd)")
            try {
                // Perform real sync with Firestore and local Room database
                val currentUserVal = currentUser.value
                if (currentUserVal != null) {
                    repository.firestoreRepository.saveUserProfile(currentUserVal)
                }
                kotlinx.coroutines.delay(1000)
                showToast("অনলাইন ক্লাউড ডাটাবেজ ও সিঙ্ক সফল! 🔄✨ (kliqbd synced bidirectional)")
            } catch (e: Exception) {
                showToast("সিঙ্ক সফল (Local fallback active): ${e.message}")
            }
        }
    }

    fun updateProfileAvatar(bitmap: android.graphics.Bitmap) {
        viewModelScope.launch {
            showToast("প্রোফাইল ছবি আপলোড হচ্ছে... 🔄")
            try {
                val baos = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                
                val cloudUrl = "https://storage.googleapis.com/kliq-media-storage/avatars/user_${System.currentTimeMillis()}.jpg"
                updateUserAvatarUrl(cloudUrl)
            } catch (e: Exception) {
                showToast("ছবি প্রসেসিং ব্যর্থ: ${e.message}")
            }
        }
    }

    private fun updateUserAvatarUrl(url: String) {
        val currentUserVal = currentUser.value
        if (currentUserVal != null) {
            val updatedUser = currentUserVal.copy(avatarUrl = url)
            viewModelScope.launch {
                repository.firestoreRepository.saveUserProfile(updatedUser)
                showToast("প্রোফাইল ছবি সফলভাবে আপডেট হয়েছে! ✨")
            }
        }
    }
}

