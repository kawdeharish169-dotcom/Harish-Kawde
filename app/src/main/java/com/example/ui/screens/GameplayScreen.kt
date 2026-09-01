package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStatsEntity
import com.example.model.ActiveTool
import com.example.model.ArrowStyle
import com.example.model.GameMode
import com.example.ui.components.GameBoardView
import com.example.ui.components.PowerUpBar
import com.example.ui.components.ScoreAndComboHeader
import com.example.ui.components.VictoryCelebrationDialog
import com.example.ui.theme.LocalGameColors
import com.example.viewmodel.GameUiState

@Composable
fun GameplayScreen(
    uiState: GameUiState,
    userStats: UserStatsEntity?,
    onBackToHome: () -> Unit,
    onCellClick: (Int, Int) -> Unit,
    onHintClick: () -> Unit,
    onRotateClick: () -> Unit,
    onBombClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRestartClick: () -> Unit,
    onNextLevel: () -> Unit,
    onLevelSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val arrowStyle = try {
        ArrowStyle.valueOf(userStats?.selectedArrowStyle ?: "MODERN_TRIANGLE")
    } catch (_: Exception) {
        ArrowStyle.MODERN_TRIANGLE
    }

    val coins = userStats?.coins ?: 100
    val hintsCount = userStats?.hintsCount ?: 3
    val rotatesCount = userStats?.rotatesCount ?: 3
    val bombsCount = userStats?.bombsCount ?: 2

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, CircleShape)
                        .testTag("btn_game_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.onSurface
                    )
                }

                // Level Mode Title
                Text(
                    text = when (uiState.gameMode) {
                        GameMode.CAMPAIGN -> "STAGE ${uiState.currentLevelId}"
                        GameMode.DAILY_CHALLENGE -> "DAILY CHALLENGE"
                        GameMode.RUSH -> "SPEED RUSH"
                        GameMode.ZEN -> "ZEN GARDEN"
                        GameMode.SANDBOX -> "SANDBOX TEST"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = colors.primary
                )

                // Coins status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }
            }

            // Score & Combos & Stats Header
            ScoreAndComboHeader(
                title = uiState.currentLevelMeta?.title ?: "Puzzle",
                score = uiState.score,
                moves = uiState.movesCount,
                timeSec = uiState.elapsedTimeSeconds,
                combo = uiState.comboCount,
                gameMode = uiState.gameMode,
                rushTimeRemaining = uiState.rushTimeRemaining
            )

            // Optional Hint / Tutorial text banner
            if (!uiState.currentLevelMeta?.hintText.isNullOrEmpty() && uiState.movesCount < 3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.8f))
                        .border(1.dp, colors.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tip",
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.currentLevelMeta?.hintText ?: "",
                            fontSize = 12.sp,
                            color = colors.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Active Tool Alert Banner (if rotate or bomb is engaged)
            AnimatedVisibility(
                visible = uiState.activeTool != ActiveTool.NONE,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.secondary.copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (uiState.activeTool == ActiveTool.ROTATE_TOOL) "Tap any arrow to rotate 90°" else "Tap any cell to Vaporize!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Main Interactive Puzzle Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GameBoardView(
                    board = uiState.board,
                    hintedCell = uiState.hintedCell,
                    activeTool = uiState.activeTool,
                    arrowStyle = arrowStyle,
                    flyingArrows = uiState.flyingArrows,
                    bumpAnimations = uiState.bumpAnimations,
                    particles = uiState.particles,
                    onCellClick = onCellClick
                )
            }

            // Bottom Power-Up & Action Bar
            PowerUpBar(
                hintsCount = hintsCount,
                rotatesCount = rotatesCount,
                bombsCount = bombsCount,
                coins = coins,
                activeTool = uiState.activeTool,
                canUndo = uiState.canUndo,
                onHintClick = onHintClick,
                onRotateClick = onRotateClick,
                onBombClick = onBombClick,
                onUndoClick = onUndoClick,
                onRestartClick = onRestartClick
            )
        }

        // Victory Dialog Popup Overlay
        if (uiState.isGameFinished) {
            VictoryCelebrationDialog(
                stars = uiState.starsAwarded,
                moves = uiState.movesCount,
                timeSec = uiState.elapsedTimeSeconds,
                maxCombo = uiState.maxCombo,
                score = uiState.score,
                gameMode = uiState.gameMode,
                onNextLevel = onNextLevel,
                onReplay = onRestartClick,
                onLevelSelect = onLevelSelect
            )
        }
    }
}
