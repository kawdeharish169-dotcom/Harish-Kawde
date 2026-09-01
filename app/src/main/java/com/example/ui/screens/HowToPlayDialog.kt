package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGameColors

@Composable
fun HowToPlayScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, CircleShape)
                    .testTag("btn_guide_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "HOW TO PLAY",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guide Cards
        GuideCard(
            number = "1",
            icon = Icons.Default.ArrowForward,
            iconColor = colors.primary,
            title = "Escape The Arrows",
            description = "Tap on an arrow to launch it. If its flight path in the pointing direction is completely clear to the board edge, it will fly off and clear space!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        GuideCard(
            number = "2",
            icon = Icons.Default.Block,
            iconColor = Color(0xFFFF5577),
            title = "Watch For Obstructions",
            description = "If an arrow's path is blocked by another arrow or wall, it will bounce back and reset your combo. Solve outer arrows first to untangle knots!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        GuideCard(
            number = "3",
            icon = Icons.Default.Sync,
            iconColor = colors.tertiary,
            title = "Rotator Arrows",
            description = "Rotator arrows turn 90° clockwise with each valid move. Plan your sequence ahead so they rotate into clear escape trajectories!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        GuideCard(
            number = "4",
            icon = Icons.Default.Warning,
            iconColor = Color(0xFFFF3300),
            title = "Bombs & Ice Shatters",
            description = "Bomb arrows destroy adjacent blockers when escaping. Clearing arrows adjacent to frosty Ice blocks will shatter them instantly!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        GuideCard(
            number = "5",
            icon = Icons.Default.ElectricBolt,
            iconColor = colors.secondary,
            title = "Chain Combo Streaks",
            description = "Clearing arrows consecutively without bumping builds up a massive Combo Multiplier (up to Frenzy x5+!) for huge bonus points."
        )

        Spacer(modifier = Modifier.height(12.dp))

        GuideCard(
            number = "6",
            icon = Icons.Default.Lightbulb,
            iconColor = Color(0xFFFFD700),
            title = "Power-Ups & Boosters",
            description = "Stuck? Use the Hint power-up to highlight the next unblocked arrow, or use Rotate/Bomb tools to turn or zap blocking tiles directly!"
        )
    }
}

@Composable
fun GuideCard(
    number: String,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    val colors = LocalGameColors.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.5.dp, iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$number. ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = iconColor
                    )
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}
