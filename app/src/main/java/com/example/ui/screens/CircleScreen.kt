package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PostEntity
import com.example.data.model.UserEntity
import com.example.ui.CircleFilter
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleScreen(
    posts: List<PostEntity>,
    selectedCircle: CircleFilter,
    selectedDistrict: String,
    selectedUpazila: String,
    selectedWard: String,
    onSelectCircle: (CircleFilter) -> Unit,
    onSetArea: (String, String, String) -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onFavoritePost: (PostEntity) -> Unit,
    onTipPost: (PostEntity) -> Unit,
    onCommentClick: (PostEntity) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onAuthorClick: (UserEntity) -> Unit
) {
    val circles = listOf(
        CircleFilter.ALL to "🌐 All Circles",
        CircleFilter.CONTACT to "📞 Contact Circle",
        CircleFilter.SCHOOL to "🏫 School Circle",
        CircleFilter.VARSITY to "🎓 Varsity Circle",
        CircleFilter.COLLEGE to "🏛️ College Circle",
        CircleFilter.AREA to "📍 Area Circle"
    )

    var showAreaSelector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Circle Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 0.5.dp, color = ObsidianBorder)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            circles.forEach { (circle, title) ->
                val isSelected = selectedCircle == circle
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
                        .clickable { onSelectCircle(circle) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) ElectricCyan else IceWhite,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Hyper-local Area Geolocation Filter Bar when Area Circle is active
        if (selectedCircle == CircleFilter.AREA) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                hasNeonAccent = true
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$selectedDistrict > $selectedUpazila > $selectedWard",
                                color = IceWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (showAreaSelector) "বন্ধ করুন" else "ফিল্টার পরিবর্তন",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2200D9FF))
                                .clickable { showAreaSelector = !showAreaSelector }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (showAreaSelector) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0x22FFFFFF), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Quick Area Chips
                            listOf("Dhanmondi", "Gulshan", "Mirpur", "Banani", "Uttara").forEach { upazila ->
                                val isCur = selectedUpazila == upazila
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isCur) ElectricCyan else Color(0xFF1E1E2E))
                                        .clickable {
                                            onSetArea("Dhaka", upazila, if (upazila == "Dhanmondi") "Ward 15" else "Ward 01")
                                            showAreaSelector = false
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = upazila,
                                        color = if (isCur) ObsidianBlack else IceWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filtered Posts
        val filteredPosts = remember(posts, selectedCircle) {
            when (selectedCircle) {
                CircleFilter.ALL -> posts
                CircleFilter.CONTACT -> posts.filter { it.circleType == "contact" }
                CircleFilter.SCHOOL -> posts.filter { it.circleType == "school" }
                CircleFilter.VARSITY -> posts.filter { it.circleType == "varsity" }
                CircleFilter.COLLEGE -> posts.filter { it.circleType == "college" }
                CircleFilter.AREA -> posts.filter { it.circleType == "area" }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "${selectedCircle.labelBn} (${filteredPosts.size} পোস্ট)",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (filteredPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🛡️", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "এই সার্কেলে এখনো কোন পোস্ট নেই",
                                color = IceWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "প্রথম পোস্টটি আপনিই করুন!",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
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
