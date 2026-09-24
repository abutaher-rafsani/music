package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PostEntity
import com.example.data.model.UserEntity
import com.example.ui.MainTab
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    currentUser: UserEntity?,
    posts: List<PostEntity>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSubNavSelected: (String) -> Unit,
    onOpenNewPost: () -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onFavoritePost: (PostEntity) -> Unit,
    onTipPost: (PostEntity) -> Unit,
    onCommentClick: (PostEntity) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onAuthorClick: (UserEntity) -> Unit
) {
    var activeSubNav by remember { mutableStateOf("Home") }
    val subNavItems = listOf("Home", "Scout", "Connector", "Circle", "Favorite", "Reels")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // 1. Top Horizontal Sub-Navigation (Sticky & visible when header collapses)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, ObsidianBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                subNavItems.forEach { item ->
                    val isSelected = activeSubNav == item
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) ElectricCyan.copy(alpha = 0.12f) else ObsidianSurface
                            )
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else ObsidianBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                activeSubNav = item
                                onSubNavSelected(item)
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item,
                                color = if (isSelected) ElectricCyan else MetallicSilver,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            if (item == "Connector") {
                                Spacer(modifier = Modifier.width(6.dp))
                                PulsingSyncDot(color = SuccessGreen, size = 7.dp)
                            }
                        }
                    }
                }
            }
        }

        // Lazy list containing Create Post box + Feed items
        val displayedPosts = remember(posts, activeSubNav) {
            when (activeSubNav) {
                "Favorite" -> posts.filter { it.isSavedFavorite }
                "Circle" -> posts.filter { it.visibility == "circle" }
                else -> posts
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Pull-to-refresh / manual refresh indicator banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE FEED • ${displayedPosts.size} POSTS",
                        color = TextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onRefresh() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = ElectricCyan,
                                strokeWidth = 1.5.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Syncing...", color = ElectricCyan, fontSize = 11.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Refresh", color = ElectricCyan, fontSize = 11.sp)
                        }
                    }
                }
            }

            // 2. Highlighted Post Creation Box
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    hasNeonAccent = true
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                                contentDescription = "My Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, ElectricCyan, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(ObsidianSurface)
                                    .border(1.dp, ObsidianBorder, RoundedCornerShape(21.dp))
                                    .clickable { onOpenNewPost() }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "What's on your mind? (শেয়ার করুন...)",
                                    color = TextSubtle,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = ObsidianBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick media shortcuts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PostShortcutItem(icon = Icons.Default.Image, text = "Photo/Video", color = ElectricCyan) { onOpenNewPost() }
                            PostShortcutItem(icon = Icons.Default.PlayCircle, text = "Reels", color = NeonPurple) { onOpenNewPost() }
                            PostShortcutItem(icon = Icons.Default.Group, text = "Circle", color = GoldCoin) { onOpenNewPost() }
                            PostShortcutItem(icon = Icons.Default.SentimentSatisfied, text = "Feeling", color = NeonMagenta) { onOpenNewPost() }
                        }
                    }
                }
            }

            // 4. Feed Post Cards or Shimmer Skeletons during refresh
            if (isRefreshing) {
                items(3) {
                    ObsidianShimmerSkeleton()
                }
            } else {
                items(displayedPosts, key = { it.id }) { post ->
                    FeedPostCard(
                        post = post,
                        onLike = { onLikePost(post) },
                        onFavorite = { onFavoritePost(post) },
                        onTip = { onTipPost(post) },
                        onComment = { onCommentClick(post) },
                        onShare = { onShareClick(post) },
                        onAuthorClick = {
                            val authorUser = UserEntity(
                                id = post.authorId,
                                fullName = post.authorName,
                                username = post.authorUsername,
                                avatarUrl = post.authorAvatarUrl,
                                isVerified = post.isAuthorVerified,
                                specialBadge = post.authorBadge,
                                phone = "+8801700000000"
                            )
                            onAuthorClick(authorUser)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PostShortcutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = MetallicSilver, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun FeedPostCard(
    post: PostEntity,
    onLike: () -> Unit,
    onFavorite: () -> Unit,
    onTip: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onAuthorClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showHeartAnimation by remember { mutableStateOf(false) }
    var isLikedAnimating by remember { mutableStateOf(false) }
    var isTippedAnimating by remember { mutableStateOf(false) }

    val likeScale by animateFloatAsState(
        targetValue = if (isLikedAnimating) 1.35f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "like_click_scale"
    )

    val tipScale by animateFloatAsState(
        targetValue = if (isTippedAnimating) 1.3f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tip_click_scale"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        hasNeonAccent = false
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Author Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick() }
                ) {
                    AsyncImage(
                        model = post.authorAvatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                        contentDescription = post.authorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                color = IceWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.isAuthorVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedBadge(size = 14.dp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            SpecialBadgePill(badgeText = post.authorBadge)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${post.authorUsername}",
                                color = TextSubtle,
                                fontSize = 11.sp
                            )
                            if (post.circleName != null) {
                                Text(text = " • ", color = TextSubtle, fontSize = 11.sp)
                                Text(
                                    text = post.circleName,
                                    color = ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = " • ${post.visibility}",
                                color = TextSubtle,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Favorite bookmark toggle
                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (post.isSavedFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (post.isSavedFavorite) GoldCoin else TextSubtle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title & Content
            if (!post.title.isNullOrBlank()) {
                Text(
                    text = post.title,
                    color = IceWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = post.content,
                color = MetallicSilver,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            // Post Media / Gallery with double tap heart support
            if (!post.mediaUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    showHeartAnimation = true
                                    isLikedAnimating = true
                                    onLike()
                                    coroutineScope.launch {
                                        delay(800)
                                        showHeartAnimation = false
                                        isLikedAnimating = false
                                    }
                                }
                            )
                        }
                ) {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = "Post Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Double-tap heart animation
                    if (showHeartAnimation) {
                        val scale by animateFloatAsState(
                            targetValue = if (showHeartAnimation) 1.2f else 0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "heart_scale"
                        )
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Heart",
                            tint = NeonMagenta,
                            modifier = Modifier
                                .size(70.dp)
                                .scale(scale)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Metrics & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like Button with spring scale animation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            isLikedAnimating = true
                            onLike()
                            coroutineScope.launch {
                                delay(350)
                                isLikedAnimating = false
                            }
                        }
                        .scale(likeScale)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) NeonMagenta else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likesCount.toString(),
                        color = if (post.isLikedByMe) NeonMagenta else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onComment() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.commentsCount.toString(),
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tip 50 Coins Button with spring scale animation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFB800))
                        .clickable {
                            isTippedAnimating = true
                            onTip()
                            coroutineScope.launch {
                                delay(350)
                                isTippedAnimating = false
                            }
                        }
                        .scale(tipScale)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "🪙", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tip 50",
                        color = GoldCoin,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share Button
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
