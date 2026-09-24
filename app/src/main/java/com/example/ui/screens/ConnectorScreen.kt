package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPhoneContactEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ConnectorScreen(
    contacts: List<UserPhoneContactEntity>,
    onInviteContact: (UserPhoneContactEntity) -> Unit,
    onExportVcf: (List<UserPhoneContactEntity>) -> Unit,
    onChatWithMatched: (String) -> Unit
) {
    val context = LocalContext.current
    val matched = contacts.filter { it.matchedUserId != null }
    val notRegistered = contacts.filter { it.matchedUserId == null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Hero Card: Live Sync Status & vCard Export
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            hasNeonAccent = true
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulsingSyncDot(color = SuccessGreen, size = 10.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "লাইভ ফোনবুক কানেক্টর",
                            color = IceWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x2200D9FF))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${matched.size} কানেক্টেড",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "আপনার ফোনবুকের নাম্বারগুলো স্বয়ংক্রিয়ভাবে KLIQ ডাটাবেজের সাথে নরমালাইজড (+৮৮০১৭...) করে ম্যাচ করা হয়েছে।",
                    color = MetallicSilver,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "ফোনবুকে এক্সপোর্ট (.vcf)" CTA
                PillButton(
                    text = "ফোনবুকে এক্সপোর্ট (.vcf)",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    gradient = BrandCyanPurpleGradient,
                    textColor = ObsidianBlack,
                    onClick = { onExportVcf(matched) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_vcf_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact lists
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Section 1: Matched Contacts on KLIQ
            item {
                Text(
                    text = "KLIQ ইউজার ম্যাচ (${matched.size})",
                    color = ElectricCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(matched) { contact ->
                MatchedContactRow(contact = contact, onChat = { onChatWithMatched(contact.matchedUserId ?: "") })
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Section 2: Unregistered Contacts - Invite Engine
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ইনভাইট করুন ও কয়েন জিতুন",
                        color = GoldCoin,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+৫০ Coins/Friend",
                        color = GoldCoin,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(notRegistered) { contact ->
                UnregisteredContactRow(contact = contact, onInvite = { onInviteContact(contact) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MatchedContactRow(
    contact: UserPhoneContactEntity,
    onChat: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BrandCyanPurpleGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.contactName.take(1),
                        color = IceWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.contactName,
                            color = IceWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(size = 14.dp)
                    }
                    Text(
                        text = contact.contactPhone,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Chat button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x2200D9FF))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable { onChat() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Message",
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "মেসেজ",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UnregisteredContactRow(
    contact: UserPhoneContactEntity,
    onInvite: () -> Unit
) {
    var invited by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.contactName.take(1),
                        color = TextMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = contact.contactName,
                        color = IceWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = contact.contactPhone,
                        color = TextSubtle,
                        fontSize = 11.sp
                    )
                }
            }

            // Invite button with +50 Coins reward
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (invited) Color(0x2200E676) else Color(0x22FFB800)
                    )
                    .border(
                        1.dp,
                        if (invited) SuccessGreen else GoldCoin.copy(alpha = 0.5f),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        if (!invited) {
                            invited = true
                            onInvite()
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (invited) "পাঠানো হয়েছে ✓" else "ইনভাইট (+50 🪙)",
                    color = if (invited) SuccessGreen else GoldCoin,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
