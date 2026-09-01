package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.model.AppTheme

val LocalGameColors = staticCompositionLocalOf { CyberNeonTheme }

@Composable
fun ArrowSolveTheme(
    appTheme: AppTheme = AppTheme.CYBER_NEON,
    content: @Composable () -> Unit
) {
    val gameColors = getGameThemeColors(appTheme)

    val colorScheme = darkColorScheme(
        primary = gameColors.primary,
        secondary = gameColors.secondary,
        tertiary = gameColors.tertiary,
        background = gameColors.background,
        surface = gameColors.surface,
        surfaceVariant = gameColors.surfaceVariant,
        onBackground = gameColors.onBackground,
        onSurface = gameColors.onSurface
    )

    CompositionLocalProvider(LocalGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
