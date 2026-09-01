package com.example.engine

import com.example.model.BlockerType
import com.example.model.Board
import com.example.model.CellContent
import com.example.model.Direction

data class CampaignChapter(
    val id: Int,
    val title: String,
    val description: String,
    val iconName: String,
    val levelStartId: Int,
    val levelEndId: Int
)

data class LevelMetadata(
    val id: Int,
    val chapterId: Int,
    val title: String,
    val rows: Int,
    val cols: Int,
    val targetMoves: Int,
    val targetTimeSeconds: Int,
    val initialBoard: Board,
    val hintText: String = ""
)

object CampaignLevels {

    val chapters = listOf(
        CampaignChapter(
            id = 1,
            title = "First Flight",
            description = "Master the basics of arrow trajectories and clear outer edges first.",
            iconName = "straight",
            levelStartId = 1,
            levelEndId = 10
        ),
        CampaignChapter(
            id = 2,
            title = "Diagonal Horizons",
            description = "Navigate cross-cutting angles and 8-way directional paths.",
            iconName = "diagonal",
            levelStartId = 11,
            levelEndId = 20
        ),
        CampaignChapter(
            id = 3,
            title = "Walls & Ice Cracks",
            description = "Work around indestructible blocks and shatter frosty ice tiles.",
            iconName = "ice",
            levelStartId = 21,
            levelEndId = 30
        ),
        CampaignChapter(
            id = 4,
            title = "Rotator Nexus",
            description = "Arrows rotate 90° clockwise with each move. Plan steps ahead!",
            iconName = "rotator",
            levelStartId = 31,
            levelEndId = 40
        ),
        CampaignChapter(
            id = 5,
            title = "Wormhole Portals",
            description = "Teleport through quantum gateways across the board.",
            iconName = "portal",
            levelStartId = 41,
            levelEndId = 50
        ),
        CampaignChapter(
            id = 6,
            title = "Grandmaster Maze",
            description = "Massive 6x6 & 7x7 intricate brain-twisting arrow labyrinths.",
            iconName = "star",
            levelStartId = 51,
            levelEndId = 60
        )
    )

    private val levelCache = mutableMapOf<Int, LevelMetadata>()

    fun getLevel(id: Int): LevelMetadata {
        return levelCache.getOrPut(id) {
            buildLevel(id)
        }
    }

    private fun buildLevel(id: Int): LevelMetadata {
        return when (id) {
            1 -> {
                // Tutorial 1: Simple 3x3 with clear outer exits
                val board = Board(3, 3)
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(1, 0, CellContent.Arrow(Direction.LEFT, 1))
                    .withCell(1, 2, CellContent.Arrow(Direction.RIGHT, 2))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN, 3))
                LevelMetadata(1, 1, "Cardinal Points", 3, 3, 4, 20, board, "Tap arrows that point freely off the board!")
            }
            2 -> {
                // Tutorial 2: Sequencing - center arrow blocked until outer leaves
                val board = Board(3, 3)
                    .withCell(1, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 1))
                    .withCell(1, 0, CellContent.Arrow(Direction.LEFT, 2))
                    .withCell(1, 2, CellContent.Arrow(Direction.RIGHT, 3))
                LevelMetadata(2, 1, "The Queue", 3, 3, 4, 25, board, "Clear the top arrow first to make way for the center!")
            }
            3 -> {
                // Pinwheel 3x3
                val board = Board(3, 3)
                    .withCell(0, 0, CellContent.Arrow(Direction.RIGHT, 0))
                    .withCell(0, 2, CellContent.Arrow(Direction.DOWN, 1))
                    .withCell(2, 2, CellContent.Arrow(Direction.LEFT, 2))
                    .withCell(2, 0, CellContent.Arrow(Direction.UP, 3))
                    .withCell(1, 1, CellContent.Arrow(Direction.UP, 0))
                LevelMetadata(3, 1, "Pinwheel Vortex", 3, 3, 5, 30, board, "Look for the free perimeter escape route.")
            }
            4 -> {
                // 3x3 Full Grid
                val board = Board(3, 3)
                    .withCell(0, 0, CellContent.Arrow(Direction.LEFT, 0))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 1))
                    .withCell(0, 2, CellContent.Arrow(Direction.RIGHT, 2))
                    .withCell(1, 0, CellContent.Arrow(Direction.LEFT, 3))
                    .withCell(1, 1, CellContent.Arrow(Direction.RIGHT, 0))
                    .withCell(1, 2, CellContent.Arrow(Direction.RIGHT, 1))
                    .withCell(2, 0, CellContent.Arrow(Direction.LEFT, 2))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN, 3))
                    .withCell(2, 2, CellContent.Arrow(Direction.DOWN, 0))
                LevelMetadata(4, 1, "Compact Matrix", 3, 3, 9, 35, board, "Untangle from the outside inwards.")
            }
            5 -> {
                // 4x4 Spiral
                val board = Board(4, 4)
                    .withCell(0, 0, CellContent.Arrow(Direction.RIGHT, 0))
                    .withCell(0, 1, CellContent.Arrow(Direction.RIGHT, 1))
                    .withCell(0, 2, CellContent.Arrow(Direction.RIGHT, 2))
                    .withCell(0, 3, CellContent.Arrow(Direction.DOWN, 3))
                    .withCell(1, 3, CellContent.Arrow(Direction.DOWN, 0))
                    .withCell(2, 3, CellContent.Arrow(Direction.DOWN, 1))
                    .withCell(3, 3, CellContent.Arrow(Direction.LEFT, 2))
                    .withCell(3, 2, CellContent.Arrow(Direction.LEFT, 3))
                    .withCell(3, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(1, 1, CellContent.Arrow(Direction.RIGHT, 1))
                    .withCell(1, 2, CellContent.Arrow(Direction.DOWN, 2))
                    .withCell(2, 1, CellContent.Arrow(Direction.LEFT, 3))
                LevelMetadata(5, 1, "Spiral Thread", 4, 4, 12, 45, board, "Follow the coil to find the lead arrow.")
            }
            6 -> {
                // 4x4 Cross Roads
                val board = Board(4, 4)
                    .withCell(1, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(1, 2, CellContent.Arrow(Direction.RIGHT, 1))
                    .withCell(2, 2, CellContent.Arrow(Direction.DOWN, 2))
                    .withCell(2, 1, CellContent.Arrow(Direction.LEFT, 3))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(1, 3, CellContent.Arrow(Direction.RIGHT, 1))
                    .withCell(3, 2, CellContent.Arrow(Direction.DOWN, 2))
                    .withCell(2, 0, CellContent.Arrow(Direction.LEFT, 3))
                LevelMetadata(6, 1, "Crossroads", 4, 4, 8, 35, board)
            }
            7 -> {
                // 4x4 Double Lock
                val board = Board(4, 4)
                    .withCell(0, 1, CellContent.Arrow(Direction.LEFT, 0))
                    .withCell(0, 2, CellContent.Arrow(Direction.RIGHT, 1))
                    .withCell(1, 0, CellContent.Arrow(Direction.UP, 2))
                    .withCell(1, 1, CellContent.Arrow(Direction.RIGHT, 3))
                    .withCell(1, 2, CellContent.Arrow(Direction.LEFT, 0))
                    .withCell(1, 3, CellContent.Arrow(Direction.UP, 1))
                    .withCell(2, 0, CellContent.Arrow(Direction.DOWN, 2))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN, 3))
                    .withCell(2, 2, CellContent.Arrow(Direction.UP, 0))
                    .withCell(2, 3, CellContent.Arrow(Direction.DOWN, 1))
                    .withCell(3, 1, CellContent.Arrow(Direction.LEFT, 2))
                    .withCell(3, 2, CellContent.Arrow(Direction.RIGHT, 3))
                LevelMetadata(7, 1, "Interlock", 4, 4, 12, 45, board)
            }
            8 -> {
                val board = ArrowGameEngine.generateSolvablePuzzle(4, 4, arrowDensity = 0.8f, seed = 1008L)
                LevelMetadata(8, 1, "Tangled Web", 4, 4, board.totalArrows(), 50, board)
            }
            9 -> {
                val board = ArrowGameEngine.generateSolvablePuzzle(4, 4, arrowDensity = 0.85f, seed = 1009L)
                LevelMetadata(9, 1, "Perimeter Breach", 4, 4, board.totalArrows(), 55, board)
            }
            10 -> {
                val board = ArrowGameEngine.generateSolvablePuzzle(4, 4, arrowDensity = 0.95f, seed = 1010L)
                LevelMetadata(10, 1, "Chapter 1 Finale", 4, 4, board.totalArrows(), 60, board)
            }

            // Chapter 2: Diagonal Drift (11 - 20)
            11 -> {
                val board = Board(4, 4)
                    .withCell(1, 1, CellContent.Arrow(Direction.UP_LEFT, 0))
                    .withCell(1, 2, CellContent.Arrow(Direction.UP_RIGHT, 1))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN_LEFT, 2))
                    .withCell(2, 2, CellContent.Arrow(Direction.DOWN_RIGHT, 3))
                    .withCell(0, 0, CellContent.Arrow(Direction.UP_LEFT, 0))
                    .withCell(0, 3, CellContent.Arrow(Direction.UP_RIGHT, 1))
                    .withCell(3, 0, CellContent.Arrow(Direction.DOWN_LEFT, 2))
                    .withCell(3, 3, CellContent.Arrow(Direction.DOWN_RIGHT, 3))
                LevelMetadata(11, 2, "X-Marks", 4, 4, 8, 35, board, "Diagonals fly diagonally toward corner boundaries!")
            }
            12 -> {
                val board = Board(4, 4)
                    .withCell(1, 1, CellContent.Arrow(Direction.DOWN_RIGHT, 0))
                    .withCell(2, 2, CellContent.Arrow(Direction.UP_LEFT, 1))
                    .withCell(0, 3, CellContent.Arrow(Direction.UP_RIGHT, 2))
                    .withCell(3, 0, CellContent.Arrow(Direction.DOWN_LEFT, 3))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(3, 2, CellContent.Arrow(Direction.DOWN, 1))
                LevelMetadata(12, 2, "Diagonal Collision", 4, 4, 6, 35, board)
            }
            in 13..20 -> {
                val seed = 2000L + id
                val board = ArrowGameEngine.generateSolvablePuzzle(
                    rows = if (id > 16) 5 else 4,
                    cols = if (id > 16) 5 else 4,
                    arrowDensity = 0.8f,
                    includeDiagonals = true,
                    seed = seed
                )
                LevelMetadata(id, 2, "Diagonal Level $id", board.rows, board.cols, board.totalArrows(), 60, board)
            }

            // Chapter 3: Walls & Ice Cracks (21 - 30)
            21 -> {
                val board = Board(4, 4)
                    .withCell(1, 1, CellContent.Blocker(BlockerType.WALL))
                    .withCell(2, 2, CellContent.Blocker(BlockerType.WALL))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 0))
                    .withCell(1, 0, CellContent.Arrow(Direction.LEFT, 1))
                    .withCell(2, 3, CellContent.Arrow(Direction.RIGHT, 2))
                    .withCell(3, 2, CellContent.Arrow(Direction.DOWN, 3))
                    .withCell(1, 2, CellContent.Arrow(Direction.UP, 0))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN, 1))
                LevelMetadata(21, 3, "Fortress Bricks", 4, 4, 6, 40, board, "Arrows cannot pass through solid walls.")
            }
            22 -> {
                val board = Board(4, 4)
                    .withCell(1, 2, CellContent.Blocker(BlockerType.ICE))
                    .withCell(1, 1, CellContent.Arrow(Direction.RIGHT, 0, isBomb = true))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 1))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN, 2))
                    .withCell(1, 3, CellContent.Arrow(Direction.RIGHT, 3))
                LevelMetadata(22, 3, "Ice Shatter", 4, 4, 4, 40, board, "Clearing an adjacent arrow shatters nearby ice!")
            }
            in 23..30 -> {
                val seed = 3000L + id
                var board = ArrowGameEngine.generateSolvablePuzzle(
                    rows = 5,
                    cols = 5,
                    arrowDensity = 0.75f,
                    includeDiagonals = true,
                    includeBombs = true,
                    seed = seed
                )
                // Add a wall in non-blocking empty spot
                val emptySpots = mutableListOf<Pair<Int, Int>>()
                for (r in 0 until 5) {
                    for (c in 0 until 5) {
                        if (board.getCell(r, c).content is CellContent.Empty) {
                            emptySpots.add(r to c)
                        }
                    }
                }
                if (emptySpots.isNotEmpty()) {
                    val p = emptySpots.first()
                    board = board.withCell(p.first, p.second, CellContent.Blocker(BlockerType.WALL))
                }
                LevelMetadata(id, 3, "Barrier Challenge $id", 5, 5, board.totalArrows(), 70, board)
            }

            // Chapter 4: Rotators (31 - 40)
            31 -> {
                val board = Board(4, 4)
                    .withCell(1, 1, CellContent.Arrow(Direction.DOWN, 0, isRotator = true))
                    .withCell(1, 2, CellContent.Arrow(Direction.UP, 1))
                    .withCell(2, 1, CellContent.Arrow(Direction.DOWN, 2))
                    .withCell(0, 1, CellContent.Arrow(Direction.UP, 3))
                    .withCell(0, 2, CellContent.Arrow(Direction.RIGHT, 0))
                LevelMetadata(31, 4, "Rotating Gears", 4, 4, 5, 45, board, "Rotator arrows turn 90° clockwise with each move!")
            }
            in 32..40 -> {
                val seed = 4000L + id
                val board = ArrowGameEngine.generateSolvablePuzzle(
                    rows = 5,
                    cols = 5,
                    arrowDensity = 0.8f,
                    includeDiagonals = true,
                    includeRotators = true,
                    seed = seed
                )
                LevelMetadata(id, 4, "Rotator Shift $id", 5, 5, board.totalArrows(), 80, board)
            }

            // Chapter 5: Wormhole Portals (41 - 50)
            41 -> {
                val board = Board(5, 5)
                    .withCell(2, 1, CellContent.Portal(1, 2))
                    .withCell(2, 3, CellContent.Portal(2, 1))
                    .withCell(2, 0, CellContent.Arrow(Direction.RIGHT, 0))
                    .withCell(0, 3, CellContent.Arrow(Direction.UP, 1))
                    .withCell(4, 3, CellContent.Arrow(Direction.DOWN, 2))
                LevelMetadata(41, 5, "Quantum Gate", 5, 5, 3, 40, board, "Arrows entering a portal exit through the matching gate!")
            }
            in 42..50 -> {
                val seed = 5000L + id
                var board = ArrowGameEngine.generateSolvablePuzzle(
                    rows = 6,
                    cols = 6,
                    arrowDensity = 0.75f,
                    includeDiagonals = true,
                    seed = seed
                )
                LevelMetadata(id, 5, "Portal Warp $id", 6, 6, board.totalArrows(), 90, board)
            }

            // Chapter 6: Grandmaster Mazes (51 - 60)
            else -> {
                val seed = 6000L + id
                val board = ArrowGameEngine.generateSolvablePuzzle(
                    rows = if (id > 55) 7 else 6,
                    cols = if (id > 55) 7 else 6,
                    arrowDensity = 0.85f,
                    includeDiagonals = true,
                    includeRotators = true,
                    includeBombs = true,
                    seed = seed
                )
                LevelMetadata(id, 6, "Grandmaster Trial ${id - 50}", board.rows, board.cols, board.totalArrows(), 120, board)
            }
        }
    }
}
