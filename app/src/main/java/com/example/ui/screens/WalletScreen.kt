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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletEntity
import com.example.data.model.WalletTransactionEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    wallet: WalletEntity?,
    transactions: List<WalletTransactionEntity>,
    onOpenCashoutSheet: () -> Unit,
    onConvertCoins: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Cash In", "Cash Out", "Escrow", "Tips")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Virtual Obsidian Card with Holographic Titanium Finish
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Color(0xFF334155)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = Color(0xFF475569),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: KLIQ Titanium Card & NFC Signal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💳", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "KLIQ TITANIUM",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NFC",
                            color = Color(0xFF67E8F9),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "NFC Signal",
                            tint = Color(0xFF67E8F9),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Middle: Available Balance
                Column {
                    Text(
                        text = "মোট ক্যাশ ব্যালেন্স (AVAILABLE)",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "৳${String.format("%.2f", wallet?.cashBalance ?: 0.0)}",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                // Bottom Row: Escrow Locked, Coins & Masked Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Escrow Locked
                        Column {
                            Text(text = "এসক্রো লকড", color = Color(0xFF94A3B8), fontSize = 9.sp)
                            Text(
                                text = "৳${String.format("%.2f", wallet?.escrowLocked ?: 0.0)}",
                                color = Color(0xFFC084FC),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Engagement Coins
                        Column {
                            Text(text = "এনগেজমেন্ট কয়েন", color = Color(0xFF94A3B8), fontSize = 9.sp)
                            Text(
                                text = "${wallet?.coinBalance ?: 0} 🪙",
                                color = Color(0xFFFBBF24),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "•••• 8821",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Actions Row: Instant Cashout & Coin Exchange
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cashout Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandCyanPurpleGradient)
                    .clickable { onOpenCashoutSheet() }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CallMade, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ইনস্ট্যান্ট ক্যাশআউট 💸",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Coin Exchange Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFEF3C7))
                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                    .clickable { onConvertCoins() }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "১-ট্যাপ কয়েন কনভার্ট",
                        color = Color(0xFFB45309),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Immutable Transaction Ledger Section
        Text(
            text = "লেনদেন হিস্ট্রি (IMMUTABLE LEDGER)",
            color = TextSubtle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ledger Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ElectricCyan else ObsidianSurface)
                        .border(1.dp, if (isSelected) ElectricCyan else ObsidianBorder, RoundedCornerShape(12.dp))
                    .clickable { selectedFilter = filter }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else IceWhite,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val filteredTransactions = remember(transactions, selectedFilter) {
            when (selectedFilter) {
                "Cash In" -> transactions.filter { it.direction == "in" }
                "Cash Out" -> transactions.filter { it.direction == "out" }
                "Escrow" -> transactions.filter { it.type.contains("escrow", ignoreCase = true) }
                "Tips" -> transactions.filter { it.type.contains("tip", ignoreCase = true) }
                else -> transactions
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTransactions, key = { it.id }) { tx ->
                TransactionLedgerRow(tx = tx)
            }
        }
    }
}

@Composable
fun TransactionLedgerRow(tx: WalletTransactionEntity) {
    val isCredit = tx.direction == "in"
    val statusColor = when (tx.status) {
        "completed" -> SuccessGreen
        "pending" -> WarningAmber
        "escrow_locked" -> NeonPurple
        else -> TextMuted
    }

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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCredit) Color(0x2200E676) else Color(0x22FF0055)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCredit) Icons.Default.CallReceived else Icons.Default.CallMade,
                        contentDescription = null,
                        tint = if (isCredit) SuccessGreen else NeonHotPink,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tx.title,
                        color = IceWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tx.referenceId ?: tx.type,
                            color = TextSubtle,
                            fontSize = 10.sp
                        )
                        Text(text = " • ", color = TextSubtle, fontSize = 10.sp)
                        Text(
                            text = tx.status.uppercase(),
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Amount
            Text(
                text = "${if (isCredit) "+" else "-"}৳${String.format("%.2f", tx.amount)}",
                color = if (isCredit) SuccessGreen else IceWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
