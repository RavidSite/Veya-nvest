package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VeyraGoldPrimary,
    onPrimary = Color(0xFF141006),
    primaryContainer = VeyraGoldDark,
    onPrimaryContainer = VeyraGoldLight,
    secondary = VeyraGoldLight,
    onSecondary = Color(0xFF141006),
    secondaryContainer = Color(0xFF221A0A),
    onSecondaryContainer = VeyraGoldLight,
    background = VeyraNavyDark,
    onBackground = VeyraTextPrimary,
    surface = VeyraNavyCard,
    onSurface = VeyraTextPrimary,
    surfaceVariant = VeyraNavyElevated,
    onSurfaceVariant = VeyraTextSecondary,
    outline = VeyraNavyBorder,
    error = VeyraError,
    onError = Color.White
)

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
