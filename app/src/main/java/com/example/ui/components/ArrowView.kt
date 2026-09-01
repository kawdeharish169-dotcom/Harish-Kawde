package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ArrowStyle
import com.example.model.BlockerType
import com.example.model.CellContent
import com.example.model.Direction
import com.example.ui.theme.LocalGameColors

@Composable
fun ArrowCellView(
    content: CellContent,
    isHinted: Boolean = false,
    isSelectedForTool: Boolean = false,
    arrowStyle: ArrowStyle = ArrowStyle.MODERN_TRIANGLE,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "cell_animations")

    val hintPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_pulse"
    )

    val rotatorSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotator_spin"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.cellBackground,
                        colors.gridBackground
                    )
                )
            )
            .border(
                width = if (isHinted) 2.5.dp else if (isSelectedForTool) 2.dp else 1.dp,
                color = if (isHinted) colors.hintGlow.copy(alpha = hintPulse)
                else if (isSelectedForTool) colors.secondary
                else colors.cardBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = colors.primary),
                onClick = onClick
            )
            .testTag("cell_item"),
        contentAlignment = Alignment.Center
    ) {
        when (content) {
            is CellContent.Arrow -> {
                val arrowColor = colors.arrowColors[content.colorVariant % colors.arrowColors.size]

                // Rotator indicator ring
                if (content.isRotator) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize(0.92f)
                            .rotate(rotatorSpin)
                    ) {
                        drawCircle(
                            color = colors.tertiary.copy(alpha = 0.4f),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    }
                }

                // Render the Arrow Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize(0.78f)
                        .rotate(content.direction.angleDegrees)
                ) {
                    drawStyledArrow(
                        arrowStyle = arrowStyle,
                        arrowColor = arrowColor,
                        isRotator = content.isRotator,
                        isBomb = content.isBomb
                    )
                }

                // Bomb badge
                if (content.isBomb) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .background(Color(0xFFFF3300), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Bomb Arrow",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.8f).align(Alignment.Center)
                        )
                    }
                }

                // Rotator mini icon
                if (content.isRotator) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .background(colors.tertiary.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Rotator Arrow",
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize(0.85f).align(Alignment.Center)
                        )
                    }
                }
            }

            is CellContent.Blocker -> {
                when (content.type) {
                    BlockerType.WALL -> {
                        Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
                            // Draw metallic brick wall pattern
                            drawRoundRect(
                                color = colors.wallColor,
                                size = size,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                            // Brick grid lines
                            drawLine(
                                color = Color.Black.copy(alpha = 0.3f),
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = Color.Black.copy(alpha = 0.3f),
                                start = Offset(size.width / 2, 0f),
                                end = Offset(size.width / 2, size.height / 2),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }

                    BlockerType.ICE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.85f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.iceColor.copy(alpha = 0.65f))
                                .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = "Ice Blocker",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    BlockerType.LOCKED_GATE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.85f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (content.isUnlocked) Color(0x3300FF88) else Color(0x66FF0055))
                                .border(
                                    1.5.dp,
                                    if (content.isUnlocked) Color(0xFF00FF88) else Color(0xFFFF0055),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Gate",
                                tint = if (content.isUnlocked) Color(0xFF00FF88) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            is CellContent.Portal -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .rotate(rotatorSpin)
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.portalColor, colors.portalColor.copy(alpha = 0.1f))
                        ),
                        radius = size.minDimension / 2.2f
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = size.minDimension / 5f
                    )
                }
            }

            is CellContent.Empty -> {
                // Subtle empty cell dot
                Canvas(modifier = Modifier.size(6.dp)) {
                    drawCircle(
                        color = colors.cardBorder.copy(alpha = 0.35f),
                        radius = 2.dp.toPx()
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStyledArrow(
    arrowStyle: ArrowStyle,
    arrowColor: Color,
    isRotator: Boolean,
    isBomb: Boolean
) {
    val w = size.width
    val h = size.height
    val centerX = w / 2f
    val centerY = h / 2f

    when (arrowStyle) {
        ArrowStyle.MODERN_TRIANGLE -> {
            // Sleek aerodynamic arrow with gradient tip and tail notch
            val path = Path().apply {
                // Tip at top center
                moveTo(centerX, h * 0.05f)
                // Right corner of arrowhead
                lineTo(w * 0.92f, h * 0.62f)
                // Inner right inset
                lineTo(centerX + w * 0.22f, h * 0.52f)
                // Stem bottom right
                lineTo(centerX + w * 0.22f, h * 0.92f)
                // Tail notch
                lineTo(centerX, h * 0.80f)
                // Stem bottom left
                lineTo(centerX - w * 0.22f, h * 0.92f)
                // Inner left inset
                lineTo(centerX - w * 0.22f, h * 0.52f)
                // Left corner of arrowhead
                lineTo(w * 0.08f, h * 0.62f)
                close()
            }

            // Outer glow / body
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        arrowColor,
                        arrowColor.copy(alpha = 0.85f)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Inner highlight outline
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.6f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        ArrowStyle.CHEVRON_DART -> {
            val path = Path().apply {
                moveTo(centerX, h * 0.08f)
                lineTo(w * 0.92f, h * 0.55f)
                lineTo(centerX, h * 0.35f)
                lineTo(w * 0.08f, h * 0.55f)
                close()
            }

            val path2 = Path().apply {
                moveTo(centerX, h * 0.45f)
                lineTo(w * 0.92f, h * 0.92f)
                lineTo(centerX, h * 0.72f)
                lineTo(w * 0.08f, h * 0.92f)
                close()
            }

            drawPath(path = path, brush = Brush.verticalGradient(listOf(Color.White, arrowColor)))
            drawPath(path = path2, color = arrowColor.copy(alpha = 0.75f))
        }

        ArrowStyle.LASER_GLOW -> {
            // Neon Laser line with bright triangular head
            val lineStroke = 4.dp.toPx()
            drawLine(
                brush = Brush.verticalGradient(listOf(arrowColor, arrowColor.copy(alpha = 0.4f))),
                start = Offset(centerX, h * 0.35f),
                end = Offset(centerX, h * 0.92f),
                strokeWidth = lineStroke,
                cap = StrokeCap.Round
            )

            val tipPath = Path().apply {
                moveTo(centerX, h * 0.05f)
                lineTo(w * 0.88f, h * 0.42f)
                lineTo(centerX, h * 0.30f)
                lineTo(w * 0.12f, h * 0.42f)
                close()
            }
            drawPath(path = tipPath, color = arrowColor)
            drawPath(path = tipPath, color = Color.White, style = Stroke(width = 1.5.dp.toPx()))
        }

        ArrowStyle.PAPER_PLANE -> {
            val planePath = Path().apply {
                moveTo(centerX, h * 0.05f)
                lineTo(w * 0.95f, h * 0.92f)
                lineTo(centerX, h * 0.70f)
                lineTo(w * 0.05f, h * 0.92f)
                close()
            }
            drawPath(planePath, brush = Brush.verticalGradient(listOf(Color.White, arrowColor)))
            // Center fold line
            drawLine(
                color = Color.Black.copy(alpha = 0.25f),
                start = Offset(centerX, h * 0.05f),
                end = Offset(centerX, h * 0.70f),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
