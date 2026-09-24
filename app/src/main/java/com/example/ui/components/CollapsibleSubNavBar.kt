package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

data class SubNavItem(
    val tab: MainTab,
    val labelBn: String,
    val labelEn: String,
    val icon: ImageVector,
    val hasBadge: Boolean = false
)

val DEFAULT_SUB_NAV_ITEMS = listOf(
    SubNavItem(MainTab.FEED, "ফিড", "Feed", Icons.Default.Home),
    SubNavItem(MainTab.CIRCLE, "সার্কেল", "Circle", Icons.Default.Group),
    SubNavItem(MainTab.REELS, "ক্লিক লাইভ", "Kliq Live", Icons.Default.LiveTv),
    SubNavItem(MainTab.WALLET, "ফেভারিট", "Favorite", Icons.Default.Favorite, hasBadge = true),
    SubNavItem(MainTab.REELS, "রিলস", "Reels", Icons.Default.PlayCircle)
)

/**
 * Horizontally scrollable tab/sub-menu component under the header.
 * Stays sticky and visible when the top header collapses to ensure navigation
 * remains instantly accessible at all times.
 */
@Composable
fun CollapsibleSubNavBar(
    currentTab: MainTab,
    language: String,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
    items: List<SubNavItem> = DEFAULT_SUB_NAV_ITEMS
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ObsidianBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentTab == item.tab

                val bgTint by animateColorAsState(
                    targetValue = if (isSelected) ElectricCyan.copy(alpha = 0.12f) else ObsidianSurface,
                    animationSpec = tween(durationMillis = 200),
                    label = "bgTint"
                )

                val borderTint by animateColorAsState(
                    targetValue = if (isSelected) ElectricCyan else ObsidianBorder,
                    animationSpec = tween(durationMillis = 200),
                    label = "borderTint"
                )

                val contentTint by animateColorAsState(
                    targetValue = if (isSelected) ElectricCyan else MetallicSilver,
                    animationSpec = tween(durationMillis = 200),
                    label = "contentTint"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgTint)
                        .border(1.dp, borderTint, RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(item.tab) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("sub_nav_tab_${item.tab.name.lowercase()}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = contentTint,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = if (language == "bn") item.labelBn else item.labelEn,
                            color = contentTint,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )

                        if (item.hasBadge) {
                            PulsingSyncDot(color = SuccessGreen, size = 6.dp)
                        }
                    }
                }
            }
        }
    }
}
