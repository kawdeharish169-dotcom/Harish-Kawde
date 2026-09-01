package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelProgressEntity
import com.example.engine.CampaignLevels
import com.example.ui.theme.LocalGameColors

@Composable
fun LevelSelectScreen(
    selectedChapterId: Int,
    levelProgressList: List<LevelProgressEntity>,
    onSelectChapter: (Int) -> Unit,
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val chapters = CampaignLevels.chapters
    val currentChapter = chapters.find { it.id == selectedChapterId } ?: chapters.first()

    val progressMap = levelProgressList.associateBy { it.levelId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(top = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, CircleShape)
                    .testTag("btn_levels_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CAMPAIGN STAGES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = colors.onBackground
            )
        }

        // Chapter Tabs Scrollable
        ScrollableTabRow(
            selectedTabIndex = selectedChapterId - 1,
            containerColor = Color.Transparent,
            contentColor = colors.primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedChapterId - 1 in tabPositions.indices) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedChapterId - 1]),
                        color = colors.primary,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            chapters.forEach { chapter ->
                val isSelected = chapter.id == selectedChapterId
                Tab(
                    selected = isSelected,
                    onClick = { onSelectChapter(chapter.id) },
                    text = {
                        Text(
                            text = "Ch ${chapter.id}: ${chapter.title}",
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) colors.primary else Color.Gray
                        )
                    }
                )
            }
        }

        // Chapter Info Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = currentChapter.title.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentChapter.description,
                    fontSize = 12.sp,
                    color = colors.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        // 10 Levels Grid (e.g. 1..10, 11..20, etc.)
        val levelRange = (currentChapter.levelStartId..currentChapter.levelEndId).toList()

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(levelRange) { levelId ->
                val progress = progressMap[levelId]
                val isUnlocked = progress?.isUnlocked == true || levelId == 1
                val stars = progress?.stars ?: 0

                LevelGridCard(
                    levelId = levelId,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    onClick = { if (isUnlocked) onSelectLevel(levelId) }
                )
            }
        }
    }
}

@Composable
fun LevelGridCard(
    levelId: Int,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val colors = LocalGameColors.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) colors.surfaceVariant else colors.surface.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .aspectRatio(0.85f)
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                color = if (stars > 0) colors.primary else if (isUnlocked) colors.cardBorder else colors.cardBorder.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_card_$levelId")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = "$levelId",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (stars > 0) colors.primary else colors.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stars row
                Row(horizontalArrangement = Arrangement.Center) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= stars) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.35f),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
