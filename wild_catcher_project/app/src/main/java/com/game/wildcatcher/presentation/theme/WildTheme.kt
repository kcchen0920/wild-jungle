package com.game.wildcatcher.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LeatherDark = Color(0xFF2B1B17)
val LeatherBrown = Color(0xFF4A2E1B)
val ParchmentBg = Color(0xFFF7F2E4)
val ParchmentCard = Color(0xFFEFE6CD)
val ParchmentBorder = Color(0xFFD6C8A8)
val VintageBrass = Color(0xFFC89D42)
val BrassHighlight = Color(0xFFFFD57A)
val BambooGreen = Color(0xFF2E7D32)
val BambooLight = Color(0xFF81C784)
val InkBlack = Color(0xFF1F2421)
val InkGrey = Color(0xFF5A5852)
val BubbleCyan = Color(0xFF00ACC1)
val SunsetAmber = Color(0xFFFF8F00)
val DangerCoral = Color(0xFFD32F2F)

// Eye Care Warm Filter Color
val EyeCareOverlayColor = Color(0x33FFA834)

private val WildColorScheme = lightColorScheme(
    primary = BambooGreen,
    onPrimary = Color.White,
    primaryContainer = BambooLight,
    secondary = VintageBrass,
    onSecondary = InkBlack,
    background = ParchmentBg,
    onBackground = InkBlack,
    surface = ParchmentCard,
    onSurface = InkBlack
)

val WildTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = LeatherDark
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = LeatherDark
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = LeatherDark
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = InkBlack
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = InkGrey
    )
)

val LocalWarmFilterActive = compositionLocalOf { false }

@Composable
fun WildCatcherTheme(
    warmFilterActive: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalWarmFilterActive provides warmFilterActive) {
        MaterialTheme(
            colorScheme = WildColorScheme,
            typography = WildTypography,
            content = content
        )
    }
}
