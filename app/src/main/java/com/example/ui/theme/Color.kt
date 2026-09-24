package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Pure White Canvas & Minimal Modern Palette
val ObsidianBlack = Color(0xFFFFFFFF) // Primary App Canvas is Full Pure White
val ObsidianSurface = Color(0xFFF8FAFC) // Slate-50 elevated clean surface
val ObsidianCardBg = Color(0xFFFFFFFF) // Crisp white card background
val ObsidianCardHover = Color(0xFFF1F5F9) // Slate-100 hover
val ObsidianBorder = Color(0xFFE2E8F0) // Clean slate-200 border
val ObsidianBorderActive = Color(0xFF6366F1) // Indigo active accent

// Brand Accents
val ElectricCyan = Color(0xFF4F46E5) // Modern Indigo/Deep Brand
val ElectricCyanGlow = Color(0x1A4F46E5)
val NeonPurple = Color(0xFF7C3AED) // Purple Accent
val NeonViolet = Color(0xFF6D28D9)
val NeonMagenta = Color(0xFFDB2777)
val NeonHotPink = Color(0xFFE11D48)
val GoldCoin = Color(0xFFD97706) // Rich Amber
val SuccessGreen = Color(0xFF10B981) // Emerald
val WarningAmber = Color(0xFFF59E0B)

// Typography (High-contrast Dark on White)
val IceWhite = Color(0xFF0F172A) // Slate-900 high contrast dark text
val MetallicSilver = Color(0xFF334155) // Slate-700 secondary dark text
val TextMuted = Color(0xFF64748B) // Slate-500 muted text
val TextSubtle = Color(0xFF94A3B8) // Slate-400 subtle placeholder text

// Gradients
val CyberBrandGradient = Brush.horizontalGradient(
    listOf(
        Color(0xFF00d2ff),
        Color(0xFF8a2be2),
        Color(0xFFff007f)
    )
)

val BrandCyanPurpleGradient = Brush.horizontalGradient(
    listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
)

val BrandPurpleMagentaGradient = Brush.horizontalGradient(
    listOf(Color(0xFF7C3AED), Color(0xFFDB2777))
)

val CardGlowBorder = Brush.linearGradient(
    listOf(
        Color(0xFFCBD5E1),
        Color(0xFFE2E8F0),
        Color(0xFFF1F5F9)
    )
)

