package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowStyle
import com.example.model.Board
import com.example.model.CellContent
import com.example.ui.components.ArrowCellView
import com.example.ui.theme.LocalGameColors

data class PaletteTool(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val tintColor: Color
)

@Composable
fun SandboxScreen(
    rows: Int,
    cols: Int,
    board: Board,
    selectedTool: String,
    onSelectGridSize: (Int, Int) -> Unit,
    onSelectTool: (String) -> Unit,
    onCellClick: (Int, Int) -> Unit,
    onTestLevel: () -> Unit,
    onSaveLevel: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var levelTitleInput by remember { mutableStateOf("") }

    val paletteTools = listOf(
        PaletteTool("ARROW_UP", "Up", Icons.Default.ArrowUpward, colors.arrowColors[0]),
        PaletteTool("ARROW_DOWN", "Down", Icons.Default.ArrowDownward, colors.arrowColors[1]),
        PaletteTool("ARROW_LEFT", "Left", Icons.AutoMirrored.Filled.ArrowBack, colors.arrowColors[2]),
        PaletteTool("ARROW_RIGHT", "Right", Icons.Default.ArrowForward, colors.arrowColors[3]),
        PaletteTool("ROTATOR", "Rotator", Icons.Default.Sync, colors.tertiary),
        PaletteTool("BOMB", "Bomb", Icons.Default.Warning, Color(0xFFFF3300)),
        PaletteTool("WALL", "Wall", Icons.Default.Close, colors.wallColor),
        PaletteTool("ICE", "Ice", Icons.Default.AcUnit, colors.iceColor),
        PaletteTool("CLEAR", "Erase", Icons.Default.Close, Color.Gray)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, CircleShape)
                        .testTag("btn_sandbox_back")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "LEVEL EDITOR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = colors.onBackground
                )
            }

            IconButton(
                onClick = { showSaveDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, CircleShape)
                    .testTag("btn_sandbox_save")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save", tint = colors.primary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid Size Picker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Size:", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
            listOf(3 to 3, 4 to 4, 5 to 5, 6 to 6).forEach { (r, c) ->
                val isSelected = (rows == r && cols == c)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) colors.primary else colors.surface)
                        .border(1.dp, if (isSelected) colors.primary else colors.cardBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectGridSize(r, c) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${r}x${c}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else colors.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Palette Selector Horizontal Scroll
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(paletteTools) { tool ->
                val isSelected = (selectedTool == tool.id)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) colors.primary.copy(alpha = 0.25f) else colors.surface)
                        .border(
                            1.5.dp,
                            if (isSelected) colors.primary else colors.cardBorder.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectTool(tool.id) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.label,
                            tint = tool.tintColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tool.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.primary else colors.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Editor Canvas Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.gridBackground)
                    .border(2.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until rows) {
                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            for (c in 0 until cols) {
                                val cell = board.getCell(r, c)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ArrowCellView(
                                        content = cell.content,
                                        isHinted = false,
                                        arrowStyle = ArrowStyle.MODERN_TRIANGLE,
                                        onClick = { onCellClick(r, c) },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Test Play Button
        Button(
            onClick = onTestLevel,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_sandbox_test_play"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Test", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TEST PLAY LEVEL (${board.totalArrows()} Arrows)",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Custom Puzzle", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = levelTitleInput,
                    onValueChange = { levelTitleInput = it },
                    label = { Text("Puzzle Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (levelTitleInput.isNotBlank()) {
                        onSaveLevel(levelTitleInput)
                        showSaveDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
