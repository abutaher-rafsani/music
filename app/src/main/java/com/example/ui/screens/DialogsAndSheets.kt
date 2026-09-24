package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PostCommentEntity
import com.example.data.model.PostEntity
import com.example.data.model.ScoutGigEntity
import com.example.data.model.UserEntity
import com.example.data.remote.GoogleCloudStorageService
import com.example.data.remote.SupabaseStorageService
import com.example.data.repository.KliqRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

// 1. New Post Modal with Google Cloud Storage & Supabase Bucket Photo & Video Upload
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String?, content: String, mediaUrl: String?, mediaType: String, visibility: String, circleType: String?, circleName: String?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val gcsService = remember { GoogleCloudStorageService(context) }
    val supabaseService = remember { SupabaseStorageService(context) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mediaUrl by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf("image") } // "image" or "video"
    var storageProvider by remember { mutableStateOf("GCS") } // "GCS" or "Supabase"
    var visibility by remember { mutableStateOf("public") }
    var selectedCircle by remember { mutableStateOf("Varsity Circle") }

    // Upload & Media selection states
    var selectedLocalUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }
    var uploadedCloudUrl by remember { mutableStateOf<String?>(null) }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedLocalUri = uri
            mediaType = "image"
            isUploading = true
            val providerName = if (storageProvider == "GCS") "Google Cloud Storage (kliq-media-storage)" else "Supabase Storage (kliq-media)"
            uploadStatusMessage = "Uploading photo to $providerName..."
            coroutineScope.launch {
                val result = if (storageProvider == "GCS") {
                    gcsService.uploadMedia(uri, "image")
                } else {
                    supabaseService.uploadMedia(uri, "image")
                }
                isUploading = false
                result.onSuccess { url ->
                    mediaUrl = url
                    uploadedCloudUrl = url
                    uploadStatusMessage = "✓ Photo successfully uploaded to $storageProvider! Cloud URL ready."
                }.onFailure { err ->
                    uploadStatusMessage = "Upload notice: ${err.message}"
                }
            }
        }
    }

    // Video Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedLocalUri = uri
            mediaType = "video"
            isUploading = true
            val providerName = if (storageProvider == "GCS") "Google Cloud Storage (kliq-media-storage)" else "Supabase Storage (kliq-media)"
            uploadStatusMessage = "Uploading video to $providerName..."
            coroutineScope.launch {
                val result = if (storageProvider == "GCS") {
                    gcsService.uploadMedia(uri, "video")
                } else {
                    supabaseService.uploadMedia(uri, "video")
                }
                isUploading = false
                result.onSuccess { url ->
                    mediaUrl = url
                    uploadedCloudUrl = url
                    uploadStatusMessage = "✓ Video successfully uploaded to $storageProvider! Cloud URL ready."
                }.onFailure { err ->
                    uploadStatusMessage = "Upload notice: ${err.message}"
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "নতুন পোস্ট তৈরি করুন",
                        color = IceWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Google Cloud & Supabase Photo/Video Storage",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("শিরোনাম (ঐচ্ছিক)", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("আপনার মতামত শেয়ার করুন... *", color = TextMuted) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cloud Storage Provider Selector (Google Cloud vs Supabase)
                Text(
                    text = "ক্লাউড স্টোরেজ প্রোভাইডার:",
                    color = IceWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Google Cloud Storage Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (storageProvider == "GCS") Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                            .border(
                                width = if (storageProvider == "GCS") 1.5.dp else 1.dp,
                                color = if (storageProvider == "GCS") ElectricCyan else ObsidianBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { storageProvider = "GCS" }
                            .padding(vertical = 8.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Google Cloud",
                                tint = if (storageProvider == "GCS") ElectricCyan else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Cloud",
                                color = if (storageProvider == "GCS") ElectricCyan else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (storageProvider == "GCS") FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Supabase Storage Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (storageProvider == "Supabase") Color(0xFFECFDF5) else Color(0xFFF8FAFC))
                            .border(
                                width = if (storageProvider == "Supabase") 1.5.dp else 1.dp,
                                color = if (storageProvider == "Supabase") SuccessGreen else ObsidianBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { storageProvider = "Supabase" }
                            .padding(vertical = 8.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Supabase",
                                tint = if (storageProvider == "Supabase") SuccessGreen else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Supabase",
                                color = if (storageProvider == "Supabase") SuccessGreen else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (storageProvider == "Supabase") FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Photo & Video Upload Buttons
                Text(
                    text = "ফটো ও ভিডিও সিলেক্ট করুন (${if (storageProvider == "GCS") "Google Cloud" else "Supabase"}):",
                    color = IceWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Upload Photo Button
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEEF2FF),
                            contentColor = ElectricCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Photo", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ছবি আপলোড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Upload Video Button
                    Button(
                        onClick = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF3E8FF),
                            contentColor = NeonPurple
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = "Video", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ভিডিও আপলোড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Upload Progress Indicator
                if (isUploading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ElectricCyan,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uploadStatusMessage ?: "Uploading to Cloud Storage...",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Uploaded Cloud Status Banner
                if (!isUploading && uploadStatusMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECFDF5), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uploadStatusMessage!!,
                                color = Color(0xFF065F46),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Selected / Uploaded Media Preview
                if (mediaUrl.isNotBlank() || selectedLocalUri != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = selectedLocalUri ?: mediaUrl,
                            contentDescription = "Selected Media Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Cloud Storage Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp)
                                .background(Color(0xCC0F172A), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (storageProvider == "GCS") {
                                    if (mediaType == "video") "VIDEO • GOOGLE CLOUD STORAGE" else "PHOTO • GOOGLE CLOUD STORAGE"
                                } else {
                                    if (mediaType == "video") "VIDEO • SUPABASE BUCKET" else "PHOTO • SUPABASE BUCKET"
                                },
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collected Cloud Storage URL
                OutlinedTextField(
                    value = mediaUrl,
                    onValueChange = { mediaUrl = it },
                    label = { Text("Cloud Storage URL (Database Saved)", color = TextMuted) },
                    placeholder = { Text("https://storage.googleapis.com/kliq-media-storage/...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Visibility Selector
                Text(
                    text = "কে দেখতে পারবে:",
                    color = IceWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("public", "circle", "private").forEach { vis ->
                        val isSel = visibility == vis
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) ElectricCyan else ObsidianSurface)
                                .border(1.dp, if (isSel) ElectricCyan else ObsidianBorder, RoundedCornerShape(12.dp))
                                .clickable { visibility = vis }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = vis.replaceFirstChar { it.uppercase() },
                                color = if (isSel) Color.White else IceWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        val circleType = if (visibility == "circle") "varsity" else null
                        val circleName = if (visibility == "circle") selectedCircle else null
                        val finalUrl = mediaUrl.takeIf { it.isNotBlank() }
                            ?: "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=800&auto=format&fit=crop&q=80"
                        onSubmit(
                            title.takeIf { it.isNotBlank() },
                            content,
                            finalUrl,
                            mediaType,
                            visibility,
                            circleType,
                            circleName
                        )
                    }
                },
                enabled = !isUploading,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "পোস্ট করুন 🚀", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "বাতিল", color = TextMuted)
            }
        }
    )
}

// 2. Comment Sheet Modal
@Composable
fun CommentSheetDialog(
    postId: String,
    repository: KliqRepository,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    val commentsFlow = remember(postId) { repository.getCommentsForPost(postId) }
    val comments by commentsFlow.collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(text = "মন্তব্যসমূহ (${comments.size})", color = IceWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Text(text = "এখনো কোন মন্তব্য নেই। প্রথম মন্তব্যটি করুন!", color = TextMuted, fontSize = 12.sp)
                        }
                    } else {
                        items(comments) { c ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column {
                                    Text(text = c.userName, color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = c.commentText, color = IceWhite, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("একটি মন্তব্য লিখুন...", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "বন্ধ করুন", color = TextMuted)
            }
        }
    )
}

// 3. Instant Cashout Modal
@Composable
fun CashoutDialog(
    currentCash: Double,
    onDismiss: () -> Unit,
    onSubmitCashout: (amount: Double, method: String, account: String) -> Unit
) {
    var amount by remember { mutableStateOf("500") }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf("01711223344") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val methods = listOf("bKash", "Nagad", "Rocket", "Bank Wire", "USDT (TRC20)")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💸 ইনস্ট্যান্ট ক্যাশআউট", color = IceWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2200D9FF))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "৳${String.format("%.2f", currentCash)}", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "উইথড্রয়াল মেথড বেছে নিন:", color = MetallicSilver, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.forEach { m ->
                        val isSel = selectedMethod == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) ElectricCyan else Color(0xFF141426))
                                .border(1.dp, if (isSel) ElectricCyan else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                .clickable { selectedMethod = m }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = m,
                                color = if (isSel) ObsidianBlack else IceWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("অ্যাকাউন্ট / ওয়ালেট নাম্বার *", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("টাকার পরিমাণ (নূন্যতম ৳৫০) *", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMsg!!, color = NeonHotPink, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtVal = amount.toDoubleOrNull()
                    if (amtVal == null || amtVal < 50.0) {
                        errorMsg = "সর্বনিম্ন ক্যাশআউট ৳৫০.০০"
                    } else if (amtVal > currentCash) {
                        errorMsg = "পর্যাপ্ত ব্যালেন্স নেই!"
                    } else if (accountNumber.isBlank()) {
                        errorMsg = "অ্যাকাউন্ট নাম্বার দিন"
                    } else {
                        onSubmitCashout(amtVal, selectedMethod, accountNumber)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = ObsidianBlack)
            ) {
                Text(text = "উইথড্র কনফার্ম করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "বাতিল", color = TextMuted)
            }
        }
    )
}

// 4. Create Scout Gig Modal
@Composable
fun CreateGigDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, category: String, location: String, rewardCash: Double) -> Unit
) {
    var title by remember { mutableStateOf("ধানমন্ডি ২ নম্বর রোডের ফার্মেসি প্রাইস অডিট") }
    var description by remember { mutableStateOf("৩টি ফার্মেসি ভিজিট করে নির্দিষ্ট ওষুধের বর্তমান এমআরপি যাচাই ও রসিদের ছবি আপলোড করতে হবে।") }
    var category by remember { mutableStateOf("Shop & Price Audit") }
    var location by remember { mutableStateOf("Dhanmondi, Dhaka") }
    var rewardCash by remember { mutableStateOf("150") }

    val categories = listOf("Shop & Price Audit", "Local Verification", "Photo & Proof", "Stock Check", "Survey")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(text = "নতুন স্কাউট গিগ পোস্ট করুন 🔍", color = IceWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "ক্যাটাগরি:", color = MetallicSilver, fontSize = 11.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { c ->
                        val isSel = category == c
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) ElectricCyan else Color(0xFF161628))
                                .clickable { category = c }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = c, color = if (isSel) ObsidianBlack else IceWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("গিগ শিরোনাম *") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("কাজের বিস্তারিত বিবরণ *") },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("লোকেশন") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = rewardCash,
                        onValueChange = { rewardCash = it },
                        label = { Text("বাউন্টি (৳)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reward = rewardCash.toDoubleOrNull() ?: 100.0
                    onSubmit(title, description, category, location, reward)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = ObsidianBlack)
            ) {
                Text(text = "এসক্রো লক ও পাবলিশ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "বাতিল", color = TextMuted) }
        }
    )
}

// 5. Submit Proof Modal
@Composable
fun SubmitProofDialog(
    gig: ScoutGigEntity,
    onDismiss: () -> Unit,
    onSubmitProof: (notes: String, proofUrl: String) -> Unit
) {
    var notes by remember { mutableStateOf("৩টি ফার্মেসি চেক করা হয়েছে, রসিদ এবং মূল কাউন্টারের ছবি সংযুক্ত করা হলো।") }
    var proofUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1576602976047-174e57a47881?w=800&auto=format&fit=crop&q=80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(text = "কাজের প্রমাণ সাবমিট করুন 📸", color = IceWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = gig.title, color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "বাউন্টি: ৳${String.format("%.0f", gig.rewardCash)} (অনুমোদন হলেই ক্যাশ যুক্ত হবে)", color = GoldCoin, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("কাজের বিবরণ ও নোট *") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = proofUrl,
                    onValueChange = { proofUrl = it },
                    label = { Text("প্রমাণ ছবির URL (Supabase CDN)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitProof(notes, proofUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = ObsidianBlack)
            ) {
                Text(text = "সাবমিট ও ৳${String.format("%.0f", gig.rewardCash)} নিন ✓", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "বাতিল", color = TextMuted) }
        }
    )
}

// 6. Author Profile Preview Modal
@Composable
fun AuthorProfileModal(
    user: UserEntity,
    onDismiss: () -> Unit,
    onFavorite: () -> Unit,
    onConnect: () -> Unit,
    onDirectMessage: () -> Unit
) {
    var isFavorited by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = user.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                    contentDescription = user.fullName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(2.dp, ElectricCyan, CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.fullName, color = IceWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(size = 14.dp)
                    }
                }

                Text(text = "@${user.username}", color = TextSubtle, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(6.dp))

                SpecialBadgePill(badgeText = user.specialBadge ?: "PRO")

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = user.bio ?: "Active community member & scout on KLIQ ecosystem.",
                    color = MetallicSilver,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Interested (Favorite), Connect, Direct Message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Interested / Favorite
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFavorited) GoldCoin.copy(alpha = 0.2f) else Color(0xFF161628))
                            .border(1.dp, if (isFavorited) GoldCoin else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                            .clickable {
                                isFavorited = !isFavorited
                                onFavorite()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFavorited) "⭐ সেভড" else "⭐ ফেভারিট",
                            color = if (isFavorited) GoldCoin else IceWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Connect
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isConnected) SuccessGreen.copy(alpha = 0.2f) else Color(0xFF161628))
                            .border(1.dp, if (isConnected) SuccessGreen else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                            .clickable {
                                isConnected = !isConnected
                                onConnect()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isConnected) "✓ কানেক্টেড" else "+ কানেক্ট",
                            color = if (isConnected) SuccessGreen else ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Direct Message
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricCyan)
                            .clickable {
                                onDirectMessage()
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "মেসেজ",
                            color = ObsidianBlack,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = "বন্ধ করুন", color = TextMuted) }
        }
    )
}

// 7. Reset Data Confirmation Dialog
@Composable
fun ResetDataConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(text = "ডাটাবেজ ও টেস্ট ডাটা রিসেট করবেন?", color = NeonHotPink, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                text = "এটি আপনার সমস্ত লোকাল Room/MySQL টেস্ট রো, ওয়ালেট ব্যালেন্স এবং চ্যাট হিস্ট্রি রিসেট করে প্রডাকশন সিড স্টেট পুনরুদ্ধার করবে।",
                color = MetallicSilver,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = NeonHotPink, contentColor = IceWhite)
            ) {
                Text(text = "হ্যাঁ, রিসেট করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "বাতিল", color = TextMuted) }
        }
    )
}

// 8. Phone OTP Reset Modal
@Composable
fun PhoneOtpResetModal(
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var phone by remember { mutableStateOf("+8801711223344") }
    var otpCode by remember { mutableStateOf("7294") }
    var isOtpSent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(text = "পাসওয়ার্ড রিসেট (Phone OTP)", color = IceWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "আপনার রেজিস্টার্ড মোবাইল নাম্বারে ৪-ডিজিটের ভেরিফিকেশন কোড পাঠানো হবে।", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("ফোন নাম্বার") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isOtpSent) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("৪ ডিজিটের OTP কোড") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isOtpSent) {
                        isOtpSent = true
                    } else {
                        onVerified()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = ObsidianBlack)
            ) {
                Text(text = if (!isOtpSent) "OTP কোড পাঠান" else "যাচাই করুন ও রিসেট করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "বাতিল", color = TextMuted) }
        }
    )
}

// 9. Offer Skill Sheet Modal
@Composable
fun OfferSkillDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, category: String, fee: Double, sessionType: String, description: String, topics: String) -> Unit
) {
    var title by remember { mutableStateOf("মোবাইল প্রডাক্ট ফটোগ্রাফি ও লাইটিং মাস্টারক্লাস") }
    var category by remember { mutableStateOf("Photography") }
    var fee by remember { mutableStateOf("300") }
    var sessionType by remember { mutableStateOf("1-on-1 Mentorship") }
    var description by remember { mutableStateOf("স্মার্টফোন দিয়ে কীভাবে ই-কমার্সের জন্য প্রফেশনাল প্রডাক্ট ছবি ও পোর্টফোলিও বানাবেন হাতে কলমে শিখুন।") }
    var topics by remember { mutableStateOf("Natural Light, Framing, Snapseed, Angle") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(text = "স্কিল শেয়ারিং প্যাকেজ অফার করুন 🎓", color = IceWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("প্যাকেজ শিরোনাম *") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fee,
                        onValueChange = { fee = it },
                        label = { Text("ফি (৳)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("ক্যাটাগরি") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("বিস্তারিত বিবরণ *") },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = topics,
                    onValueChange = { topics = it },
                    label = { Text("টপিকসমূহ (কমা দিয়ে আলাদা করুন)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val feeVal = fee.toDoubleOrNull() ?: 200.0
                    onSubmit(title, category, feeVal, sessionType, description, topics)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = IceWhite)
            ) {
                Text(text = "প্রকাশ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "বাতিল", color = TextMuted) }
        }
    )
}

/**
 * Notification Center Modal UI for gig opportunities, profile interactions, and kliq live activity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterModal(
    onDismiss: () -> Unit,
    onNavigateGig: () -> Unit,
    onNavigateLive: () -> Unit
) {
    var filterType by remember { mutableStateOf("All") } // All, Gigs, Interactions, Live

    val allNotifications = listOf(
        NotificationItem(
            id = "1",
            type = "gig",
            title = "🚀 New Scout Gig Opportunity",
            description = "Full Stack Android Developer needed in Gulshan-2. Budget: ৳15,000",
            timeAgo = "2 mins ago",
            isUnread = true
        ),
        NotificationItem(
            id = "2",
            type = "interaction",
            title = "❤️ Profile Interaction",
            description = "Tanvir Ahmed liked your post & sent a ৳100 tip!",
            timeAgo = "18 mins ago",
            isUnread = true
        ),
        NotificationItem(
            id = "3",
            type = "live",
            title = "🔴 Kliq Live Started",
            description = "Tech Talk & AI Q&A is live now hosted by Sarah Khan. Join stream!",
            timeAgo = "45 mins ago",
            isUnread = true
        ),
        NotificationItem(
            id = "4",
            type = "gig",
            title = "✅ Gig Application Approved",
            description = "Your proof for 'UI/UX Brand Redesign' gig was verified. Payout credited!",
            timeAgo = "2 hours ago",
            isUnread = false
        ),
        NotificationItem(
            id = "5",
            type = "interaction",
            title = "💬 New Comment",
            description = "Rafiqul Islam commented on your status: 'Awesome update bro!'",
            timeAgo = "5 hours ago",
            isUnread = false
        )
    )

    val filteredNotifications = when (filterType) {
        "Gigs" -> allNotifications.filter { it.type == "gig" }
        "Interactions" -> allNotifications.filter { it.type == "interaction" }
        "Live" -> allNotifications.filter { it.type == "live" }
        else -> allNotifications
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCardBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandCyanPurpleGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = IceWhite, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Notification Center", color = IceWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Gigs", "Interactions", "Live").forEach { filter ->
                        FilterChip(
                            selected = filterType == filter,
                            onClick = { filterType = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = IceWhite,
                                containerColor = ObsidianSurface,
                                labelColor = MetallicSilver
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { item ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (item.type == "gig") {
                                        onDismiss()
                                        onNavigateGig()
                                    } else if (item.type == "live") {
                                        onDismiss()
                                        onNavigateLive()
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            hasNeonAccent = item.isUnread
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (item.type) {
                                                "gig" -> GoldCoin.copy(alpha = 0.2f)
                                                "interaction" -> NeonMagenta.copy(alpha = 0.2f)
                                                else -> ElectricCyan.copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item.type) {
                                            "gig" -> Icons.Default.Work
                                            "interaction" -> Icons.Default.Favorite
                                            else -> Icons.Default.LiveTv
                                        },
                                        contentDescription = null,
                                        tint = when (item.type) {
                                            "gig" -> GoldCoin
                                            "interaction" -> NeonMagenta
                                            else -> ElectricCyan
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = item.title, color = IceWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        if (item.isUnread) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricCyan)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = item.description, color = MetallicSilver, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.timeAgo, color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = IceWhite)
            ) {
                Text(text = "সব পঠিত হিসেবে চিহ্নিত করুন", fontWeight = FontWeight.Bold)
            }
        }
    )
}

data class NotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val isUnread: Boolean
)

// 7. Edit Profile Modal Dialog (Bio, Occupation, Profile Photo with File Picker & Firestore Save)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onSaveProfile: (newBio: String, newOccupation: String, newAvatarUrl: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val gcsService = remember { GoogleCloudStorageService(context) }

    var bio by remember { mutableStateOf(currentUser.bio ?: "") }
    var occupation by remember { mutableStateOf(currentUser.jobInfo ?: "Software Engineer") }
    var avatarUrl by remember { mutableStateOf(currentUser.avatarUrl ?: "") }
    var selectedLocalUri by remember { mutableStateOf<Uri?>(null) }
    
    var isUploading by remember { mutableStateOf(false) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedLocalUri = uri
            isUploading = true
            uploadStatus = "Uploading profile photo to Google Cloud Storage..."
            coroutineScope.launch {
                val result = gcsService.uploadMedia(uri, "image")
                isUploading = false
                result.onSuccess { url ->
                    avatarUrl = url
                    uploadStatus = "✓ Profile photo successfully uploaded to GCS!"
                }.onFailure { err ->
                    uploadStatus = "Upload notice: ${err.message}"
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonPin,
                        contentDescription = "Edit Profile",
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "প্রোফাইল এডিট করুন",
                        color = IceWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Firestore Sync & GCS Photo Upload",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar Preview & Picker Button
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, ElectricCyan, CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        AsyncImage(
                            model = selectedLocalUri ?: avatarUrl.takeIf { it.isNotBlank() } ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                            contentDescription = "Avatar Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text(text = "📸 নতুন প্রোফাইল ছবি সিলেক্ট করুন", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isUploading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ElectricCyan, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = uploadStatus ?: "Uploading...", color = ElectricCyan, fontSize = 11.sp)
                    }
                }

                if (!isUploading && uploadStatus != null) {
                    Text(text = uploadStatus!!, color = SuccessGreen, fontSize = 11.sp)
                }

                // Occupation / Job Info
                OutlinedTextField(
                    value = occupation,
                    onValueChange = { 
                        occupation = it
                        validationError = null
                    },
                    label = { Text("পেশা / পদবি (Occupation) *", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { 
                        bio = it
                        validationError = null
                    },
                    label = { Text("বায়ো / পরিচিতি (Bio) *", color = TextMuted) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Avatar URL fallback text field
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Avatar Image URL", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = IceWhite,
                        unfocusedTextColor = IceWhite,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (validationError != null) {
                    Text(text = validationError!!, color = NeonHotPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (occupation.isBlank()) {
                        validationError = "পেশা (Occupation) খালি রাখা যাবে না।"
                    } else if (bio.isBlank()) {
                        validationError = "বায়ো (Bio) খালি রাখা যাবে না।"
                    } else {
                        val finalAvatar = avatarUrl.takeIf { it.isNotBlank() } ?: currentUser.avatarUrl ?: ""
                        onSaveProfile(bio, occupation, finalAvatar)
                    }
                },
                enabled = !isUploading,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Firestore-এ সেভ করুন 💾", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "বাতিল", color = TextMuted)
            }
        }
    )
}

