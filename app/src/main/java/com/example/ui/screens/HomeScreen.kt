package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelProgressEntity
import com.example.data.UserStatsEntity
import com.example.model.GameMode
import com.example.ui.theme.LocalGameColors
import com.example.viewmodel.AppScreen

@Composable
fun HomeScreen(
    userStats: UserStatsEntity?,
    levelProgressList: List<LevelProgressEntity>,
    onPlayCampaign: () -> Unit,
    onOpenLevelSelect: () -> Unit,
    onPlayDaily: () -> Unit,
    onPlayRush: () -> Unit,
    onPlayZen: () -> Unit,
    onOpenSandbox: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val scrollState = rememberScrollState()

    val totalStars = levelProgressList.sumOf { it.stars }
    val completedLevels = levelProgressList.count { it.stars > 0 }
    val nextLevelId = levelProgressList.filter { it.isUnlocked }.maxOfOrNull { it.levelId } ?: 1
    val coins = userStats?.coins ?: 100

    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Coins & Stats & Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coins badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Coins",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$coins",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.onSurface
                )
            }

            // Total Stars Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.tertiary.copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stars",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$totalStars / 180",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colors.onSurface
                )
            }

            // Settings & Guide Icons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onOpenHowToPlay,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, CircleShape)
                        .testTag("btn_home_guide")
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = "Guide", tint = colors.onSurface, modifier = Modifier.size(20.dp))
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, CircleShape)
                        .testTag("btn_home_settings")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = colors.onSurface, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Logo and App Title
        Box(
            modifier = Modifier
                .scale(glowScale)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            colors.primary.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ARROW SOLVE",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = colors.primary,
                    modifier = Modifier.testTag("home_app_title")
                )
                Text(
                    text = "BRAIN PUZZLE ESCAPE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp,
                    color = colors.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Play Card: Campaign Mode
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, colors.primary, RoundedCornerShape(24.dp))
                .clickable { onPlayCampaign() }
                .testTag("card_play_campaign")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CAMPAIGN JOURNEY",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Level $nextLevelId • 60 Mind-Bending Stages",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colors.primary)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (completedLevels / 60f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colors.primary,
                    trackColor = colors.surfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$completedLevels / 60 Levels Solved",
                        fontSize = 11.sp,
                        color = colors.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Select Level →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        modifier = Modifier.clickable { onOpenLevelSelect() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Grid of Game Modes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Daily Challenge
            GameModeCard(
                title = "Daily Puzzle",
                subtitle = if (userStats?.dailyStreak ?: 0 > 0) "${userStats?.dailyStreak} Day Streak 🔥" else "New Everyday",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = colors.secondary,
                onClick = onPlayDaily,
                modifier = Modifier.weight(1f),
                testTag = "card_mode_daily"
            )

            // Rush Mode
            GameModeCard(
                title = "Speed Rush",
                subtitle = "60s Fast Solve",
                icon = Icons.Default.Speed,
                accentColor = Color(0xFFFFCC00),
                onClick = onPlayRush,
                modifier = Modifier.weight(1f),
                testTag = "card_mode_rush"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Zen Relax
            GameModeCard(
                title = "Zen Mode",
                subtitle = "Endless Calm",
                icon = Icons.Default.SelfImprovement,
                accentColor = colors.arrowColors[3],
                onClick = onPlayZen,
                modifier = Modifier.weight(1f),
                testTag = "card_mode_zen"
            )

            // Sandbox Creator
            GameModeCard(
                title = "Level Editor",
                subtitle = "Build & Share",
                icon = Icons.Default.Build,
                accentColor = colors.tertiary,
                onClick = onOpenSandbox,
                modifier = Modifier.weight(1f),
                testTag = "card_mode_sandbox"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Bottom Stats & Trophies Row
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .clickable { onOpenStats() }
                .testTag("card_home_stats")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Achievements",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Brain Stats & Trophies",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "${userStats?.totalArrowsEscaped ?: 0} Arrows Cleared • High Combo x${userStats?.highestCombo ?: 0}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Text(
                    text = "View →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
fun GameModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val colors = LocalGameColors.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = modifier
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
