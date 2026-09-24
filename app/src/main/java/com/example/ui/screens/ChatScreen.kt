package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.repository.KliqRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ChatScreen(
    conversations: List<ConversationEntity>,
    activeConvId: String?,
    repository: KliqRepository,
    onOpenConversation: (String) -> Unit,
    onCloseConversation: () -> Unit,
    onSendMessage: (String, String, String, Boolean, Double?) -> Unit,
    onReleaseEscrow: (String, Double) -> Unit
) {
    if (activeConvId != null) {
        val conv = conversations.find { it.id == activeConvId }
        val messagesFlow = remember(activeConvId) { repository.getMessagesFlow(activeConvId) }
        val messages by messagesFlow.collectAsState(initial = emptyList())

        ActiveChatWindow(
            conversation = conv,
            messages = messages,
            currentUserId = repository.currentUserId,
            onBack = onCloseConversation,
            onSend = { text, isEscrow, amount ->
                val recipient = if (conv != null && conv.participantOne != repository.currentUserId) {
                    conv.participantOne
                } else if (conv != null) {
                    conv.participantTwo
                } else {
                    ""
                }
                onSendMessage(activeConvId, recipient, text, isEscrow, amount)
            },
            onReleaseEscrow = onReleaseEscrow
        )
    } else {
        ConversationListView(
            conversations = conversations,
            onSelect = onOpenConversation
        )
    }
}

@Composable
fun ConversationListView(
    conversations: List<ConversationEntity>,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "মেসেঞ্জার (REAL-TIME CHAT)",
                color = IceWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2200E676))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingSyncDot(color = SuccessGreen, size = 6.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "অনলাইন", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(conversations, key = { it.id }) { conv ->
                ConversationItemRow(conv = conv, onClick = { onSelect(conv.id) })
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    conv: ConversationEntity,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box {
                    AsyncImage(
                        model = conv.participantAvatar ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                        contentDescription = conv.participantName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.dp, ElectricCyan, CircleShape)
                    )
                    // Online dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                            .border(1.dp, ObsidianBlack, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = conv.participantName,
                            color = IceWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (conv.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge(size = 14.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = conv.lastMessage ?: "ছবি পাঠিয়েছেন",
                        color = if (conv.unreadCount > 0) IceWhite else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }

            if (conv.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(NeonMagenta),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conv.unreadCount.toString(),
                        color = IceWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveChatWindow(
    conversation: ConversationEntity?,
    messages: List<MessageEntity>,
    currentUserId: String,
    onBack: () -> Unit,
    onSend: (String, Boolean, Double?) -> Unit,
    onReleaseEscrow: (String, Double) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showOfferDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = IceWhite)
                }

                AsyncImage(
                    model = conversation?.participantAvatar ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, ElectricCyan, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = conversation?.participantName ?: "User",
                        color = IceWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Active now", color = SuccessGreen, fontSize = 11.sp)
                }
            }

            // Create Escrow Offer Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x227928CA))
                    .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                    .clickable { showOfferDialog = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🔒 এসক্রো অফার",
                    color = Color(0xFFD0BCFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId
                MessageBubble(
                    message = msg,
                    isMe = isMe,
                    onReleaseEscrow = { onReleaseEscrow(msg.id, msg.escrowOfferAmount ?: 0.0) }
                )
            }
        }

        // Input Field Dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 0.5.dp, color = ObsidianBorder)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = TextStyle(color = IceWhite, fontSize = 14.sp),
                    cursorBrush = SolidColor(ElectricCyan),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (inputText.isEmpty()) {
                            Text(text = "মেসেজ লিখুন...", color = TextSubtle, fontSize = 13.sp)
                        }
                        inner()
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSend(inputText, false, null)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = ObsidianBlack, modifier = Modifier.size(18.dp))
            }
        }
    }

    // Escrow Offer Dialog
    if (showOfferDialog) {
        var offerTitle by remember { mutableStateOf("ধানমন্ডি শপ অডিট ও রিপোর্ট") }
        var offerAmount by remember { mutableStateOf("250") }

        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            containerColor = ObsidianSurface,
            title = { Text(text = "চ্যাটে কাস্টম এসক্রো অফার পাঠান", color = IceWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "টাকা আপনার ওয়ালেট থেকে এসক্রোতে লক থাকবে। কাজ শেষে রিলিজ হবে।", color = MetallicSilver, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = offerTitle,
                        onValueChange = { offerTitle = it },
                        label = { Text("কাজের বিবরণ") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = offerAmount,
                        onValueChange = { offerAmount = it },
                        label = { Text("টাকার পরিমাণ (৳)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = offerAmount.toDoubleOrNull() ?: 100.0
                        onSend("🔒 কাস্টম এসক্রো চুক্তি: $offerTitle (৳$amt)", true, amt)
                        showOfferDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("লক ও সেন্ড করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("বাতিল", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun MessageBubble(
    message: MessageEntity,
    isMe: Boolean,
    onReleaseEscrow: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (message.isEscrowContract) {
            // Escrow Contract Box
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E1030))
                    .border(1.dp, NeonPurple, RoundedCornerShape(18.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "এসক্রো চুক্তি লকড", color = Color(0xFFD0BCFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = message.messageText, color = IceWhite, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isMe && message.escrowOfferStatus == "locked") {
                        Button(
                            onClick = onReleaseEscrow,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "কাজ সম্পন্ন - টাকা রিলিজ করুন ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = if (message.escrowOfferStatus == "released") "✓ ফান্ড রিলিজ সম্পন্ন হয়েছে" else "স্ট্যাটাস: ${message.escrowOfferStatus ?: "locked"}",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Regular Chat Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 16.dp
                        )
                    )
                    .background(
                        if (isMe) ElectricCyan.copy(alpha = 0.25f) else Color(0xFF181828)
                    )
                    .border(
                        1.dp,
                        if (isMe) ElectricCyan.copy(alpha = 0.5f) else Color(0x22FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.messageText,
                    color = IceWhite,
                    fontSize = 13.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp, end = 4.dp)
        ) {
            Text(text = "10:42 AM", color = TextSubtle, fontSize = 9.sp)
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.DoneAll, contentDescription = "Read", tint = ElectricCyan, modifier = Modifier.size(12.dp))
            }
        }
    }
}
