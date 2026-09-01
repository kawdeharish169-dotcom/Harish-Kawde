package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "arrow_solve_db"
    ).build()

    private val dao = database.appDao()

    val levelProgressFlow: Flow<List<LevelProgressEntity>> = dao.getAllLevelProgress()
    val userStatsFlow: Flow<UserStatsEntity?> = dao.getUserStatsFlow()
    val customLevelsFlow: Flow<List<CustomLevelEntity>> = dao.getCustomLevelsFlow()

    suspend fun initDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        val stats = dao.getUserStats()
        if (stats == null) {
            dao.saveUserStats(UserStatsEntity())
        }
        val firstLevel = dao.getLevelProgress(1)
        if (firstLevel == null) {
            // Unlock level 1 by default
            dao.saveLevelProgress(LevelProgressEntity(levelId = 1, isUnlocked = true))
        }
    }

    suspend fun saveLevelCompletion(levelId: Int, stars: Int, timeSec: Int, moves: Int) = withContext(Dispatchers.IO) {
        val existing = dao.getLevelProgress(levelId)
        val bestStars = maxOf(existing?.stars ?: 0, stars)
        val bestTime = if (existing != null && existing.bestTimeSec > 0) minOf(existing.bestTimeSec, timeSec) else timeSec
        val bestMoves = if (existing != null && existing.bestMoves > 0) minOf(existing.bestMoves, moves) else moves

        dao.saveLevelProgress(
            LevelProgressEntity(
                levelId = levelId,
                stars = bestStars,
                bestTimeSec = bestTime,
                bestMoves = bestMoves,
                isUnlocked = true,
                completedAt = System.currentTimeMillis()
            )
        )

        // Unlock next level
        val nextLevelId = levelId + 1
        if (nextLevelId <= 60) {
            val nextLevel = dao.getLevelProgress(nextLevelId)
            if (nextLevel == null || !nextLevel.isUnlocked) {
                dao.saveLevelProgress(
                    LevelProgressEntity(
                        levelId = nextLevelId,
                        stars = nextLevel?.stars ?: 0,
                        bestTimeSec = nextLevel?.bestTimeSec ?: 0,
                        bestMoves = nextLevel?.bestMoves ?: 0,
                        isUnlocked = true
                    )
                )
            }
        }

        // Award coins and update user stats
        val currentStats = dao.getUserStats() ?: UserStatsEntity()
        val earnedCoins = stars * 15 + 10
        dao.saveUserStats(
            currentStats.copy(
                coins = currentStats.coins + earnedCoins,
                totalLevelsCleared = currentStats.totalLevelsCleared + 1
            )
        )
    }

    suspend fun incrementArrowsEscaped(count: Int, combo: Int) = withContext(Dispatchers.IO) {
        val currentStats = dao.getUserStats() ?: UserStatsEntity()
        val newHighestCombo = maxOf(currentStats.highestCombo, combo)
        dao.saveUserStats(
            currentStats.copy(
                totalArrowsEscaped = currentStats.totalArrowsEscaped + count,
                highestCombo = newHighestCombo
            )
        )
    }

    suspend fun updateSettings(
        theme: String? = null,
        arrowStyle: String? = null,
        sound: Boolean? = null,
        haptic: Boolean? = null
    ) = withContext(Dispatchers.IO) {
        val currentStats = dao.getUserStats() ?: UserStatsEntity()
        dao.saveUserStats(
            currentStats.copy(
                selectedTheme = theme ?: currentStats.selectedTheme,
                selectedArrowStyle = arrowStyle ?: currentStats.selectedArrowStyle,
                isSoundEnabled = sound ?: currentStats.isSoundEnabled,
                isHapticEnabled = haptic ?: currentStats.isHapticEnabled
            )
        )
    }

    suspend fun usePowerUp(type: String): Boolean = withContext(Dispatchers.IO) {
        val currentStats = dao.getUserStats() ?: UserStatsEntity()
        when (type) {
            "HINT" -> {
                if (currentStats.hintsCount > 0) {
                    dao.saveUserStats(currentStats.copy(hintsCount = currentStats.hintsCount - 1))
                    true
                } else if (currentStats.coins >= 30) {
                    dao.saveUserStats(currentStats.copy(coins = currentStats.coins - 30))
                    true
                } else false
            }
            "ROTATE" -> {
                if (currentStats.rotatesCount > 0) {
                    dao.saveUserStats(currentStats.copy(rotatesCount = currentStats.rotatesCount - 1))
                    true
                } else if (currentStats.coins >= 40) {
                    dao.saveUserStats(currentStats.copy(coins = currentStats.coins - 40))
                    true
                } else false
            }
            "BOMB" -> {
                if (currentStats.bombsCount > 0) {
                    dao.saveUserStats(currentStats.copy(bombsCount = currentStats.bombsCount - 1))
                    true
                } else if (currentStats.coins >= 50) {
                    dao.saveUserStats(currentStats.copy(coins = currentStats.coins - 50))
                    true
                } else false
            }
            else -> false
        }
    }

    suspend fun saveCustomLevel(title: String, rows: Int, cols: Int, data: String): Long = withContext(Dispatchers.IO) {
        dao.insertCustomLevel(CustomLevelEntity(title = title, rows = rows, cols = cols, layoutData = data))
    }

    suspend fun deleteCustomLevel(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteCustomLevel(id)
    }

    suspend fun recordDailyCompletion(todayDate: String): Boolean = withContext(Dispatchers.IO) {
        val currentStats = dao.getUserStats() ?: UserStatsEntity()
        if (currentStats.lastDailyDate != todayDate) {
            val newStreak = currentStats.dailyStreak + 1
            val bonusCoins = 50 + newStreak * 10
            dao.saveUserStats(
                currentStats.copy(
                    lastDailyDate = todayDate,
                    dailyStreak = newStreak,
                    coins = currentStats.coins + bonusCoins
                )
            )
            true
        } else false
    }
}
