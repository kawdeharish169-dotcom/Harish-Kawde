package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.ActiveTool
import com.example.model.ArrowStyle
import com.example.model.Board
import com.example.model.BumpAnimation
import com.example.model.CellContent
import com.example.model.FlyingArrowAnimation
import com.example.model.Particle
import com.example.ui.theme.LocalGameColors
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GameBoardView(
    board: Board,
    hintedCell: Pair<Int, Int>?,
    activeTool: ActiveTool,
    arrowStyle: ArrowStyle,
    flyingArrows: List<FlyingArrowAnimation>,
    bumpAnimations: List<BumpAnimation>,
    particles: List<Particle>,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = minOf(maxWidth, maxHeight.takeIf { it > 0.dp } ?: maxWidth)
        val cellWidth = boardSize / board.cols
        val cellHeight = boardSize / board.rows

        Box(
            modifier = Modifier
                .size(boardSize)
                .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = colors.primary.copy(alpha = 0.2f))
                .clip(RoundedCornerShape(24.dp))
                .background(colors.gridBackground)
                .border(2.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                .padding(8.dp)
                .testTag("game_board_matrix")
        ) {
            // Main Grid
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until board.rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (c in 0 until board.cols) {
                            val cell = board.getCell(r, c)
                            val isHinted = (hintedCell?.first == r && hintedCell?.second == c)
                            val isSelectedForTool = (activeTool != ActiveTool.NONE && cell.content !is CellContent.Empty)

                            // Check if this cell is currently bumping
                            val bump = bumpAnimations.find { it.row == r && it.col == c }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (bump != null) {
                                    BumpAnimatedCell(
                                        bump = bump,
                                        content = cell.content,
                                        isHinted = isHinted,
                                        isSelectedForTool = isSelectedForTool,
                                        arrowStyle = arrowStyle,
                                        onClick = { onCellClick(r, c) }
                                    )
                                } else {
                                    ArrowCellView(
                                        content = cell.content,
                                        isHinted = isHinted,
                                        isSelectedForTool = isSelectedForTool,
                                        arrowStyle = arrowStyle,
                                        onClick = { onCellClick(r, c) },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Flying Arrows Overlay
            flyingArrows.forEach { flying ->
                FlyingArrowOverlay(
                    flying = flying,
                    boardRows = board.rows,
                    boardCols = board.cols,
                    arrowStyle = arrowStyle
                )
            }

            // Canvas Particles Overlay
            if (particles.isNotEmpty()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scaleX = size.width / board.cols
                    val scaleY = size.height / board.rows

                    particles.forEach { p ->
                        val px = (p.x + 0.5f) * scaleX
                        val py = (p.y + 0.5f) * scaleY
                        drawCircle(
                            color = Color(p.color).copy(alpha = p.alpha),
                            radius = p.size,
                            center = Offset(px, py)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BumpAnimatedCell(
    bump: BumpAnimation,
    content: CellContent,
    isHinted: Boolean,
    isSelectedForTool: Boolean,
    arrowStyle: ArrowStyle,
    onClick: () -> Unit
) {
    val animProgress = remember(bump.startTimeMs) { Animatable(0f) }

    LaunchedEffect(bump.startTimeMs) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = bump.durationMs.toInt())
        )
    }

    val p = animProgress.value
    // Nudge forward, then recoil wobble
    val amplitude = 18f * (1f - p) * sin(p * Math.PI * 4).toFloat()
    val offsetX = amplitude * bump.direction.dCol
    val offsetY = amplitude * bump.direction.dRow

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
    ) {
        ArrowCellView(
            content = content,
            isHinted = isHinted,
            isSelectedForTool = isSelectedForTool,
            arrowStyle = arrowStyle,
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FlyingArrowOverlay(
    flying: FlyingArrowAnimation,
    boardRows: Int,
    boardCols: Int,
    arrowStyle: ArrowStyle
) {
    val colors = LocalGameColors.current
    val animProgress = remember(flying.startTimeMs) { Animatable(0f) }

    LaunchedEffect(flying.startTimeMs) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = flying.durationMs.toInt())
        )
    }

    val progress = animProgress.value
    val maxTravel = maxOf(boardRows, boardCols) * 1.5f
    val travelDist = progress * maxTravel

    val currentFractionRow = flying.startRow + flying.direction.dRow * travelDist
    val currentFractionCol = flying.startCol + flying.direction.dCol * travelDist

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cellW = maxWidth / boardCols
        val cellH = maxHeight / boardRows

        val leftOffset = cellW * currentFractionCol
        val topOffset = cellH * currentFractionRow
        val alpha = (1f - progress * 0.4f).coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .size(cellW, cellH)
                .offset(x = leftOffset, y = topOffset)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            ArrowCellView(
                content = flying.arrowContent,
                isHinted = false,
                arrowStyle = arrowStyle,
                onClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
