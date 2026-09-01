package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelProgressEntity
import com.example.data.UserStatsEntity
import com.example.ui.theme.LocalGameColors

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val current: Int,
    val max: Int
)

@Composable
fun StatsAchievementsScreen(
    userStats: UserStatsEntity?,
    levelProgressList: List<LevelProgressEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val totalStars = levelProgressList.sumOf { it.stars }
    val completedLevels = levelProgressList.count { it.stars > 0 }
    val totalArrows = userStats?.totalArrowsEscaped ?: 0
    val highestCombo = userStats?.highestCombo ?: 0
    val streak = userStats?.dailyStreak ?: 0

    val achievements = listOf(
        AchievementItem("first_step", "First Flight", "Solve your very first arrow puzzle", Icons.Default.CheckCircle, completedLevels.coerceAtMost(1), 1),
        AchievementItem("arrow_100", "Arrow Novice", "Escape 100 total arrows", Icons.Default.Psychology, totalArrows.coerceAtMost(100), 100),
        AchievementItem("arrow_500", "Arrow Master", "Escape 500 total arrows", Icons.Default.Psychology, totalArrows.coerceAtMost(500), 500),
        AchievementItem("stars_30", "Star Collector", "Earn 30 gold stars in Campaign", Icons.Default.Star, totalStars.coerceAtMost(30), 30),
        AchievementItem("stars_90", "Starlight Prodigy", "Earn 90 gold stars in Campaign", Icons.Default.Star, totalStars.coerceAtMost(90), 90),
        AchievementItem("combo_5", "Combo Lightning", "Achieve a 5x combo streak", Icons.Default.ElectricBolt, highestCombo.coerceAtMost(5), 5),
        AchievementItem("combo_10", "Combo God", "Achieve a 10x combo streak", Icons.Default.ElectricBolt, highestCombo.coerceAtMost(10), 10),
        AchievementItem("streak_7", "Weekly Devotion", "Maintain a 7-day daily puzzle streak", Icons.Default.LocalFireDepartment, streak.coerceAtMost(7), 7)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
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
                    .testTag("btn_stats_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "BRAIN STATS & AWARDS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(label = "Arrows Escaped", value = "$totalArrows", color = colors.primary, modifier = Modifier.weight(1f))
            StatCard(label = "Stages Cleared", value = "$completedLevels/60", color = colors.secondary, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(label = "Total Stars", value = "$totalStars/180", color = Color(0xFFFFD700), modifier = Modifier.weight(1f))
            StatCard(label = "Max Combo", value = "x$highestCombo", color = colors.tertiary, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "TROPHIES & ACHIEVEMENTS",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(achievements) { item ->
                val isUnlocked = item.current >= item.max
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isUnlocked) colors.primary.copy(alpha = 0.8f) else colors.cardBorder.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) colors.primary.copy(alpha = 0.2f) else colors.surfaceVariant)
                                .border(1.dp, if (isUnlocked) colors.primary else colors.cardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (isUnlocked) colors.primary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) colors.onBackground else colors.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "${item.current}/${item.max}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) colors.primary else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (item.current.toFloat() / item.max).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isUnlocked) colors.primary else colors.tertiary,
                                trackColor = colors.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    val colors = LocalGameColors.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = modifier.border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
