package com.alphaimperium.alphahub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AlphaColors = darkColorScheme(
    primary = Color(0xFF159CFF),
    onPrimary = Color.White,
    secondary = Color(0xFF8B5CFF),
    onSecondary = Color.White,
    tertiary = Color(0xFF00E5FF),
    background = Color(0xFF02050D),
    onBackground = Color(0xFFF6F7FF),
    surface = Color(0xFF071325),
    onSurface = Color(0xFFF6F7FF),
    surfaceVariant = Color(0xFF0B1D38),
    onSurfaceVariant = Color(0xFFB6C7E6)
)

@Composable
fun AlphaHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AlphaColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
