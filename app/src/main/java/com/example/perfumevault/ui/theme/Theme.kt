package com.example.perfumevault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HighVisibilityPrimary,
    onPrimary = Color.Black,
    secondary = HighVisibilitySecondary,
    onSecondary = Color.Black,
    background = GlassBackground,
    onBackground = Color.White,
    surface = GlassSurface,
    onSurface = Color.White,
    surfaceVariant = GlassSurface,
    onSurfaceVariant = Color.White,
    outline = GlassBorder,
    error = ErrorRed
)

@Composable
fun PerfumeVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
