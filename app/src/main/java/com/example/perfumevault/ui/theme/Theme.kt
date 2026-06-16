package com.perfumevault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppleAccentBlue,
    onPrimary = Color.White,
    secondary = DarkTextSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = AppleAccentBlue,
    onPrimary = Color.White,
    secondary = AppleTextSecondary,
    background = SoftWhiteBackground,
    surface = SoftSurface,
    onBackground = AppleTextBlack,
    onSurface = AppleTextBlack,
    error = ErrorRed
)

data class AdaptiveColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val glassBase: Color,
    val glassBorder: Color,
    val isDark: Boolean
)

val LocalAdaptiveColors = staticCompositionLocalOf {
    AdaptiveColors(
        textPrimary = AppleTextBlack,
        textSecondary = AppleTextSecondary,
        glassBase = SoftSurface,
        glassBorder = GlassBorderLight,
        isDark = false
    )
}

@Composable
fun PerfumeVaultTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        AdaptiveColors(
            textPrimary = DarkText,
            textSecondary = DarkTextSecondary,
            glassBase = DarkSurface,
            glassBorder = GlassBorderDark,
            isDark = true
        )
    } else {
        AdaptiveColors(
            textPrimary = AppleTextBlack,
            textSecondary = AppleTextSecondary,
            glassBase = SoftSurface,
            glassBorder = GlassBorderLight,
            isDark = false
        )
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAdaptiveColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
