package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.theme.*

data class NavItem(
    val tab: MainTab,
    val labelBn: String,
    val labelEn: String,
    val iconActive: ImageVector,
    val iconInactive: ImageVector,
    val testTag: String
)

@Composable
fun KliqBottomNavigation(
    currentTab: MainTab,
    language: String,
    onTabSelected: (MainTab) -> Unit
) {
    val items = listOf(
        NavItem(MainTab.FEED, "হোম", "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
        NavItem(MainTab.SCOUT, "ওটিপি হাব", "OTP Hub", Icons.Filled.Hub, Icons.Outlined.Hub, "nav_otphub"),
        NavItem(MainTab.REELS, "ক্লিক লাইভ", "Kliq Live", Icons.Filled.LiveTv, Icons.Outlined.LiveTv, "nav_kliqlive"),
        NavItem(MainTab.PROFILE, "মেনু", "Menu", Icons.Filled.Menu, Icons.Outlined.Menu, "nav_menu")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.tab
                val label = if (language == "bn") item.labelBn else item.labelEn

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(item.tab) }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag(item.testTag)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ElectricCyan.copy(alpha = 0.2f) else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.iconActive else item.iconInactive,
                            contentDescription = label,
                            tint = if (isSelected) ElectricCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = label,
                        color = if (isSelected) ElectricCyan else TextSubtle,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )

                    if (isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan)
                        )
                    }
                }
            }
        }
    }
}
