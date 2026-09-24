package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ElectricCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = ElectricCyan,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = NeonPurple,
    tertiary = NeonMagenta,
    onTertiary = Color.White,
    background = Color(0xFFFFFFFF), // Full pure white
    onBackground = IceWhite,
    surface = Color(0xFFFFFFFF),
    onSurface = IceWhite,
    surfaceVariant = ObsidianSurface,
    onSurfaceVariant = MetallicSilver,
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun KliqTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

