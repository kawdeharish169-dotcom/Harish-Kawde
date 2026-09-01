package com.example.model

enum class Direction(val dRow: Int, val dCol: Int, val angleDegrees: Float) {
    UP(-1, 0, 0f),
    UP_RIGHT(-1, 1, 45f),
    RIGHT(0, 1, 90f),
    DOWN_RIGHT(1, 1, 135f),
    DOWN(1, 0, 180f),
    DOWN_LEFT(1, -1, 225f),
    LEFT(0, -1, 270f),
    UP_LEFT(-1, -1, 315f);

    fun rotatedClockwise(): Direction {
        return when (this) {
            UP -> UP_RIGHT
            UP_RIGHT -> RIGHT
            RIGHT -> DOWN_RIGHT
            DOWN_RIGHT -> DOWN
            DOWN -> DOWN_LEFT
            DOWN_LEFT -> LEFT
            LEFT -> UP_LEFT
            UP_LEFT -> UP
        }
    }

    fun rotated90Clockwise(): Direction {
        return when (this) {
            UP -> RIGHT
            UP_RIGHT -> DOWN_RIGHT
            RIGHT -> DOWN
            DOWN_RIGHT -> DOWN_LEFT
            DOWN -> LEFT
            DOWN_LEFT -> UP_LEFT
            LEFT -> UP
            UP_LEFT -> UP_RIGHT
        }
    }

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            UP_RIGHT -> DOWN_LEFT
            RIGHT -> LEFT
            DOWN_RIGHT -> UP_LEFT
            DOWN -> UP
            DOWN_LEFT -> UP_RIGHT
            LEFT -> RIGHT
            UP_LEFT -> DOWN_RIGHT
        }
    }
}

enum class BlockerType {
    WALL,       // Indestructible static wall
    ICE,        // Shatters when an adjacent arrow takes off
    LOCKED_GATE // Opens when a key arrow is cleared
}

sealed class CellContent {
    object Empty : CellContent()

    data class Arrow(
        val direction: Direction,
        val colorVariant: Int = 0,
        val isRotator: Boolean = false,
        val isBomb: Boolean = false,
        val isKey: Boolean = false,
        val isLocked: Boolean = false,
        val hitsRemaining: Int = 1
    ) : CellContent()

    data class Blocker(
        val type: BlockerType,
        val isUnlocked: Boolean = false
    ) : CellContent()

    data class Portal(
        val portalId: Int,
        val targetPortalId: Int
    ) : CellContent()
}

data class GridCell(
    val row: Int,
    val col: Int,
    val content: CellContent
)

data class Board(
    val rows: Int,
    val cols: Int,
    val grid: Map<Pair<Int, Int>, GridCell> = emptyMap()
) {
    fun getCell(r: Int, c: Int): GridCell {
        return grid[r to c] ?: GridCell(r, c, CellContent.Empty)
    }

    fun withCell(r: Int, c: Int, content: CellContent): Board {
        val newGrid = grid.toMutableMap()
        if (content == CellContent.Empty) {
            newGrid.remove(r to c)
        } else {
            newGrid[r to c] = GridCell(r, c, content)
        }
        return copy(grid = newGrid)
    }

    fun remainingArrows(): Int {
        return grid.values.count { it.content is CellContent.Arrow }
    }

    fun totalArrows(): Int {
        return grid.values.count { it.content is CellContent.Arrow }
    }

    fun isCleared(): Boolean {
        return remainingArrows() == 0
    }
}

enum class GameMode {
    CAMPAIGN,
    DAILY_CHALLENGE,
    RUSH,
    ZEN,
    SANDBOX
}

enum class ActiveTool {
    NONE,
    ROTATE_TOOL,
    BOMB_TOOL
}

enum class AppTheme(val displayName: String) {
    CYBER_NEON("Cyber Neon"),
    ZEN_GARDEN("Zen Garden"),
    MIDNIGHT_VELVET("Midnight Velvet"),
    RETRO_ARCADE("Retro Arcade"),
    MINIMAL_DARK("Minimal Dark")
}

enum class ArrowStyle(val displayName: String) {
    MODERN_TRIANGLE("Modern Triangle"),
    CHEVRON_DART("Chevron Dart"),
    LASER_GLOW("Laser Glow"),
    PAPER_PLANE("Paper Plane")
}

data class FlyingArrowAnimation(
    val startRow: Int,
    val startCol: Int,
    val direction: Direction,
    val arrowContent: CellContent.Arrow,
    val startTimeMs: Long,
    val durationMs: Long = 350L
)

data class BumpAnimation(
    val row: Int,
    val col: Int,
    val blockerRow: Int,
    val blockerCol: Int,
    val direction: Direction,
    val startTimeMs: Long,
    val durationMs: Long = 250L
)

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Long,
    val size: Float,
    val alpha: Float = 1f,
    val lifeMs: Long = 500L,
    val createdAt: Long = System.currentTimeMillis()
)
