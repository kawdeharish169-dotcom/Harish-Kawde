package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppTheme
import com.example.model.GameMode
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.RushModeScreen
import com.example.ui.screens.SandboxScreen
import com.example.ui.screens.SettingsThemesScreen
import com.example.ui.screens.StatsAchievementsScreen
import com.example.ui.theme.ArrowSolveTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            val userStats by viewModel.userStatsFlow.collectAsState(initial = null)
            val levelProgressList by viewModel.levelProgressFlow.collectAsState(initial = emptyList())

            val selectedTheme = try {
                AppTheme.valueOf(userStats?.selectedTheme ?: "CYBER_NEON")
            } catch (_: Exception) {
                AppTheme.CYBER_NEON
            }

            ArrowSolveTheme(appTheme = selectedTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Global back handler
                    if (uiState.currentScreen != AppScreen.HOME) {
                        BackHandler {
                            viewModel.navigateTo(AppScreen.HOME)
                        }
                    }

                    when (uiState.currentScreen) {
                        AppScreen.HOME -> {
                            HomeScreen(
                                userStats = userStats,
                                levelProgressList = levelProgressList,
                                onPlayCampaign = { viewModel.startCampaign() },
                                onOpenLevelSelect = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
                                onPlayDaily = { viewModel.navigateTo(AppScreen.DAILY_CHALLENGE) },
                                onPlayRush = { viewModel.navigateTo(AppScreen.RUSH_MODE) },
                                onPlayZen = { viewModel.startZen() },
                                onOpenSandbox = { viewModel.navigateTo(AppScreen.SANDBOX) },
                                onOpenStats = { viewModel.navigateTo(AppScreen.STATS_ACHIEVEMENTS) },
                                onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS_THEMES) },
                                onOpenHowToPlay = { viewModel.navigateTo(AppScreen.HOW_TO_PLAY) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.GAMEPLAY -> {
                            GameplayScreen(
                                uiState = uiState,
                                userStats = userStats,
                                onBackToHome = { viewModel.navigateTo(AppScreen.HOME) },
                                onCellClick = { r, c -> viewModel.onCellClick(r, c) },
                                onHintClick = { viewModel.useHint() },
                                onRotateClick = { viewModel.toggleRotateTool() },
                                onBombClick = { viewModel.toggleBombTool() },
                                onUndoClick = { viewModel.undoMove() },
                                onRestartClick = { viewModel.restartLevel() },
                                onNextLevel = { viewModel.nextLevel() },
                                onLevelSelect = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.LEVEL_SELECT -> {
                            LevelSelectScreen(
                                selectedChapterId = uiState.selectedChapterId,
                                levelProgressList = levelProgressList,
                                onSelectChapter = { viewModel.selectChapter(it) },
                                onSelectLevel = { viewModel.startCampaignLevel(it) },
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.DAILY_CHALLENGE -> {
                            DailyChallengeScreen(
                                userStats = userStats,
                                isCompletedToday = uiState.dailyCompletedToday,
                                onStartDaily = { viewModel.startDailyChallenge() },
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.RUSH_MODE -> {
                            RushModeScreen(
                                onStartRush = { viewModel.startRush() },
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.SANDBOX -> {
                            SandboxScreen(
                                rows = uiState.sandboxRows,
                                cols = uiState.sandboxCols,
                                board = uiState.sandboxBoard,
                                selectedTool = uiState.sandboxSelectedTool,
                                onSelectGridSize = { r, c -> viewModel.setSandboxGridSize(r, c) },
                                onSelectTool = { viewModel.selectSandboxTool(it) },
                                onCellClick = { r, c -> viewModel.onSandboxCellClick(r, c) },
                                onTestLevel = { viewModel.testPlaySandbox() },
                                onSaveLevel = { viewModel.saveSandboxLevel(it) },
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.STATS_ACHIEVEMENTS -> {
                            StatsAchievementsScreen(
                                userStats = userStats,
                                levelProgressList = levelProgressList,
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.SETTINGS_THEMES -> {
                            SettingsThemesScreen(
                                userStats = userStats,
                                onSelectTheme = { viewModel.setTheme(it) },
                                onSelectArrowStyle = { viewModel.setArrowStyle(it) },
                                onToggleSound = { viewModel.toggleSound(it) },
                                onToggleHaptic = { viewModel.toggleHaptic(it) },
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        AppScreen.HOW_TO_PLAY -> {
                            HowToPlayScreen(
                                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
