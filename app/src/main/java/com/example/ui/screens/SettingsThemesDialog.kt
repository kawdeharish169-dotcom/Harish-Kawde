package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStatsEntity
import com.example.model.AppTheme
import com.example.model.ArrowStyle
import com.example.ui.theme.LocalGameColors
import com.example.ui.theme.getGameThemeColors

@Composable
fun SettingsThemesScreen(
    userStats: UserStatsEntity?,
    onSelectTheme: (AppTheme) -> Unit,
    onSelectArrowStyle: (ArrowStyle) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val currentThemeName = userStats?.selectedTheme ?: "CYBER_NEON"
    val currentArrowStyleName = userStats?.selectedArrowStyle ?: "MODERN_TRIANGLE"
    val isSound = userStats?.isSoundEnabled ?: true
    val isHaptic = userStats?.isHapticEnabled ?: true

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, CircleShape)
                    .testTag("btn_settings_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "SETTINGS & THEMES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sound & Haptics Section
        Text(
            text = "AUDIO & FEEDBACK",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier.fillMaxWidth().border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = colors.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sound Effects & Chimes", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.onBackground)
                    }
                    Switch(
                        checked = isSound,
                        onCheckedChange = onToggleSound,
                        colors = SwitchDefaults.colors(checkedThumbColor = colors.primary, checkedTrackColor = colors.surfaceVariant)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = colors.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Haptic Touch Vibration", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.onBackground)
                    }
                    Switch(
                        checked = isHaptic,
                        onCheckedChange = onToggleHaptic,
                        colors = SwitchDefaults.colors(checkedThumbColor = colors.secondary, checkedTrackColor = colors.surfaceVariant)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Theme Palette Selection
        Text(
            text = "COLOR THEMES",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppTheme.values().forEach { theme ->
            val isSelected = currentThemeName == theme.name
            val themeColors = getGameThemeColors(theme)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) themeColors.primary else themeColors.cardBorder.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectTheme(theme) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Palette preview circles
                        Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                            themeColors.arrowColors.forEach { c ->
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = theme.displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.onBackground
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = themeColors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Arrow Style Selection
        Text(
            text = "ARROW HEAD STYLES",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArrowStyle.values().forEach { style ->
                val isSelected = currentArrowStyleName == style.name
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) colors.primary else colors.cardBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectArrowStyle(style) }
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = style.displayName.split(" ").first(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.primary else colors.onSurface
                        )
                    }
                }
            }
        }
    }
}
