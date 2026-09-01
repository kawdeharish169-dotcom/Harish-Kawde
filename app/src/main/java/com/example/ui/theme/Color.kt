package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.model.AppTheme

data class GameThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBorder: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val gridBackground: Color,
    val cellBackground: Color,
    val arrowColors: List<Color>,
    val wallColor: Color,
    val iceColor: Color,
    val portalColor: Color,
    val hintGlow: Color,
    val comboGlow: Color
)

val CyberNeonTheme = GameThemeColors(
    background = Color(0xFF090D16),
    surface = Color(0xFF131B2E),
    surfaceVariant = Color(0xFF1C2742),
    cardBorder = Color(0xFF2E406B),
    primary = Color(0xFF00F0FF),
    secondary = Color(0xFFFF007F),
    tertiary = Color(0xFFFFD600),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFE2E8F0),
    gridBackground = Color(0xFF0E1524),
    cellBackground = Color(0xFF162035),
    arrowColors = listOf(
        Color(0xFF00F0FF), // Neon Cyan
        Color(0xFFFF007F), // Neon Pink
        Color(0xFFFFD600), // Neon Yellow
        Color(0xFF00FF88)  // Neon Green
    ),
    wallColor = Color(0xFF475569),
    iceColor = Color(0xFF7DD3FC),
    portalColor = Color(0xFFA855F7),
    hintGlow = Color(0xFFFFE600),
    comboGlow = Color(0xFFFF0055)
)

val ZenGardenTheme = GameThemeColors(
    background = Color(0xFF14201A),
    surface = Color(0xFF1F3128),
    surfaceVariant = Color(0xFF2B4237),
    cardBorder = Color(0xFF3D5C4E),
    primary = Color(0xFF52B788),
    secondary = Color(0xFFE76F51),
    tertiary = Color(0xFFE9C46A),
    onBackground = Color(0xFFF4EBD9),
    onSurface = Color(0xFFE8DCC4),
    gridBackground = Color(0xFF18261F),
    cellBackground = Color(0xFF22352B),
    arrowColors = listOf(
        Color(0xFF74C69D),
        Color(0xFFE76F51),
        Color(0xFFF4A261),
        Color(0xFF2A9D8F)
    ),
    wallColor = Color(0xFF5C677D),
    iceColor = Color(0xFFA8DADC),
    portalColor = Color(0xFF9D4EDD),
    hintGlow = Color(0xFFE9C46A),
    comboGlow = Color(0xFFE76F51)
)

val MidnightVelvetTheme = GameThemeColors(
    background = Color(0xFF0D0819),
    surface = Color(0xFF18102B),
    surfaceVariant = Color(0xFF261A42),
    cardBorder = Color(0xFF402E6B),
    primary = Color(0xFFA78BFA),
    secondary = Color(0xFFF472B6),
    tertiary = Color(0xFFFBBF24),
    onBackground = Color(0xFFF5F3FF),
    onSurface = Color(0xFFEDE9FE),
    gridBackground = Color(0xFF130D24),
    cellBackground = Color(0xFF1E1436),
    arrowColors = listOf(
        Color(0xFFA78BFA),
        Color(0xFFF472B6),
        Color(0xFFFBBF24),
        Color(0xFF38BDF8)
    ),
    wallColor = Color(0xFF4B5563),
    iceColor = Color(0xFF93C5FD),
    portalColor = Color(0xFFC084FC),
    hintGlow = Color(0xFFFDE047),
    comboGlow = Color(0xFFEC4899)
)

val RetroArcadeTheme = GameThemeColors(
    background = Color(0xFF0F0F1A),
    surface = Color(0xFF1C1C2E),
    surfaceVariant = Color(0xFF2A2A44),
    cardBorder = Color(0xFFFF0055),
    primary = Color(0xFFFF0055),
    secondary = Color(0xFF00E5FF),
    tertiary = Color(0xFFFFE600),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFF3F4F6),
    gridBackground = Color(0xFF141424),
    cellBackground = Color(0xFF222238),
    arrowColors = listOf(
        Color(0xFFFF0055),
        Color(0xFF00E5FF),
        Color(0xFFFFE600),
        Color(0xFF00FF66)
    ),
    wallColor = Color(0xFF6B7280),
    iceColor = Color(0xFF67E8F9),
    portalColor = Color(0xFFD946EF),
    hintGlow = Color(0xFFFFE600),
    comboGlow = Color(0xFFFF0055)
)

val MinimalDarkTheme = GameThemeColors(
    background = Color(0xFF121214),
    surface = Color(0xFF1E1E22),
    surfaceVariant = Color(0xFF2B2B30),
    cardBorder = Color(0xFF404048),
    primary = Color(0xFFE2E8F0),
    secondary = Color(0xFFF87171),
    tertiary = Color(0xFFFBBF24),
    onBackground = Color(0xFFFAFAFA),
    onSurface = Color(0xFFF4F4F5),
    gridBackground = Color(0xFF17171A),
    cellBackground = Color(0xFF24242A),
    arrowColors = listOf(
        Color(0xFF38BDF8),
        Color(0xFFF87171),
        Color(0xFFFBBF24),
        Color(0xFF34D399)
    ),
    wallColor = Color(0xFF52525B),
    iceColor = Color(0xFFBAE6FD),
    portalColor = Color(0xFFA78BFA),
    hintGlow = Color(0xFFFEF08A),
    comboGlow = Color(0xFFF87171)
)

fun getGameThemeColors(theme: AppTheme): GameThemeColors {
    return when (theme) {
        AppTheme.CYBER_NEON -> CyberNeonTheme
        AppTheme.ZEN_GARDEN -> ZenGardenTheme
        AppTheme.MIDNIGHT_VELVET -> MidnightVelvetTheme
        AppTheme.RETRO_ARCADE -> RetroArcadeTheme
        AppTheme.MINIMAL_DARK -> MinimalDarkTheme
    }
}
