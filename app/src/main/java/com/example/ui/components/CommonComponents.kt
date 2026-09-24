package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    borderColor: Color = Color(0x336366F1),
    borderWidth: Dp = 1.2.dp,
    hasNeonAccent: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xCCFFFFFF),
            Color(0x99F8FAFC)
        )
    )

    Box(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = shape, spotColor = Color(0x1A6366F1))
            .clip(shape)
            .background(bgBrush)
            .border(
                width = borderWidth,
                brush = if (hasNeonAccent) 
                    Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFFA855F7))) 
                else 
                    Brush.linearGradient(listOf(Color(0x66E2E8F0), Color(0x33CBD5E1))),
                shape = shape
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    Icon(
        imageVector = Icons.Default.Verified,
        contentDescription = "Verified",
        tint = ElectricCyan,
        modifier = modifier.size(size)
    )
}

@Composable
fun SpecialBadgePill(
    badgeText: String,
    modifier: Modifier = Modifier
) {
    val bgGradient = when (badgeText) {
        "VVIP" -> Brush.horizontalGradient(listOf(NeonMagenta, NeonPurple))
        "PRO" -> Brush.horizontalGradient(listOf(ElectricCyan, NeonViolet))
        "Scout Leader" -> Brush.horizontalGradient(listOf(NeonPurple, Color(0xFF3B82F6)))
        else -> Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgGradient)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = badgeText,
            color = IceWhite,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PulsingSyncDot(
    modifier: Modifier = Modifier,
    color: Color = SuccessGreen,
    size: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    gradient: Brush = BrandCyanPurpleGradient,
    textColor: Color = IceWhite,
    height: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .height(height)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = ElectricCyan,
                spotColor = ElectricCyan
            )
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Obsidian-themed shimmer skeleton loader for feed data fetching states.
 */
@Composable
fun ObsidianShimmerSkeleton(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        ObsidianSurface,
        ObsidianCardHover,
        ObsidianSurface
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(translateAnim - 200f, translateAnim - 200f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim)
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        borderColor = ObsidianBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
    }
}

/**
 * Modifier extension to apply the Cyber Brand Gradient background
 * (#00d2ff, #8a2be2, #ff007f) for buttons, headers, and cards.
 */
fun Modifier.brandBackground(shape: Shape = RoundedCornerShape(12.dp)): Modifier =
    this.clip(shape).background(CyberBrandGradient)

/**
 * Modifier extension to apply a Cyber Brand Gradient border
 */
fun Modifier.brandBorder(width: Dp = 1.5.dp, shape: Shape = RoundedCornerShape(12.dp)): Modifier =
    this.border(width = width, brush = CyberBrandGradient, shape = shape)
