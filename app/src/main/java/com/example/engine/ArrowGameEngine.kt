package com.example.engine

import com.example.model.BlockerType
import com.example.model.Board
import com.example.model.CellContent
import com.example.model.Direction
import com.example.model.GridCell
import kotlin.random.Random

data class EscapeResult(
    val canEscape: Boolean,
    val blockerRow: Int = -1,
    val blockerCol: Int = -1,
    val path: List<Pair<Int, Int>> = emptyList(),
    val hitIceCells: List<Pair<Int, Int>> = emptyList()
)

data class MoveOutcome(
    val newBoard: Board,
    val escapedArrow: CellContent.Arrow,
    val escapePath: List<Pair<Int, Int>>,
    val destroyedBlockers: List<Pair<Int, Int>>,
    val openedGates: List<Pair<Int, Int>>,
    val rotatedArrows: List<Pair<Int, Int>>
)

object ArrowGameEngine {

    /**
     * Checks whether an arrow at (startRow, startCol) has a clear path to escape out of the board.
     * Takes into account portals, walls, blockers, and locked gates.
     */
    fun checkEscape(board: Board, startRow: Int, startCol: Int): EscapeResult {
        val cell = board.getCell(startRow, startCol)
        val arrow = cell.content as? CellContent.Arrow ?: return EscapeResult(false)

        var currRow = startRow
        var currCol = startCol
        var currDir = arrow.direction
        val path = mutableListOf<Pair<Int, Int>>()
        val visitedCells = mutableSetOf<Pair<Int, Int>>()
        var portalJumps = 0

        while (true) {
            val nextRow = currRow + currDir.dRow
            val nextCol = currCol + currDir.dCol

            // Out of bounds means escaped!
            if (nextRow !in 0 until board.rows || nextCol !in 0 until board.cols) {
                path.add(nextRow to nextCol)
                return EscapeResult(canEscape = true, path = path)
            }

            val nextPos = nextRow to nextCol
            if (nextPos in visitedCells) {
                // Infinite loop through portals
                return EscapeResult(canEscape = false, blockerRow = nextRow, blockerCol = nextCol, path = path)
            }
            visitedCells.add(nextPos)
            path.add(nextPos)

            val nextCell = board.getCell(nextRow, nextCol)
            when (val content = nextCell.content) {
                is CellContent.Empty -> {
                    currRow = nextRow
                    currCol = nextCol
                }
                is CellContent.Arrow -> {
                    // Blocked by another arrow
                    return EscapeResult(canEscape = false, blockerRow = nextRow, blockerCol = nextCol, path = path)
                }
                is CellContent.Blocker -> {
                    if (content.isUnlocked) {
                        currRow = nextRow
                        currCol = nextCol
                    } else {
                        // Blocked by wall, ice, or locked gate
                        return EscapeResult(canEscape = false, blockerRow = nextRow, blockerCol = nextCol, path = path)
                    }
                }
                is CellContent.Portal -> {
                    portalJumps++
                    if (portalJumps > 10) {
                        return EscapeResult(canEscape = false, blockerRow = nextRow, blockerCol = nextCol, path = path)
                    }
                    // Find matching target portal on board
                    val targetPortalCell = board.grid.values.firstOrNull {
                        val portal = it.content as? CellContent.Portal
                        portal != null && portal.portalId == content.targetPortalId && (it.row != nextRow || it.col != nextCol)
                    }
                    if (targetPortalCell != null) {
                        currRow = targetPortalCell.row
                        currCol = targetPortalCell.col
                        path.add(currRow to currCol)
                    } else {
                        currRow = nextRow
                        currCol = nextCol
                    }
                }
            }
        }
    }

    /**
     * Executes the arrow escape move, returning the new board state and side effects (rotations, bomb explosions, etc.)
     */
    fun executeEscape(board: Board, row: Int, col: Int): MoveOutcome? {
        val escapeResult = checkEscape(board, row, col)
        if (!escapeResult.canEscape) return null

        val cell = board.getCell(row, col)
        val arrow = cell.content as? CellContent.Arrow ?: return null

        var workingGrid = board.grid.toMutableMap()
        // Remove escaping arrow
        workingGrid.remove(row to col)

        val destroyedBlockers = mutableListOf<Pair<Int, Int>>()
        val openedGates = mutableListOf<Pair<Int, Int>>()
        val rotatedArrows = mutableListOf<Pair<Int, Int>>()

        // Bomb effect: if the arrow was a bomb, destroy adjacent obstacles and ice
        if (arrow.isBomb) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val nr = row + dr
                    val nc = col + dc
                    val neighbor = workingGrid[nr to nc]
                    if (neighbor != null && neighbor.content is CellContent.Blocker) {
                        workingGrid.remove(nr to nc)
                        destroyedBlockers.add(nr to nc)
                    }
                }
            }
        }

        // Key effect: if arrow was a key, unlock all locked gates
        if (arrow.isKey) {
            workingGrid.entries.forEach { (pos, gridCell) ->
                if (gridCell.content is CellContent.Blocker && gridCell.content.type == BlockerType.LOCKED_GATE) {
                    workingGrid[pos] = gridCell.copy(content = CellContent.Blocker(BlockerType.LOCKED_GATE, isUnlocked = true))
                    openedGates.add(pos)
                }
            }
        }

        // Ice effect: Shatter adjacent ice blocks
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                val neighbor = workingGrid[nr to nc]
                if (neighbor != null && neighbor.content is CellContent.Blocker && neighbor.content.type == BlockerType.ICE) {
                    workingGrid.remove(nr to nc)
                    destroyedBlockers.add(nr to nc)
                }
            }
        }

        // Rotator effect: any rotator arrows in the puzzle rotate 90 degrees clockwise
        workingGrid.entries.forEach { (pos, gridCell) ->
            val content = gridCell.content
            if (content is CellContent.Arrow && content.isRotator) {
                val newArrow = content.copy(direction = content.direction.rotated90Clockwise())
                workingGrid[pos] = gridCell.copy(content = newArrow)
                rotatedArrows.add(pos)
            }
        }

        val newBoard = board.copy(grid = workingGrid)
        return MoveOutcome(
            newBoard = newBoard,
            escapedArrow = arrow,
            escapePath = escapeResult.path,
            destroyedBlockers = destroyedBlockers,
            openedGates = openedGates,
            rotatedArrows = rotatedArrows
        )
    }

    /**
     * Finds the next solvable arrow for hints.
     */
    fun findSolvableArrow(board: Board): Pair<Int, Int>? {
        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                val cell = board.getCell(r, c)
                if (cell.content is CellContent.Arrow) {
                    val escape = checkEscape(board, r, c)
                    if (escape.canEscape) {
                        return r to c
                    }
                }
            }
        }
        return null
    }

    /**
     * Checks if the entire puzzle can be solved from current state.
     */
    fun isSolvable(board: Board): Boolean {
        var currentBoard = board
        while (!currentBoard.isCleared()) {
            val move = findSolvableArrow(currentBoard) ?: return false
            val outcome = executeEscape(currentBoard, move.first, move.second) ?: return false
            currentBoard = outcome.newBoard
        }
        return true
    }

    /**
     * Procedural Solvable Puzzle Generator.
     * Back-traces from an empty board by placing arrows whose ray from edge to cell is clear,
     * ensuring 100% solvable puzzle every time.
     */
    fun generateSolvablePuzzle(
        rows: Int,
        cols: Int,
        arrowDensity: Float = 0.75f,
        includeDiagonals: Boolean = false,
        includeRotators: Boolean = false,
        includeBombs: Boolean = false,
        seed: Long = System.currentTimeMillis()
    ): Board {
        val rand = Random(seed)
        var board = Board(rows, cols, emptyMap())
        val totalCells = rows * cols
        val targetArrowCount = (totalCells * arrowDensity).toInt().coerceIn(3, totalCells - 1)

        val straightDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
        val allDirections = Direction.values().toList()
        val allowedDirections = if (includeDiagonals) allDirections else straightDirections

        val placedPositions = mutableListOf<Pair<Int, Int>>()
        val availableCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                availableCells.add(r to c)
            }
        }
        availableCells.shuffle(rand)

        // Generate reverse sequence
        var attempts = 0
        while (placedPositions.size < targetArrowCount && attempts < 200 && availableCells.isNotEmpty()) {
            attempts++
            val pos = availableCells.removeAt(0)
            val r = pos.first
            val c = pos.second

            // Try all possible directions to find one where current cell can escape to edge without hitting existing placed arrows
            val validDirs = allowedDirections.filter { dir ->
                // Check if ray towards edge is free of currently placed arrows in `board`
                var testR = r
                var testC = c
                var isClear = true
                while (true) {
                    testR += dir.dRow
                    testC += dir.dCol
                    if (testR !in 0 until rows || testC !in 0 until cols) break
                    if (board.getCell(testR, testC).content is CellContent.Arrow) {
                        isClear = false
                        break
                    }
                }
                isClear
            }

            if (validDirs.isNotEmpty()) {
                val chosenDir = validDirs.random(rand)
                val colorVariant = rand.nextInt(4)
                val isRotator = includeRotators && rand.nextFloat() < 0.15f
                val isBomb = includeBombs && rand.nextFloat() < 0.10f

                val arrow = CellContent.Arrow(
                    direction = chosenDir,
                    colorVariant = colorVariant,
                    isRotator = isRotator,
                    isBomb = isBomb
                )
                board = board.withCell(r, c, arrow)
                placedPositions.add(pos)
            } else {
                availableCells.add(pos) // put back
            }
        }

        // Verify solvability; if not solvable (e.g., due to rotators), regenerate simply
        if (!isSolvable(board) || board.remainingArrows() < 3) {
            // Fallback generation: basic guaranteed straight puzzle
            var fallbackBoard = Board(rows, cols, emptyMap())
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val dir = when {
                        r == 0 -> Direction.UP
                        r == rows - 1 -> Direction.DOWN
                        c == 0 -> Direction.LEFT
                        c == cols - 1 -> Direction.RIGHT
                        r < rows / 2 -> Direction.UP
                        else -> Direction.DOWN
                    }
                    fallbackBoard = fallbackBoard.withCell(r, c, CellContent.Arrow(dir, colorVariant = (r + c) % 4))
                }
            }
            return fallbackBoard
        }

        return board
    }
}
