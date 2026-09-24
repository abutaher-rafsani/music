package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserEntity
import com.example.ui.MainTab
import com.example.ui.theme.*

@Composable
fun TopGlassHeader(
    currentUser: UserEntity?,
    unreadChatCount: Int,
    searchQuery: String,
    searchFilter: String,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchFilterChange: (String) -> Unit,
    onToggleSearch: (Boolean) -> Unit,
    onNewPostClick: () -> Unit,
    onChatClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val trendingTags = listOf("#Scout", "#MarketAudit", "#Design", "#Dhaka", "#Cashout", "#Python")
    val searchFilters = listOf("All", "Friends", "Posts", "Gigs")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 0.5.dp, color = ObsidianBorder)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Left: Glowing KLIQ gradient logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleSearch(false) }
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandCyanPurpleGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K",
                        color = IceWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "KLIQ",
                        color = IceWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ECOSYSTEM",
                        color = ElectricCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Action Dock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Toggle
                IconButton(
                    onClick = { onToggleSearch(!isSearching) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSearching) ElectricCyan.copy(alpha = 0.2f) else ObsidianCardBg)
                        .border(1.dp, if (isSearching) ElectricCyan else ObsidianBorder, CircleShape)
                        .testTag("search_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isSearching) ElectricCyan else IceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // New Post Quick Launcher
                IconButton(
                    onClick = onNewPostClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BrandCyanPurpleGradient)
                        .testTag("new_post_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Post",
                        tint = IceWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Live Chat Icon with unread badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ObsidianCardBg)
                        .border(1.dp, ObsidianBorder, CircleShape)
                        .clickable { onChatClick() }
                        .testTag("chat_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Chat",
                        tint = IceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    if (unreadChatCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(NeonMagenta),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unreadChatCount.toString(),
                                color = IceWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Notification Center Bell Icon with unread badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ObsidianCardBg)
                        .border(1.dp, ObsidianBorder, CircleShape)
                        .clickable { onNotificationClick() }
                        .testTag("notification_center_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(NeonPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            color = IceWhite,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Profile Avatar with verified badge & online indicator
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onProfileClick() }
                        .testTag("header_profile_avatar")
                ) {
                    val avatarUrl = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80"
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(1.5.dp, ElectricCyan, CircleShape)
                    )
                    // Online indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                            .border(1.5.dp, ObsidianBlack, CircleShape)
                    )
                }
            }
        }

        // Expanded Search Hub Overlay
        AnimatedVisibility(
            visible = isSearching,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Search Input Field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianSurface)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = TextStyle(color = IceWhite, fontSize = 14.sp),
                            cursorBrush = SolidColor(ElectricCyan),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search posts, scout gigs, friends...",
                                        color = TextSubtle,
                                        fontSize = 13.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onSearchQueryChange("") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    searchFilters.forEach { filter ->
                        val isSelected = searchFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) ElectricCyan else ObsidianSurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else ObsidianBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSearchFilterChange(filter) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else IceWhite,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Trending Tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trendingTags.forEach { tag ->
                        Text(
                            text = tag,
                            color = NeonPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3E8FF))
                                .clickable { onSearchQueryChange(tag) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
