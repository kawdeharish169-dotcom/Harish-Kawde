package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveTool
import com.example.ui.theme.LocalGameColors

@Composable
fun PowerUpBar(
    hintsCount: Int,
    rotatesCount: Int,
    bombsCount: Int,
    coins: Int,
    activeTool: ActiveTool,
    canUndo: Boolean,
    onHintClick: () -> Unit,
    onRotateClick: () -> Unit,
    onBombClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo Button
        PowerUpButton(
            icon = Icons.Default.Undo,
            label = "Undo",
            count = null,
            isActive = false,
            isEnabled = canUndo,
            onClick = onUndoClick,
            testTag = "btn_undo"
        )

        // Hint Button
        PowerUpButton(
            icon = Icons.Default.Lightbulb,
            label = "Hint",
            count = hintsCount,
            cost = 30,
            isActive = false,
            isEnabled = true,
            onClick = onHintClick,
            testTag = "btn_hint"
        )

        // Rotate Tool Button
        PowerUpButton(
            icon = Icons.Default.RotateRight,
            label = "Rotate",
            count = rotatesCount,
            cost = 40,
            isActive = activeTool == ActiveTool.ROTATE_TOOL,
            isEnabled = true,
            onClick = onRotateClick,
            testTag = "btn_rotate"
        )

        // Bomb Zap Tool Button
        PowerUpButton(
            icon = Icons.Default.Warning,
            label = "Bomb",
            count = bombsCount,
            cost = 50,
            isActive = activeTool == ActiveTool.BOMB_TOOL,
            isEnabled = true,
            onClick = onBombClick,
            testTag = "btn_bomb"
        )

        // Restart Button
        PowerUpButton(
            icon = Icons.Default.Refresh,
            label = "Restart",
            count = null,
            isActive = false,
            isEnabled = true,
            onClick = onRestartClick,
            testTag = "btn_restart"
        )
    }
}

@Composable
fun PowerUpButton(
    icon: ImageVector,
    label: String,
    count: Int?,
    cost: Int? = null,
    isActive: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val colors = LocalGameColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isActive) Brush.linearGradient(listOf(colors.secondary, colors.primary))
                    else Brush.verticalGradient(listOf(colors.surfaceVariant, colors.surface))
                )
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) colors.primary else colors.cardBorder.copy(alpha = if (isEnabled) 0.8f else 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    enabled = isEnabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = onClick
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = when {
                    isActive -> Color.White
                    isEnabled -> colors.primary
                    else -> colors.onSurface.copy(alpha = 0.3f)
                },
                modifier = Modifier.size(24.dp)
            )

            // Badge for remaining count / cost
            if (count != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(if (count > 0) colors.tertiary else colors.surface)
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (count > 0) "$count" else "${cost}c",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (count > 0) Color.Black else colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isEnabled) colors.onSurface.copy(alpha = 0.8f) else colors.onSurface.copy(alpha = 0.3f)
        )
    }
}
