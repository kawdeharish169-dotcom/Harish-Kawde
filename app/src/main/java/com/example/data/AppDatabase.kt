package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val stars: Int = 0,
    val bestTimeSec: Int = 0,
    val bestMoves: Int = 0,
    val isUnlocked: Boolean = false,
    val completedAt: Long = 0L
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 100,
    val totalArrowsEscaped: Int = 0,
    val totalLevelsCleared: Int = 0,
    val highestCombo: Int = 0,
    val dailyStreak: Int = 0,
    val lastDailyDate: String = "",
    val selectedTheme: String = "CYBER_NEON",
    val selectedArrowStyle: String = "MODERN_TRIANGLE",
    val isSoundEnabled: Boolean = true,
    val isHapticEnabled: Boolean = true,
    val hintsCount: Int = 3,
    val rotatesCount: Int = 3,
    val bombsCount: Int = 2
)

@Entity(tableName = "custom_levels")
data class CustomLevelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rows: Int,
    val cols: Int,
    val layoutData: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface AppDao {
    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    fun getAllLevelProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
    suspend fun getLevelProgress(levelId: Int): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLevelProgress(progress: LevelProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllLevelProgress(progressList: List<LevelProgressEntity>)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStats(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserStats(stats: UserStatsEntity)

    @Query("SELECT * FROM custom_levels ORDER BY createdAt DESC")
    fun getCustomLevelsFlow(): Flow<List<CustomLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomLevel(level: CustomLevelEntity): Long

    @Query("DELETE FROM custom_levels WHERE id = :id")
    suspend fun deleteCustomLevel(id: Int)
}

@Database(
    entities = [LevelProgressEntity::class, UserStatsEntity::class, CustomLevelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
