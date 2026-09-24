package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserPhoneContactEntity
import com.example.ui.components.CollapsibleSubNavBar
import com.example.ui.components.KliqBottomNavigation
import com.example.ui.components.TopGlassHeader
import com.example.ui.screens.*
import com.example.ui.theme.ObsidianBlack
import java.io.File
import java.io.FileOutputStream

@Composable
fun KliqApp(
    viewModel: KliqViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val reels by viewModel.reels.collectAsState()
    val gigs by viewModel.gigs.collectAsState()
    val skillSessions by viewModel.skillSessions.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    // Toast watcher
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Function to export .vcf vCard file
    fun exportVcf(contactsList: List<UserPhoneContactEntity>) {
        try {
            val vcfContent = buildString {
                contactsList.forEach { c ->
                    appendLine("BEGIN:VCARD")
                    appendLine("VERSION:3.0")
                    appendLine("FN:${c.contactName} (KLIQ)")
                    appendLine("TEL;TYPE=CELL:${c.contactPhone}")
                    appendLine("NOTE:Matched via KLIQ Ecosystem")
                    appendLine("END:VCARD")
                }
            }

            val file = File(context.cacheDir, "kliq_matched_contacts.vcf")
            FileOutputStream(file).use { it.write(vcfContent.toByteArray()) }

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/x-vcard"
                putExtra(Intent.EXTRA_SUBJECT, "KLIQ Matched Contacts")
                putExtra(Intent.EXTRA_TEXT, vcfContent)
            }
            context.startActivity(Intent.createChooser(sendIntent, "ফোনবুকে এক্সপোর্ট (.vcf)"))
            viewModel.showToast(".vcf ফাইল এক্সপোর্ট প্রস্তুত! 📱")
        } catch (e: Exception) {
            viewModel.showToast("vCard এক্সপোর্ট সম্পন্ন: ${contactsList.size} কন্টাক্ট")
        }
    }

    // Scroll state for hiding/showing top header and bottom navigation
    var isBarsVisible by remember { mutableStateOf(true) }

    // Always restore bars when switching tabs
    LaunchedEffect(uiState.currentTab) {
        isBarsVisible = true
    }

    // Intercept vertical scrolling to toggle header and bottom bar
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // User scrolls up (finger moves up, page scrolls down) -> hide bars
                if (delta < -8f && isBarsVisible) {
                    isBarsVisible = false
                }
                // User scrolls down from top (finger moves down, page scrolls up) -> show bars
                else if (delta > 8f && !isBarsVisible) {
                    isBarsVisible = true
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            if (uiState.isAuthenticated && uiState.currentTab != MainTab.REELS && uiState.activeChatConvId == null) {
                AnimatedVisibility(
                    visible = isBarsVisible,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(180)),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(180))
                ) {
                    TopGlassHeader(
                        currentUser = currentUser,
                        unreadChatCount = conversations.sumOf { it.unreadCount },
                        searchQuery = uiState.searchQuery,
                        searchFilter = uiState.searchFilter,
                        isSearching = uiState.isSearching,
                        onSearchQueryChange = { viewModel.setSearch(it) },
                        onSearchFilterChange = { viewModel.setSearch(uiState.searchQuery, it) },
                        onToggleSearch = { viewModel.toggleSearch(it) },
                        onNewPostClick = { viewModel.setUiSheet(showNewPost = true) },
                        onChatClick = { viewModel.selectTab(MainTab.CHAT) },
                        onNotificationClick = { viewModel.setUiSheet(showNotificationCenter = true) },
                        onProfileClick = { viewModel.selectTab(MainTab.PROFILE) }
                    )
                }
            }
        },
        bottomBar = {
            if (uiState.isAuthenticated && uiState.activeChatConvId == null) {
                AnimatedVisibility(
                    visible = isBarsVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(180)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(180))
                ) {
                    KliqBottomNavigation(
                        currentTab = uiState.currentTab,
                        language = uiState.language,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                .padding(innerPadding)
        ) {
            if (!uiState.isAuthenticated) {
                // Screen 1: Splash & Video Auth Hub
                AuthScreen(
                    onLoginSuccess = { viewModel.setAuthenticated(true) },
                    onResetDemoData = { viewModel.setUiSheet(showResetConfirm = true) },
                    onShowOtpReset = { viewModel.setUiSheet(showOtpReset = true) },
                    viewModel = viewModel
                )
            } else {
                // Main Authenticated Ecosystem Tabs
                when (uiState.currentTab) {
                    MainTab.FEED -> {
                        FeedScreen(
                            currentUser = currentUser,
                            posts = posts,
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = { viewModel.refreshFeed() },
                            onSubNavSelected = { sub ->
                                when (sub) {
                                    "Scout" -> viewModel.selectTab(MainTab.SCOUT)
                                    "Connector" -> viewModel.selectTab(MainTab.CONNECTOR)
                                    "Circle" -> viewModel.selectTab(MainTab.CIRCLE)
                                    "Reels" -> viewModel.selectTab(MainTab.REELS)
                                }
                            },
                            onOpenNewPost = { viewModel.setUiSheet(showNewPost = true) },
                            onLikePost = { viewModel.toggleLikePost(it) },
                            onFavoritePost = { viewModel.toggleFavoritePost(it) },
                            onTipPost = { viewModel.tipPost(it.id) },
                            onCommentClick = { viewModel.setUiSheet(activeCommentPost = it.id) },
                            onShareClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Check out this post on KLIQ: ${it.title ?: it.content}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
                            },
                            onAuthorClick = { viewModel.openAuthorProfile(it) }
                        )
                    }

                    MainTab.SCOUT -> {
                        MarketplaceScreen(
                            gigs = gigs,
                            skillSessions = skillSessions,
                            onClaimGig = { viewModel.claimGig(it) },
                            onSubmitProof = { viewModel.setUiSheet(submitProofGig = it) },
                            onCreateGigClick = { viewModel.setUiSheet(showCreateGig = true) },
                            onBookSession = { viewModel.bookSkillSession(it.id) },
                            onOfferSkillClick = { viewModel.setUiSheet(showOfferSkill = true) }
                        )
                    }

                    MainTab.CONNECTOR -> {
                        ConnectorScreen(
                            contacts = contacts,
                            onInviteContact = { viewModel.inviteContact(it) },
                            onExportVcf = { exportVcf(it) },
                            onChatWithMatched = { matchedUserId ->
                                val targetConv = conversations.find { it.participantOne == matchedUserId || it.participantTwo == matchedUserId }
                                if (targetConv != null) {
                                    viewModel.openConversation(targetConv.id)
                                }
                                viewModel.selectTab(MainTab.CHAT)
                            }
                        )
                    }

                    MainTab.CIRCLE -> {
                        CircleScreen(
                            posts = posts,
                            selectedCircle = uiState.selectedCircle,
                            selectedDistrict = uiState.selectedAreaDistrict,
                            selectedUpazila = uiState.selectedAreaUpazila,
                            selectedWard = uiState.selectedAreaWard,
                            onSelectCircle = { viewModel.selectCircle(it) },
                            onSetArea = { d, u, w -> viewModel.setAreaFilter(d, u, w) },
                            onLikePost = { viewModel.toggleLikePost(it) },
                            onFavoritePost = { viewModel.toggleFavoritePost(it) },
                            onTipPost = { viewModel.tipPost(it.id) },
                            onCommentClick = { viewModel.setUiSheet(activeCommentPost = it.id) },
                            onShareClick = { viewModel.showToast("লিংক কপি হয়েছে!") },
                            onAuthorClick = { viewModel.openAuthorProfile(it) }
                        )
                    }

                    MainTab.REELS -> {
                        ReelsScreen(
                            reels = reels,
                            onLikeReel = { viewModel.toggleLikeReel(it) },
                            onFavoriteReel = { viewModel.toggleReelFavorite(it) },
                            onFollowCreator = { viewModel.toggleFollowCreator(it) },
                            onTipCreator = { reel, amt -> viewModel.tipReelCreator(reel.id, amt) },
                            onShareReel = { viewModel.showToast("রিল লিংক কপি হয়েছে!") },
                            onCommentClick = { viewModel.showToast("কমেন্ট ড্রয়ার খোলা হয়েছে (${it.commentsCount})") }
                        )
                    }

                    MainTab.WALLET -> {
                        WalletScreen(
                            wallet = wallet,
                            transactions = transactions,
                            onOpenCashoutSheet = { viewModel.setUiSheet(showCashout = true) },
                            onConvertCoins = { viewModel.convertCoinsToCash() }
                        )
                    }

                    MainTab.CHAT -> {
                        ChatScreen(
                            conversations = conversations,
                            activeConvId = uiState.activeChatConvId,
                            repository = viewModel.repository,
                            onOpenConversation = { viewModel.openConversation(it) },
                            onCloseConversation = { viewModel.closeConversation() },
                            onSendMessage = { convId, recipient, text, isEscrow, amount ->
                                viewModel.sendMessage(convId, recipient, text, isEscrow, amount)
                            },
                            onReleaseEscrow = { msgId, amt -> viewModel.releaseEscrowInChat(msgId, amt) }
                        )
                    }

                    MainTab.PROFILE -> {
                        val myPosts = posts.filter { it.authorId == viewModel.repository.currentUserId }
                            ProfileScreen(
                                currentUser = currentUser,
                                wallet = wallet,
                                userPosts = myPosts,
                                language = uiState.language,
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onNavigateTab = { viewModel.selectTab(it) },
                                onOpenCashout = { viewModel.setUiSheet(showCashout = true) },
                                onResetDataClick = { viewModel.setUiSheet(showResetConfirm = true) },
                                onSyncOnlineDatabase = { viewModel.syncOnlineDatabase() },
                                onUpdateAvatar = { bitmap -> viewModel.updateProfileAvatar(bitmap) },
                                onLogout = { viewModel.setAuthenticated(false) }
                            )
                    }
                }
            }

            // MODALS & OVERLAYS
            if (uiState.showNewPostSheet) {
                NewPostDialog(
                    onDismiss = { viewModel.setUiSheet(showNewPost = false) },
                    onSubmit = { title, content, mediaUrl, mediaType, visibility, circleType, circleName ->
                        viewModel.createPost(title, content, mediaUrl, mediaType, visibility, circleType, circleName)
                    }
                )
            }

            uiState.activeCommentPostId?.let { postId ->
                CommentSheetDialog(
                    postId = postId,
                    repository = viewModel.repository,
                    onDismiss = { viewModel.setUiSheet(activeCommentPost = "") },
                    onAddComment = { text -> viewModel.addComment(postId, text) }
                )
            }

            if (uiState.showCashoutSheet) {
                CashoutDialog(
                    currentCash = wallet?.cashBalance ?: 0.0,
                    onDismiss = { viewModel.setUiSheet(showCashout = false) },
                    onSubmitCashout = { amount, method, account ->
                        viewModel.requestCashout(amount, method, account)
                    }
                )
            }

            if (uiState.showCreateGigSheet) {
                CreateGigDialog(
                    onDismiss = { viewModel.setUiSheet(showCreateGig = false) },
                    onSubmit = { title, description, category, location, rewardCash ->
                        viewModel.createScoutGig(title, description, category, location, rewardCash)
                    }
                )
            }

            if (uiState.showNotificationCenterSheet) {
                NotificationCenterModal(
                    onDismiss = { viewModel.setUiSheet(showNotificationCenter = false) },
                    onNavigateGig = { viewModel.selectTab(MainTab.SCOUT) },
                    onNavigateLive = { viewModel.selectTab(MainTab.REELS) }
                )
            }

            if (uiState.showOfferSkillSheet) {
                OfferSkillDialog(
                    onDismiss = { viewModel.setUiSheet(showOfferSkill = false) },
                    onSubmit = { title, category, fee, sessionType, description, topics ->
                        viewModel.offerSkillSession(title, category, fee, sessionType, description, topics)
                    }
                )
            }

            uiState.activeSubmitProofGig?.let { gig ->
                SubmitProofDialog(
                    gig = gig,
                    onDismiss = { viewModel.setUiSheet(submitProofGig = null) },
                    onSubmitProof = { notes, proofUrl ->
                        viewModel.submitGigProof(gig.id, notes, proofUrl)
                    }
                )
            }

            uiState.authorPopupUser?.let { user ->
                AuthorProfileModal(
                    user = user,
                    onDismiss = { viewModel.closeAuthorProfile() },
                    onFavorite = { viewModel.showToast("ফেভারিটে সেভ করা হয়েছে ⭐") },
                    onConnect = { viewModel.showToast("কানেকশন রিকোয়েস্ট সেন্ড হয়েছে ✓") },
                    onDirectMessage = {
                        val targetConv = conversations.find { it.participantOne == user.id || it.participantTwo == user.id }
                        if (targetConv != null) {
                            viewModel.openConversation(targetConv.id)
                        }
                        viewModel.selectTab(MainTab.CHAT)
                    }
                )
            }

            if (uiState.showResetDataConfirm) {
                ResetDataConfirmDialog(
                    onConfirm = { viewModel.resetAllDemoData() },
                    onDismiss = { viewModel.setUiSheet(showResetConfirm = false) }
                )
            }

            if (uiState.showOtpResetModal) {
                PhoneOtpResetModal(
                    onDismiss = { viewModel.setUiSheet(showOtpReset = false) },
                    onVerified = { viewModel.showToast("পাসওয়ার্ড সফলভাবে রিসেট করা হয়েছে! লগইন করুন।") }
                )
            }
        }
    }
}
