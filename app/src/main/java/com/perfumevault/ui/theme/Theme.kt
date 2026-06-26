package com.perfumevault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BlueSlate,
    onPrimary = Color.White,
    secondary = DustyDenim,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = LightModePrimary,
    onPrimary = Color.White,
    secondary = LightModeSecondary,
    background = LightModeBackground,
    surface = LightModeSurface,
    onBackground = LightModeTextPrimary,
    onSurface = LightModeTextPrimary,
    error = ErrorRed
)

data class AdaptiveColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val glassBase: Color,
    val glassBorder: Color,
    val background: Color,
    val isDark: Boolean
)

val LocalAdaptiveColors = staticCompositionLocalOf {
    AdaptiveColors(
        textPrimary = LightModeTextPrimary,
        textSecondary = LightModeTextSecondary,
        glassBase = Color.White.copy(alpha = 0.5f),
        glassBorder = Color(0x1A8C7851),
        background = LightModeBackground,
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
            glassBase = DeepSpaceBlue.copy(alpha = 0.6f),
            glassBorder = Color(0x33F2F5F9),
            background = DarkBackground,
            isDark = true
        )
    } else {
        AdaptiveColors(
            textPrimary = LightModeTextPrimary,
            textSecondary = LightModeTextSecondary,
            glassBase = Color.White.copy(alpha = 0.3f), // Muted glass look
            glassBorder = Color(0x1A3D4A5C),
            background = LightModeBackground,
            isDark = false
        )
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAdaptiveColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
