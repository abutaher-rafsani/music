package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PostEntity
import com.example.data.model.UserEntity
import com.example.data.model.WalletEntity
import com.example.ui.MainTab
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    wallet: WalletEntity?,
    userPosts: List<PostEntity>,
    language: String,
    onToggleLanguage: () -> Unit,
    onNavigateTab: (MainTab) -> Unit,
    onOpenCashout: () -> Unit,
    onResetDataClick: () -> Unit,
    onSyncOnlineDatabase: () -> Unit,
    onUpdateAvatar: (android.graphics.Bitmap) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onUpdateAvatar(bitmap)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            android.widget.Toast.makeText(context, "ক্যামেরা পারমিশন প্রয়োজন", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    var activeViewMode by remember { mutableIntStateOf(0) } // 0: My Profile, 1: Services Hub
    var activeProfileSubTab by remember { mutableStateOf("Posts") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Mode Switcher Header: My Profile vs Menu & Services Hub
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(18.dp))
                            .then(
                                if (activeViewMode == 0) Modifier.background(BrandCyanPurpleGradient)
                                else Modifier.background(Color.Transparent)
                            )
                            .clickable { activeViewMode = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "bn") "👤 আমার প্রোফাইল" else "👤 My Profile",
                            color = if (activeViewMode == 0) Color.White else IceWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(18.dp))
                            .then(
                                if (activeViewMode == 1) Modifier.background(BrandPurpleMagentaGradient)
                                else Modifier.background(Color.Transparent)
                            )
                            .clickable { activeViewMode = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "bn") "⚡ মেনু ও সার্ভিসেস" else "⚡ Services Hub",
                            color = if (activeViewMode == 1) Color.White else IceWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (activeViewMode == 0) {
            // VIEW A: MY PROFILE
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Cover & Avatar Header
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        AsyncImage(
                            model = currentUser?.coverUrl ?: "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200&auto=format&fit=crop&q=80",
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, ObsidianBlack.copy(alpha = 0.8f), ObsidianBlack)
                                    )
                                )
                        )

                        // Avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 20.dp)
                                .offset(y = 10.dp)
                        ) {
                            AsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(2.5.dp, ElectricCyan, CircleShape)
                            )
                            // Camera capture button badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                                    .clickable {
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(
                                                context,
                                                android.Manifest.permission.CAMERA
                                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                        ) {
                                            cameraLauncher.launch(null)
                                        } else {
                                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Capture Profile Picture",
                                    tint = ObsidianBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Profile Info
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.fullName ?: "User",
                                        color = IceWhite,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedBadge(size = 16.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    SpecialBadgePill(badgeText = currentUser?.specialBadge ?: "PRO")
                                }
                                Text(
                                    text = "@${currentUser?.username ?: "user"}",
                                    color = TextSubtle,
                                    fontSize = 12.sp
                                )
                            }

                            // WhatsApp Direct Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x2225D366))
                                    .border(1.dp, Color(0xFF25D366), RoundedCornerShape(14.dp))
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/8801711223344"))
                                        context.startActivity(intent)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "WhatsApp", color = Color(0xFF25D366), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentUser?.bio ?: "Full-Stack Dev & Scout Auditor | Dhanmondi, Dhaka",
                            color = MetallicSilver,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Metadata pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetaPill(icon = Icons.Default.Work, text = currentUser?.jobInfo ?: "Software Engineer")
                            MetaPill(icon = Icons.Default.School, text = currentUser?.university ?: "DU / BUET")
                            MetaPill(icon = Icons.Default.LocationOn, text = "${currentUser?.addressDistrict ?: "Dhaka"}, ${currentUser?.addressDivision ?: "ঢাকা"}")
                            if (!currentUser?.myReferralCode.isNullOrBlank()) {
                                MetaPill(icon = Icons.Default.CardGiftcard, text = "রেফারেল: ${currentUser?.myReferralCode}")
                            }
                            MetaPill(icon = Icons.Default.Person, text = currentUser?.userType ?: "ছাত্র / শিক্ষার্থী")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metrics (Followers, Following, Gigs completed)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MetricItem(count = 342, label = "Followers")
                            MetricItem(count = 180, label = "Following")
                            MetricItem(count = 14, label = "Gigs Done")
                        }
                    }
                }

                // Sub-Tabs: Posts, Photos, Favorites, Connections
                item {
                    val subTabs = listOf("Posts", "Photos", "Favorites", "Connections")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(width = 0.5.dp, color = ObsidianBorder)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        subTabs.forEach { tab ->
                            val isSel = activeProfileSubTab == tab
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { activeProfileSubTab = tab }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = tab,
                                    color = if (isSel) ElectricCyan else TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSel) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .height(2.dp)
                                            .background(ElectricCyan)
                                    )
                                }
                            }
                        }
                    }
                }

                // Sub-tab content
                items(userPosts) { post ->
                    FeedPostCard(
                        post = post,
                        onLike = {},
                        onFavorite = {},
                        onTip = {},
                        onComment = {},
                        onShare = {},
                        onAuthorClick = {}
                    )
                }
            }
        } else {
            // VIEW B: MENU & SERVICES HUB
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Compact Profile Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        hasNeonAccent = true
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, ElectricCyan, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.fullName ?: "User",
                                        color = IceWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedBadge(size = 14.dp)
                                }
                                Text(
                                    text = "@${currentUser?.username ?: "user"}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            SpecialBadgePill(badgeText = currentUser?.specialBadge ?: "PRO")
                        }
                    }
                }

                // Dual Balance Widget & 1-Tap Cashout Shortcut
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "ওয়ালেট ব্যালেন্স", color = TextSubtle, fontSize = 11.sp)
                                Text(
                                    text = "৳${String.format("%.2f", wallet?.cashBalance ?: 0.0)}",
                                    color = IceWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                 )
                                Text(
                                    text = "${wallet?.coinBalance ?: 0} 🪙 Coins",
                                    color = GoldCoin,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            PillButton(
                                text = "ক্যাশআউট 💸",
                                onClick = onOpenCashout,
                                height = 38.dp
                            )
                        }
                    }
                }

                // Ecosystem Service Grid
                item {
                    Text(
                        text = "ইকোসিস্টেম সার্ভিসেস",
                        color = TextSubtle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val services = listOf(
                        ServiceGridItemData("🔍 স্কাউট গিগস", "Scout Tasks", Icons.Default.Work) { onNavigateTab(MainTab.SCOUT) },
                        ServiceGridItemData("🎓 স্কিল শেয়ারিং", "Mentorship", Icons.Default.School) { onNavigateTab(MainTab.SCOUT) },
                        ServiceGridItemData("📞 ফোনবুক সিঙ্ক", "vCard Export", Icons.Default.ContactPhone) { onNavigateTab(MainTab.CONNECTOR) },
                        ServiceGridItemData("🛡️ কমিউনিটি সার্কেল", "Circles Hub", Icons.Default.Group) { onNavigateTab(MainTab.CIRCLE) },
                        ServiceGridItemData("🎬 রিলস হাব", "Short Videos", Icons.Default.PlayCircle) { onNavigateTab(MainTab.REELS) },
                        ServiceGridItemData("💳 ফিনটেক ওয়ালেট", "Escrow & Cashout", Icons.Default.AccountBalanceWallet) { onNavigateTab(MainTab.WALLET) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        services.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { item ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ServiceCard(item = item)
                                    }
                                }
                            }
                        }
                    }
                }

                // Cloud Storage Configuration & Status Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEEF2FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cloud,
                                            contentDescription = "Cloud Storage",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "ক্লাউড স্টোরেজ ইন্টিগ্রেশন",
                                            color = IceWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Google Cloud Storage & Supabase",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFECFDF5))
                                        .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Active 🟢",
                                        color = SuccessGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                    .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Primary GCS Bucket", color = TextSubtle, fontSize = 10.sp)
                                    Text("kliq-media-storage", color = IceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Media Support", color = TextSubtle, fontSize = 10.sp)
                                    Text("Photos & Videos (CDN)", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Cloud Firestore Data Repository Card (kliqbd)
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storage,
                                            contentDescription = "Cloud Firestore",
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "ক্লাউড ফায়ারস্টোর রিপোজিটরি",
                                            color = IceWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Firebase Firestore (kliqbd)",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFECFDF5))
                                        .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Sync Live 🟢",
                                        color = SuccessGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                             Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                    .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Managed Collections", color = TextSubtle, fontSize = 10.sp)
                                    Text("users • scout_gigs", color = IceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Cloud Database", color = TextSubtle, fontSize = 10.sp)
                                    Text("kliqbd.firebaseio.com", color = Color(0xFFD97706), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = onSyncOnlineDatabase,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = IceWhite),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "🔄 অনলাইন ডাটাবেজ সিঙ্ক করুন (Sync Now)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Language Switcher: 🇧🇩 বাংলা / 🇬🇧 English
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleLanguage() },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌐", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "ভাষা পরিবর্তন (Language)",
                                        color = IceWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language == "bn") "বর্তমান: বাংলা 🇧🇩" else "Current: English 🇬🇧",
                                        color = ElectricCyan,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = ElectricCyan)
                        }
                    }
                }

                // Purge All Test Data safety button
                item {
                    Button(
                        onClick = onResetDataClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22111A),
                            contentColor = NeonHotPink
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonHotPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .testTag("purge_test_data_button")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Purge All Test Data (রিসেট ডাটাবেজ)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Logout Button
                item {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF141424),
                            contentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "লগআউট করুন (Log Out)")
                    }
                }
            }
        }
    }
}

data class ServiceGridItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ServiceCard(item: ServiceGridItemData) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Icon(imageVector = item.icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = item.title, color = IceWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = item.subtitle, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun MetaPill(icon: ImageVector, text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF161628))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = MetallicSilver, fontSize = 11.sp)
        }
    }
}

@Composable
fun MetricItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), color = IceWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(text = label, color = TextSubtle, fontSize = 11.sp)
    }
}
