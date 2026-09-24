package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ReelEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ReelsScreen(
    reels: List<ReelEntity>,
    onLikeReel: (ReelEntity) -> Unit,
    onFavoriteReel: (ReelEntity) -> Unit,
    onFollowCreator: (ReelEntity) -> Unit,
    onTipCreator: (ReelEntity, Double) -> Unit,
    onShareReel: (ReelEntity) -> Unit,
    onCommentClick: (ReelEntity) -> Unit
) {
    if (reels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "কোন রিল পাওয়া যায়নি", color = IceWhite)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })
    var isMuted by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var showTipModal by remember { mutableStateOf(false) }
    var tipTargetReel by remember { mutableStateOf<ReelEntity?>(null) }

    // Vinyl spin animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("reels_vertical_pager")
    ) { pageIndex ->
        val currentReel = reels[pageIndex]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack)
        ) {
            // Full screen 9:16 Video / Poster Canvas
            AsyncImage(
                model = currentReel.posterUrl ?: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop&q=80",
                contentDescription = currentReel.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("reel_poster_${currentReel.id}")
            )

            // Dark gradient scrims for contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Bookmark Pill & Controls (Mute, Replay)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reel ID Bookmark Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x990E0E17))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable { onFavoriteReel(currentReel) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("reel_bookmark_pill_${currentReel.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentReel.isFavoritedByMe) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (currentReel.isFavoritedByMe) GoldCoin else IceWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "#${currentReel.id}",
                            color = IceWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Volume Toggle & Play/Pause
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .testTag("reel_mute_toggle")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Volume Toggle",
                            tint = IceWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .testTag("reel_play_toggle")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Play/Pause",
                            tint = IceWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Floating Right Side Action Dock (Avatar + Follow overlay, Like, Comment, Tip, Share, Vinyl)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Creator Avatar with (+) Follow button overlay
                Box(contentAlignment = Alignment.BottomCenter) {
                    AsyncImage(
                        model = currentReel.creatorAvatarUrl ?: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                        contentDescription = currentReel.creatorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, ElectricCyan, CircleShape)
                    )

                    // (+) Follow Button Overlay
                    if (!currentReel.isFollowingCreator) {
                        Box(
                            modifier = Modifier
                                .offset(y = 8.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(NeonMagenta)
                                .clickable { onFollowCreator(currentReel) }
                                .testTag("reel_follow_button_${currentReel.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Follow",
                                tint = IceWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Like Action
                ReelActionItem(
                    icon = if (currentReel.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    tint = if (currentReel.isLikedByMe) NeonMagenta else IceWhite,
                    label = currentReel.likesCount.toString(),
                    onClick = { onLikeReel(currentReel) }
                )

                // Comment Action
                ReelActionItem(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    tint = IceWhite,
                    label = currentReel.commentsCount.toString(),
                    onClick = { onCommentClick(currentReel) }
                )

                // Gift Tip Action
                ReelActionItem(
                    icon = Icons.Filled.MonetizationOn,
                    tint = GoldCoin,
                    label = "৳${String.format("%.0f", currentReel.tipsTotal)}",
                    onClick = {
                        tipTargetReel = currentReel
                        showTipModal = true
                    }
                )

                // Share Action
                ReelActionItem(
                    icon = Icons.Outlined.Share,
                    tint = IceWhite,
                    label = currentReel.sharesCount.toString(),
                    onClick = { onShareReel(currentReel) }
                )

                // Spinning Vinyl Record audio disk
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF111111))
                        .border(2.dp, ElectricCyan, CircleShape)
                        .rotate(spinAngle),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(NeonPurple)
                    )
                }
            }

            // Bottom Details Overlay (Creator Name, Caption, Audio Ticker)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 80.dp, bottom = 90.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentReel.creatorName,
                        color = IceWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (currentReel.isCreatorVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(size = 14.dp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    SpecialBadgePill(badgeText = "CREATOR")
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentReel.caption,
                    color = MetallicSilver,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentReel.hashtags,
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Audio ticker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentReel.audioTitle} • ${currentReel.musicAuthor}",
                        color = IceWhite,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }

    // Gift Tip Modal Dialog
    if (showTipModal && tipTargetReel != null) {
        AlertDialog(
            onDismissRequest = { showTipModal = false },
            containerColor = ObsidianSurface,
            titleContentColor = IceWhite,
            title = {
                Text(
                    text = "ক্রিয়েটরকে উপহার টিপ পাঠান",
                    fontWeight = FontWeight.Bold,
                    color = IceWhite
                )
            },
            text = {
                Column {
                    Text(
                        text = "আপনার ওয়ালেট ব্যালেন্স থেকে সরাসরি ${tipTargetReel!!.creatorName} কে টিপ পাঠানো হবে:",
                        color = MetallicSilver,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10.0, 20.0, 50.0, 100.0).forEach { amt ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x2200D9FF))
                                    .border(1.dp, ElectricCyan, RoundedCornerShape(12.dp))
                                    .clickable {
                                        onTipCreator(tipTargetReel!!, amt)
                                        showTipModal = false
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "৳${amt.toInt()}",
                                    color = ElectricCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTipModal = false }) {
                    Text(text = "বাতিল", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun ReelActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x66000000))
                .border(1.dp, Color(0x33FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = IceWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
