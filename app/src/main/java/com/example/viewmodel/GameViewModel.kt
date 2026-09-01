package com.example.viewmodel

import android.app.Application
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.CustomLevelEntity
import com.example.data.GameRepository
import com.example.data.LevelProgressEntity
import com.example.data.UserStatsEntity
import com.example.engine.ArrowGameEngine
import com.example.engine.CampaignChapter
import com.example.engine.CampaignLevels
import com.example.engine.LevelMetadata
import com.example.model.ActiveTool
import com.example.model.AppTheme
import com.example.model.ArrowStyle
import com.example.model.BlockerType
import com.example.model.Board
import com.example.model.BumpAnimation
import com.example.model.CellContent
import com.example.model.Direction
import com.example.model.FlyingArrowAnimation
import com.example.model.GameMode
import com.example.model.Particle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class AppScreen {
    HOME,
    GAMEPLAY,
    LEVEL_SELECT,
    DAILY_CHALLENGE,
    RUSH_MODE,
    SANDBOX,
    STATS_ACHIEVEMENTS,
    SETTINGS_THEMES,
    HOW_TO_PLAY
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val gameMode: GameMode = GameMode.CAMPAIGN,
    val currentLevelId: Int = 1,
    val currentLevelMeta: LevelMetadata? = null,
    val board: Board = Board(3, 3),
    val movesCount: Int = 0,
    val elapsedTimeSeconds: Int = 0,
    val comboCount: Int = 0,
    val maxCombo: Int = 0,
    val score: Int = 0,
    val isGameFinished: Boolean = false,
    val starsAwarded: Int = 0,
    val isPaused: Boolean = false,
    val hintedCell: Pair<Int, Int>? = null,
    val activeTool: ActiveTool = ActiveTool.NONE,
    val flyingArrows: List<FlyingArrowAnimation> = emptyList(),
    val bumpAnimations: List<BumpAnimation> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val selectedChapterId: Int = 1,
    val rushTimeRemaining: Int = 60,
    val rushBoardsCleared: Int = 0,
    val isRushRunning: Boolean = false,
    val dailyCompletedToday: Boolean = false,
    val canUndo: Boolean = false,
    // Sandbox Editor
    val sandboxRows: Int = 4,
    val sandboxCols: Int = 4,
    val sandboxSelectedTool: String = "ARROW_UP",
    val sandboxBoard: Board = Board(4, 4),
    val sandboxIsTesting: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)
    val soundManager = SoundManager()
    private val vibrator = application.getSystemService<Vibrator>()

    val levelProgressFlow: StateFlow<List<LevelProgressEntity>> = repository.levelProgressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStatsFlow: StateFlow<UserStatsEntity?> = repository.userStatsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val customLevelsFlow: StateFlow<List<CustomLevelEntity>> = repository.customLevelsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<Board>()
    private var timerJob: Job? = null
    private var animationTickerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
            userStatsFlow.collect { stats ->
                if (stats != null) {
                    soundManager.isSoundEnabled = stats.isSoundEnabled
                    soundManager.isHapticEnabled = stats.isHapticEnabled
                    checkDailyStatus(stats.lastDailyDate)
                }
            }
        }
        startAnimationTicker()
    }

    private fun checkDailyStatus(lastDailyDate: String) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(dailyCompletedToday = lastDailyDate == todayStr)
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playClick()
        triggerHaptic()
        if (screen == AppScreen.GAMEPLAY && _uiState.value.currentScreen != AppScreen.GAMEPLAY) {
            startTimer()
        } else if (screen != AppScreen.GAMEPLAY && _uiState.value.currentScreen == AppScreen.GAMEPLAY) {
            stopTimer()
        }
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectChapter(chapterId: Int) {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(selectedChapterId = chapterId)
    }

    // --- CAMPAIGN MODE ---
    fun startCampaignLevel(levelId: Int) {
        stopTimer()
        undoStack.clear()
        val meta = CampaignLevels.getLevel(levelId)
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.GAMEPLAY,
            gameMode = GameMode.CAMPAIGN,
            currentLevelId = levelId,
            currentLevelMeta = meta,
            board = meta.initialBoard,
            movesCount = 0,
            elapsedTimeSeconds = 0,
            comboCount = 0,
            maxCombo = 0,
            score = 0,
            isGameFinished = false,
            starsAwarded = 0,
            hintedCell = null,
            activeTool = ActiveTool.NONE,
            flyingArrows = emptyList(),
            bumpAnimations = emptyList(),
            canUndo = false
        )
        startTimer()
    }

    fun restartCurrentLevel() {
        soundManager.playClick()
        when (_uiState.value.gameMode) {
            GameMode.CAMPAIGN -> startCampaignLevel(_uiState.value.currentLevelId)
            GameMode.DAILY_CHALLENGE -> startDailyChallenge()
            GameMode.RUSH -> startRushMode()
            GameMode.ZEN -> startZenMode()
            GameMode.SANDBOX -> {
                _uiState.value = _uiState.value.copy(
                    board = _uiState.value.sandboxBoard,
                    movesCount = 0,
                    isGameFinished = false
                )
            }
        }
    }

    fun nextCampaignLevel() {
        val nextId = _uiState.value.currentLevelId + 1
        if (nextId <= 60) {
            startCampaignLevel(nextId)
        } else {
            navigateTo(AppScreen.LEVEL_SELECT)
        }
    }

    // --- DAILY CHALLENGE ---
    fun startDailyChallenge() {
        stopTimer()
        undoStack.clear()
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val seed = todayStr.toLongOrNull() ?: 20260901L
        val board = ArrowGameEngine.generateSolvablePuzzle(
            rows = 5,
            cols = 5,
            arrowDensity = 0.85f,
            includeDiagonals = true,
            includeRotators = true,
            includeBombs = true,
            seed = seed
        )
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.GAMEPLAY,
            gameMode = GameMode.DAILY_CHALLENGE,
            currentLevelMeta = LevelMetadata(999, 1, "Daily Challenge ($todayStr)", 5, 5, board.totalArrows(), 90, board, "Today's Special Brain Puzzle"),
            board = board,
            movesCount = 0,
            elapsedTimeSeconds = 0,
            comboCount = 0,
            maxCombo = 0,
            score = 0,
            isGameFinished = false,
            starsAwarded = 0,
            hintedCell = null,
            activeTool = ActiveTool.NONE,
            flyingArrows = emptyList(),
            bumpAnimations = emptyList(),
            canUndo = false
        )
        startTimer()
    }

    // --- RUSH MODE (Speed Solve Frenzy) ---
    fun startRushMode() {
        stopTimer()
        val board = generateRushBoard(1)
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.GAMEPLAY,
            gameMode = GameMode.RUSH,
            currentLevelMeta = LevelMetadata(888, 1, "Rush Mode", board.rows, board.cols, 50, 60, board),
            board = board,
            movesCount = 0,
            elapsedTimeSeconds = 0,
            comboCount = 0,
            maxCombo = 0,
            score = 0,
            isGameFinished = false,
            starsAwarded = 0,
            rushTimeRemaining = 60,
            rushBoardsCleared = 0,
            isRushRunning = true,
            hintedCell = null,
            activeTool = ActiveTool.NONE
        )
        startRushTimer()
    }

    private fun generateRushBoard(boardNum: Int): Board {
        val size = when {
            boardNum <= 2 -> 3
            boardNum <= 6 -> 4
            else -> 5
        }
        return ArrowGameEngine.generateSolvablePuzzle(
            rows = size,
            cols = size,
            arrowDensity = 0.75f,
            includeDiagonals = boardNum >= 4,
            includeBombs = boardNum >= 7,
            seed = System.currentTimeMillis() + boardNum
        )
    }

    // --- ZEN MODE ---
    fun startZenMode() {
        stopTimer()
        undoStack.clear()
        val board = ArrowGameEngine.generateSolvablePuzzle(
            rows = 5,
            cols = 5,
            arrowDensity = 0.75f,
            includeDiagonals = true,
            seed = System.currentTimeMillis()
        )
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.GAMEPLAY,
            gameMode = GameMode.ZEN,
            currentLevelMeta = LevelMetadata(777, 1, "Zen Garden", 5, 5, 100, 999, board, "Infinite Relaxing Arrow Puzzles"),
            board = board,
            movesCount = 0,
            elapsedTimeSeconds = 0,
            comboCount = 0,
            maxCombo = 0,
            score = 0,
            isGameFinished = false,
            starsAwarded = 0,
            hintedCell = null,
            activeTool = ActiveTool.NONE,
            canUndo = false
        )
        startTimer()
    }

    fun nextZenPuzzle() {
        val size = listOf(4, 5, 6).random()
        val board = ArrowGameEngine.generateSolvablePuzzle(
            rows = size,
            cols = size,
            arrowDensity = 0.8f,
            includeDiagonals = true,
            includeRotators = true,
            seed = System.currentTimeMillis()
        )
        _uiState.value = _uiState.value.copy(
            board = board,
            movesCount = 0,
            isGameFinished = false,
            hintedCell = null
        )
    }

    // --- GAMEPLAY TAP & MOVE INTERACTION ---
    fun onCellTapped(row: Int, col: Int) {
        val state = _uiState.value
        if (state.isGameFinished || state.isPaused) return

        val cell = state.board.getCell(row, col)

        // If tool active:
        if (state.activeTool == ActiveTool.ROTATE_TOOL) {
            if (cell.content is CellContent.Arrow) {
                val rotated = cell.content.direction.rotated90Clockwise()
                val newBoard = state.board.withCell(row, col, cell.content.copy(direction = rotated))
                soundManager.playPowerUp()
                triggerHaptic()
                _uiState.value = state.copy(board = newBoard, activeTool = ActiveTool.NONE)
            }
            return
        }

        if (state.activeTool == ActiveTool.BOMB_TOOL) {
            if (cell.content is CellContent.Arrow || cell.content is CellContent.Blocker) {
                val newBoard = state.board.withCell(row, col, CellContent.Empty)
                soundManager.playBombExplode()
                triggerHaptic()
                spawnExplosionParticles(row, col)
                _uiState.value = state.copy(board = newBoard, activeTool = ActiveTool.NONE)
                checkBoardCompletion(newBoard)
            }
            return
        }

        // Standard Arrow Tap
        val arrow = cell.content as? CellContent.Arrow ?: return

        // Push current board to undo stack
        undoStack.add(state.board)
        if (undoStack.size > 20) undoStack.removeAt(0)

        val escapeResult = ArrowGameEngine.checkEscape(state.board, row, col)

        if (escapeResult.canEscape) {
            // SUCCESSFUL ESCAPE!
            val outcome = ArrowGameEngine.executeEscape(state.board, row, col)
            if (outcome != null) {
                val newCombo = state.comboCount + 1
                val maxCombo = maxOf(state.maxCombo, newCombo)
                val pointsAdded = 100 * newCombo

                soundManager.playTapSuccess(newCombo - 1)
                soundManager.playWhoosh()
                triggerHaptic()

                // Spawn flying animation
                val flying = FlyingArrowAnimation(
                    startRow = row,
                    startCol = col,
                    direction = arrow.direction,
                    arrowContent = arrow,
                    startTimeMs = System.currentTimeMillis()
                )

                // Spawn sparkle particles along trajectory
                spawnTrailParticles(row, col, arrow.direction)

                if (arrow.isBomb) {
                    soundManager.playBombExplode()
                    spawnExplosionParticles(row, col)
                }

                _uiState.value = state.copy(
                    board = outcome.newBoard,
                    movesCount = state.movesCount + 1,
                    comboCount = newCombo,
                    maxCombo = maxCombo,
                    score = state.score + pointsAdded,
                    flyingArrows = state.flyingArrows + flying,
                    hintedCell = null,
                    canUndo = undoStack.isNotEmpty()
                )

                viewModelScope.launch {
                    repository.incrementArrowsEscaped(1, newCombo)
                }

                checkBoardCompletion(outcome.newBoard)
            }
        } else {
            // BLOCKED!
            soundManager.playBlockedThud()
            triggerHaptic()

            val bump = BumpAnimation(
                row = row,
                col = col,
                blockerRow = escapeResult.blockerRow,
                blockerCol = escapeResult.blockerCol,
                direction = arrow.direction,
                startTimeMs = System.currentTimeMillis()
            )

            _uiState.value = state.copy(
                movesCount = state.movesCount + 1,
                comboCount = 0, // Reset combo
                bumpAnimations = state.bumpAnimations + bump,
                canUndo = undoStack.isNotEmpty()
            )
        }
    }

    private fun checkBoardCompletion(newBoard: Board) {
        if (newBoard.isCleared()) {
            val state = _uiState.value
            soundManager.playVictory()
            triggerHaptic()
            spawnVictoryCelebrationParticles()

            val moves = state.movesCount + 1
            val timeSec = state.elapsedTimeSeconds
            val meta = state.currentLevelMeta

            // Calculate Stars
            val targetMoves = meta?.targetMoves ?: 10
            val targetTime = meta?.targetTimeSeconds ?: 30
            val stars = when {
                moves <= targetMoves && timeSec <= targetTime -> 3
                moves <= (targetMoves * 1.4).toInt() -> 2
                else -> 1
            }

            if (state.gameMode == GameMode.CAMPAIGN) {
                viewModelScope.launch {
                    repository.saveLevelCompletion(state.currentLevelId, stars, timeSec, moves)
                }
            } else if (state.gameMode == GameMode.DAILY_CHALLENGE) {
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                viewModelScope.launch {
                    repository.recordDailyCompletion(todayStr)
                }
            } else if (state.gameMode == GameMode.RUSH) {
                // In Rush mode, board cleared -> award extra time + new board
                val newBoardsCleared = state.rushBoardsCleared + 1
                val addedTime = 8
                val nextBoard = generateRushBoard(newBoardsCleared + 1)
                _uiState.value = state.copy(
                    board = nextBoard,
                    rushBoardsCleared = newBoardsCleared,
                    rushTimeRemaining = state.rushTimeRemaining + addedTime,
                    score = state.score + 500 * (newBoardsCleared)
                )
                return
            }

            _uiState.value = _uiState.value.copy(
                isGameFinished = true,
                starsAwarded = stars
            )
            stopTimer()
        }
    }

    // --- POWER UPS ---
    fun useHint() {
        viewModelScope.launch {
            val canUse = repository.usePowerUp("HINT")
            if (canUse) {
                val hint = ArrowGameEngine.findSolvableArrow(_uiState.value.board)
                if (hint != null) {
                    soundManager.playPowerUp()
                    triggerHaptic()
                    _uiState.value = _uiState.value.copy(hintedCell = hint)
                }
            }
        }
    }

    fun toggleRotateTool() {
        viewModelScope.launch {
            if (_uiState.value.activeTool == ActiveTool.ROTATE_TOOL) {
                _uiState.value = _uiState.value.copy(activeTool = ActiveTool.NONE)
            } else {
                val canUse = repository.usePowerUp("ROTATE")
                if (canUse) {
                    soundManager.playPowerUp()
                    _uiState.value = _uiState.value.copy(activeTool = ActiveTool.ROTATE_TOOL)
                }
            }
        }
    }

    fun toggleBombTool() {
        viewModelScope.launch {
            if (_uiState.value.activeTool == ActiveTool.BOMB_TOOL) {
                _uiState.value = _uiState.value.copy(activeTool = ActiveTool.NONE)
            } else {
                val canUse = repository.usePowerUp("BOMB")
                if (canUse) {
                    soundManager.playPowerUp()
                    _uiState.value = _uiState.value.copy(activeTool = ActiveTool.BOMB_TOOL)
                }
            }
        }
    }

    fun undoMove() {
        if (undoStack.isNotEmpty()) {
            val prevBoard = undoStack.removeAt(undoStack.lastIndex)
            soundManager.playClick()
            _uiState.value = _uiState.value.copy(
                board = prevBoard,
                hintedCell = null,
                activeTool = ActiveTool.NONE,
                canUndo = undoStack.isNotEmpty()
            )
        }
    }

    // --- TIMERS ---
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_uiState.value.isPaused && !_uiState.value.isGameFinished) {
                    _uiState.value = _uiState.value.copy(
                        elapsedTimeSeconds = _uiState.value.elapsedTimeSeconds + 1
                    )
                }
            }
        }
    }

    private fun startRushTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val curr = _uiState.value.rushTimeRemaining
                if (curr <= 1) {
                    _uiState.value = _uiState.value.copy(rushTimeRemaining = 0, isGameFinished = true)
                    soundManager.playBlockedThud()
                    break
                } else {
                    _uiState.value = _uiState.value.copy(rushTimeRemaining = curr - 1)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    // --- PARTICLE AND ANIMATION TICKER ---
    private fun startAnimationTicker() {
        animationTickerJob = viewModelScope.launch {
            while (true) {
                delay(16) // ~60fps
                val now = System.currentTimeMillis()

                // Clean up flying arrows
                val activeFlying = _uiState.value.flyingArrows.filter {
                    now - it.startTimeMs < it.durationMs
                }

                // Clean up bumps
                val activeBumps = _uiState.value.bumpAnimations.filter {
                    now - it.startTimeMs < it.durationMs
                }

                // Update particles
                val updatedParticles = _uiState.value.particles.mapNotNull { p ->
                    val age = now - p.createdAt
                    if (age > p.lifeMs) null
                    else {
                        val progress = age.toFloat() / p.lifeMs
                        p.copy(
                            x = p.x + p.vx,
                            y = p.y + p.vy,
                            alpha = (1f - progress).coerceIn(0f, 1f)
                        )
                    }
                }

                if (activeFlying.size != _uiState.value.flyingArrows.size ||
                    activeBumps.size != _uiState.value.bumpAnimations.size ||
                    updatedParticles.size != _uiState.value.particles.size ||
                    updatedParticles.isNotEmpty()
                ) {
                    _uiState.value = _uiState.value.copy(
                        flyingArrows = activeFlying,
                        bumpAnimations = activeBumps,
                        particles = updatedParticles
                    )
                }
            }
        }
    }

    private fun spawnTrailParticles(row: Int, col: Int, dir: Direction) {
        val count = 8
        val colors = listOf(0xFF00F0FF, 0xFFFF007F, 0xFFFFD600, 0xFF00FF88)
        val newParticles = (0 until count).map {
            val angle = (Random.nextFloat() * Math.PI * 2).toFloat()
            val speed = Random.nextFloat() * 4f + 2f
            Particle(
                x = col.toFloat(),
                y = row.toFloat(),
                vx = cos(angle) * speed * 0.03f,
                vy = sin(angle) * speed * 0.03f,
                color = colors.random(),
                size = Random.nextFloat() * 8f + 4f,
                lifeMs = 400L
            )
        }
        _uiState.value = _uiState.value.copy(particles = _uiState.value.particles + newParticles)
    }

    private fun spawnExplosionParticles(row: Int, col: Int) {
        val count = 20
        val colors = listOf(0xFFFF3300, 0xFFFF9900, 0xFFFFCC00, 0xFFFFFFFF)
        val newParticles = (0 until count).map {
            val angle = (Random.nextFloat() * Math.PI * 2).toFloat()
            val speed = Random.nextFloat() * 8f + 3f
            Particle(
                x = col.toFloat(),
                y = row.toFloat(),
                vx = cos(angle) * speed * 0.05f,
                vy = sin(angle) * speed * 0.05f,
                color = colors.random(),
                size = Random.nextFloat() * 12f + 6f,
                lifeMs = 600L
            )
        }
        _uiState.value = _uiState.value.copy(particles = _uiState.value.particles + newParticles)
    }

    private fun spawnVictoryCelebrationParticles() {
        val count = 40
        val colors = listOf(0xFFFFD700, 0xFFFF1493, 0xFF00FFFF, 0xFF32CD32, 0xFFFF4500)
        val newParticles = (0 until count).map {
            val angle = (Random.nextFloat() * Math.PI * 2).toFloat()
            val speed = Random.nextFloat() * 10f + 4f
            Particle(
                x = 2f,
                y = 2f,
                vx = cos(angle) * speed * 0.08f,
                vy = sin(angle) * speed * 0.08f,
                color = colors.random(),
                size = Random.nextFloat() * 14f + 6f,
                lifeMs = 1200L
            )
        }
        _uiState.value = _uiState.value.copy(particles = _uiState.value.particles + newParticles)
    }

    private fun triggerHaptic() {
        if (soundManager.isHapticEnabled) {
            try {
                vibrator?.vibrate(VibrationEffect.createOneShot(25L, VibrationEffect.DEFAULT_AMPLITUDE))
            } catch (_: Exception) {}
        }
    }

    // --- SETTINGS & THEMES ---
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            repository.updateSettings(theme = theme.name)
        }
    }

    fun setArrowStyle(style: ArrowStyle) {
        viewModelScope.launch {
            repository.updateSettings(arrowStyle = style.name)
        }
    }

    fun toggleSound(enabled: Boolean) {
        soundManager.isSoundEnabled = enabled
        viewModelScope.launch {
            repository.updateSettings(sound = enabled)
        }
    }

    fun toggleHaptic(enabled: Boolean) {
        soundManager.isHapticEnabled = enabled
        viewModelScope.launch {
            repository.updateSettings(haptic = enabled)
        }
    }

    // --- SANDBOX CREATOR ---
    fun setSandboxGridSize(rows: Int, cols: Int) {
        _uiState.value = _uiState.value.copy(
            sandboxRows = rows,
            sandboxCols = cols,
            sandboxBoard = Board(rows, cols)
        )
    }

    fun selectSandboxTool(tool: String) {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(sandboxSelectedTool = tool)
    }

    fun onSandboxCellClick(r: Int, c: Int) {
        soundManager.playClick()
        val currentContent = _uiState.value.sandboxBoard.getCell(r, c).content
        val tool = _uiState.value.sandboxSelectedTool
        val newContent = when (tool) {
            "ARROW_UP" -> CellContent.Arrow(Direction.UP, 0)
            "ARROW_DOWN" -> CellContent.Arrow(Direction.DOWN, 1)
            "ARROW_LEFT" -> CellContent.Arrow(Direction.LEFT, 2)
            "ARROW_RIGHT" -> CellContent.Arrow(Direction.RIGHT, 3)
            "ARROW_UP_LEFT" -> CellContent.Arrow(Direction.UP_LEFT, 0)
            "ARROW_UP_RIGHT" -> CellContent.Arrow(Direction.UP_RIGHT, 1)
            "ARROW_DOWN_LEFT" -> CellContent.Arrow(Direction.DOWN_LEFT, 2)
            "ARROW_DOWN_RIGHT" -> CellContent.Arrow(Direction.DOWN_RIGHT, 3)
            "ROTATOR" -> CellContent.Arrow(Direction.UP, 0, isRotator = true)
            "BOMB" -> CellContent.Arrow(Direction.UP, 1, isBomb = true)
            "WALL" -> CellContent.Blocker(BlockerType.WALL)
            "ICE" -> CellContent.Blocker(BlockerType.ICE)
            "CLEAR" -> CellContent.Empty
            else -> CellContent.Empty
        }
        val updated = _uiState.value.sandboxBoard.withCell(r, c, newContent)
        _uiState.value = _uiState.value.copy(sandboxBoard = updated)
    }

    fun testSandboxLevel() {
        val board = _uiState.value.sandboxBoard
        if (board.totalArrows() == 0) return
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.GAMEPLAY,
            gameMode = GameMode.SANDBOX,
            currentLevelMeta = LevelMetadata(0, 1, "Custom Sandbox Puzzle", board.rows, board.cols, board.totalArrows(), 60, board, "Your Custom Creation"),
            board = board,
            movesCount = 0,
            elapsedTimeSeconds = 0,
            comboCount = 0,
            maxCombo = 0,
            score = 0,
            isGameFinished = false,
            starsAwarded = 0,
            hintedCell = null,
            activeTool = ActiveTool.NONE
        )
        startTimer()
    }

    fun startCampaign() {
        startCampaignLevel(_uiState.value.currentLevelId)
    }

    fun startRush() {
        startRushMode()
    }

    fun startZen() {
        startZenMode()
    }

    fun onCellClick(r: Int, c: Int) {
        onCellTapped(r, c)
    }

    fun restartLevel() {
        restartCurrentLevel()
    }

    fun nextLevel() {
        if (_uiState.value.gameMode == GameMode.ZEN) {
            nextZenPuzzle()
        } else {
            nextCampaignLevel()
        }
    }

    fun testPlaySandbox() {
        testSandboxLevel()
    }

    fun saveSandboxLevel(title: String) {
        viewModelScope.launch {
            // Encode board
            val board = _uiState.value.sandboxBoard
            repository.saveCustomLevel(title, board.rows, board.cols, "custom_level_data")
            soundManager.playVictory()
        }
    }
}
