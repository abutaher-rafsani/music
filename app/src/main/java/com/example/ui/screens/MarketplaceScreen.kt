package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ScoutGigEntity
import com.example.data.model.SkillSessionEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    gigs: List<ScoutGigEntity>,
    skillSessions: List<SkillSessionEntity>,
    onClaimGig: (ScoutGigEntity) -> Unit,
    onSubmitProof: (ScoutGigEntity) -> Unit,
    onCreateGigClick: () -> Unit,
    onBookSession: (SkillSessionEntity) -> Unit,
    onOfferSkillClick: () -> Unit
) {
    var activeMode by remember { mutableIntStateOf(0) } // 0: Scout Gigs, 1: Skill Sharing
    var selectedCategory by remember { mutableStateOf("All") }

    val gigCategories = listOf("All", "Shop & Price Audit", "Local Verification", "Photo & Proof", "Stock Check", "Survey")
    val skillCategories = listOf("All", "Photography", "Content Creation", "Market Research", "Graphic Design")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Mode Switcher Header: Scout Gigs vs Skill Sharing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(22.dp))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Mode 0: Scout Gigs
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(18.dp))
                            .then(
                                if (activeMode == 0) Modifier.background(BrandCyanPurpleGradient)
                                else Modifier.background(Color.Transparent)
                            )
                            .clickable {
                                activeMode = 0
                                selectedCategory = "All"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔍 লোকাল স্কাউট গিগস",
                            color = if (activeMode == 0) Color.White else IceWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Mode 1: Peer-to-Peer Skill Sharing
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(18.dp))
                            .then(
                                if (activeMode == 1) Modifier.background(BrandPurpleMagentaGradient)
                                else Modifier.background(Color.Transparent)
                            )
                            .clickable {
                                activeMode = 1
                                selectedCategory = "All"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎓 স্কিল শেয়ারিং ও মেন্টর",
                            color = if (activeMode == 1) Color.White else IceWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        val categories = if (activeMode == 0) gigCategories else skillCategories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 0.5.dp, color = ObsidianBorder)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) ElectricCyan.copy(alpha = 0.12f) else ObsidianSurface
                        )
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else ObsidianBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) ElectricCyan else MetallicSilver,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main List Content
        if (activeMode == 0) {
            // MODE 0: SCOUT GIGS
            val filteredGigs = remember(gigs, selectedCategory) {
                if (selectedCategory == "All") gigs else gigs.filter { it.category == selectedCategory }
            }

            // Cloud Firestore Status Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Firestore Live Repository (kliqbd)",
                        color = TextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "${filteredGigs.size} Gigs Listed",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Post New Scout Gig Banner
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        hasNeonAccent = true
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "আপনার এলাকায় অডিট করাতে চান?",
                                    color = IceWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "নিরাপদ এসক্রো সুবিধায় স্কাউট নিয়োগ দিন",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            PillButton(
                                text = "+ গিগ দিন",
                                onClick = onCreateGigClick,
                                height = 36.dp
                            )
                        }
                    }
                }

                items(filteredGigs, key = { it.id }) { gig ->
                    ScoutGigCard(
                        gig = gig,
                        onClaim = { onClaimGig(gig) },
                        onSubmitProof = { onSubmitProof(gig) }
                    )
                }
            }
        } else {
            // MODE 1: SKILL SHARING
            val filteredSessions = remember(skillSessions, selectedCategory) {
                if (selectedCategory == "All") skillSessions else skillSessions.filter { it.category == selectedCategory }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Offer Skill Banner
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        hasNeonAccent = true
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "নিজের স্কিল শিখিয়ে আয় করুন",
                                    color = IceWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "১-অন-১ মেন্টরশিপ বা ওয়ার্কশপ প্যাকেজ সাজান",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            PillButton(
                                text = "+ অফার করুন",
                                onClick = onOfferSkillClick,
                                gradient = BrandPurpleMagentaGradient,
                                height = 36.dp
                            )
                        }
                    }
                }

                items(filteredSessions, key = { it.id }) { session ->
                    SkillSessionCard(
                        session = session,
                        onBook = { onBookSession(session) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScoutGigCard(
    gig: ScoutGigEntity,
    onClaim: () -> Unit,
    onSubmitProof: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        hasNeonAccent = gig.status == "open"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Category & Bounty
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x227928CA))
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = gig.category,
                        color = Color(0xFFD0BCFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Bounty in ৳
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "৳${String.format("%.0f", gig.rewardCash)}",
                        color = GoldCoin,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "বাউন্টি",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = gig.title,
                color = IceWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = gig.description,
                color = MetallicSilver,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Remaining Hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = gig.location, color = TextMuted, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gig.remainingHours} ঘণ্টা বাকি", color = WarningAmber, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Escrow guarantee notice
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x1A00E676))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🔒 ১০০% এসক্রো সিকিউর্ড (কাজ শেষ হলেই ব্যালেন্সে ক্যাশ যুক্ত হবে)",
                    color = SuccessGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            when (gig.status) {
                "open" -> {
                    PillButton(
                        text = "গিগ ক্লেইম করুন (Claim Task)",
                        onClick = onClaim,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "in_progress" -> {
                    Button(
                        onClick = onSubmitProof,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "প্রমাণ আপলোড ও টাকা নিন 📸", fontWeight = FontWeight.Bold)
                    }
                }
                "completed" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x3300E676)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓ কাজ সম্পন্ন ও বাউন্টি পেইড",
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SkillSessionCard(
    session: SkillSessionEntity,
    onBook: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Instructor Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = session.instructorAvatar ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                        contentDescription = session.instructorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.dp, NeonPurple, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = session.instructorName,
                            color = IceWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐ ${session.instructorRating}", color = GoldCoin, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = " (${session.reviewsCount} reviews)", color = TextSubtle, fontSize = 11.sp)
                        }
                    }
                }

                // Fee
                Text(
                    text = "৳${String.format("%.0f", session.fee)}",
                    color = ElectricCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Session Title
            Text(
                text = session.title,
                color = IceWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = session.description,
                color = MetallicSilver,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Topics covered
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141424))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "টপিক: ${session.topics}", color = Color(0xFFD0BCFF), fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Booking button
            PillButton(
                text = "সেশন বুক করুন (${session.sessionType})",
                gradient = BrandPurpleMagentaGradient,
                onClick = onBook,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
