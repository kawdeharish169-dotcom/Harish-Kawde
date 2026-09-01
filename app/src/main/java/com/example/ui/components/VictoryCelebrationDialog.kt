package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameMode
import com.example.ui.theme.LocalGameColors

@Composable
fun VictoryCelebrationDialog(
    stars: Int,
    moves: Int,
    timeSec: Int,
    maxCombo: Int,
    score: Int,
    gameMode: GameMode,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onLevelSelect: () -> Unit
) {
    val colors = LocalGameColors.current

    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(stars) {
        if (stars >= 1) {
            star1Scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        }
        if (stars >= 2) {
            star2Scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        }
        if (stars >= 3) {
            star3Scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        }
    }

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, colors.cardBorder, RoundedCornerShape(28.dp))
                .padding(4.dp)
                .testTag("victory_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(colors.primary, colors.secondary)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (gameMode == GameMode.RUSH) "TIME'S UP!" else "PUZZLE SOLVED!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Star Rating Animation (for campaign / daily)
                if (gameMode != GameMode.RUSH) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // Star 1
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star 1",
                            tint = if (stars >= 1) Color(0xFFFFD700) else colors.cardBorder,
                            modifier = Modifier
                                .size(44.dp)
                                .scale(if (stars >= 1) star1Scale.value else 0.8f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Star 2
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star 2",
                            tint = if (stars >= 2) Color(0xFFFFD700) else colors.cardBorder,
                            modifier = Modifier
                                .size(54.dp)
                                .scale(if (stars >= 2) star2Scale.value else 0.8f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Star 3
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star 3",
                            tint = if (stars >= 3) Color(0xFFFFD700) else colors.cardBorder,
                            modifier = Modifier
                                .size(44.dp)
                                .scale(if (stars >= 3) star3Scale.value else 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceVariant)
                        .border(1.dp, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatRow(label = "Score Earned", value = "+$score PTS", valueColor = colors.primary)
                        StatRow(label = "Total Moves", value = "$moves moves", valueColor = colors.onSurface)
                        val m = timeSec / 60
                        val s = timeSec % 60
                        StatRow(label = "Clear Time", value = String.format("%02d:%02d", m, s), valueColor = colors.onSurface)
                        if (maxCombo > 1) {
                            StatRow(label = "Max Combo Streak", value = "x$maxCombo COMBO", valueColor = colors.secondary)
                        }
                    }
                }

                // Coins reward pill
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x33FFD700))
                        .border(1.dp, Color(0xFFFFD700), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+${stars * 15 + 10} Coins Earned",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_victory_replay"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = colors.onSurface)
                    }

                    OutlinedButton(
                        onClick = onLevelSelect,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_victory_levels"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = "Levels", tint = colors.onSurface)
                    }

                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                            .testTag("btn_victory_next"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(
                            text = if (gameMode == GameMode.ZEN) "Next Puzzle" else "Continue",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
