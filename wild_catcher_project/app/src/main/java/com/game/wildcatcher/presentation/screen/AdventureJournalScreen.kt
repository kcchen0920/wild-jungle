package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.AnimalEntity
import com.game.wildcatcher.data.model.ConservationGrade
import com.game.wildcatcher.data.model.HabitatType
import com.game.wildcatcher.presentation.component.DaveNotebookFrame
import com.game.wildcatcher.presentation.component.PbrAnimalRenderer
import com.game.wildcatcher.presentation.theme.*

@Composable
fun AdventureJournalScreen(
    animals: List<AnimalEntity>,
    onPlayAnimalSound: (String) -> Unit,
    onSpeakGuide: (AnimalEntity) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedHabitat by remember { mutableStateOf<HabitatType?>(null) }
    var selectedAnimal by remember { mutableStateOf<AnimalEntity?>(animals.firstOrNull()) }

    val filteredList = remember(animals, selectedHabitat) {
        if (selectedHabitat == null) animals
        else animals.filter { it.habitat == selectedHabitat }
    }

    DaveNotebookFrame(
        title = "📖 野生生態冒險筆記本",
        subtitle = "共收錄 ${animals.size} 種寫實野生動物物種  |  已發現：${animals.count { it.isCaptured }} 種",
        actions = {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LeatherDark.copy(alpha = 0.8f))
            ) {
                Text("✕", color = VintageBrass, fontWeight = FontWeight.Bold)
            }
        },
        modifier = modifier
    ) {
        // Habitat Filter Ribbon
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedHabitat == null,
                    onClick = { selectedHabitat = null },
                    label = { Text("全部 (${animals.size})") }
                )
            }
            item {
                FilterChip(
                    selected = selectedHabitat == HabitatType.TROPICAL_RAINFOREST,
                    onClick = { selectedHabitat = HabitatType.TROPICAL_RAINFOREST },
                    label = { Text("熱帶雨林") }
                )
            }
            item {
                FilterChip(
                    selected = selectedHabitat == HabitatType.ALPINE_CONIFER,
                    onClick = { selectedHabitat = HabitatType.ALPINE_CONIFER },
                    label = { Text("高山針葉") }
                )
            }
            item {
                FilterChip(
                    selected = selectedHabitat == HabitatType.CANOPY_AND_SKY,
                    onClick = { selectedHabitat = HabitatType.CANOPY_AND_SKY },
                    label = { Text("天空猛禽") }
                )
            }
            item {
                FilterChip(
                    selected = selectedHabitat == HabitatType.RIVER_AND_STREAM,
                    onClick = { selectedHabitat = HabitatType.RIVER_AND_STREAM },
                    label = { Text("溪流活水") }
                )
            }
            item {
                FilterChip(
                    selected = selectedHabitat == HabitatType.BAMBOO_GROVE,
                    onClick = { selectedHabitat = HabitatType.BAMBOO_GROVE },
                    label = { Text("淺山竹林") }
                )
            }
        }

        // Split Page Tablet Layout: Left List (35%) & Right Detailed Showcase (65%)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Index Column
            LazyColumn(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ParchmentCard)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredList) { animal ->
                    val isSelected = animal.id == selectedAnimal?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) VintageBrass.copy(alpha = 0.25f) else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) VintageBrass else ParchmentBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedAnimal = animal }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Conservation traffic light indicator dot
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (animal.grade) {
                                            ConservationGrade.GREEN -> BambooLight
                                            ConservationGrade.YELLOW -> SunsetAmber
                                            ConservationGrade.RED -> DangerCoral
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = animal.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = LeatherDark
                                )
                                Text(
                                    text = animal.scientificName,
                                    fontSize = 10.sp,
                                    color = InkGrey,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }

                        if (animal.isCaptured) {
                            Text("✓ 已捕獲", fontSize = 10.sp, color = BambooGreen, fontWeight = FontWeight.Bold)
                        } else {
                            Text("待探索", fontSize = 10.sp, color = InkGrey)
                        }
                    }
                }
            }

            // Right Large Page: Animal Specimen Details & 3D PBR Model
            Box(
                modifier = Modifier
                    .weight(0.62f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ParchmentCard)
                    .border(1.dp, ParchmentBorder, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                if (selectedAnimal != null) {
                    val curr = selectedAnimal!!
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Title & Conservation Banner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                if (curr.getFullBopomofo().isNotEmpty()) {
                                    Text(
                                        text = curr.getFullBopomofo(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunsetAmber
                                    )
                                }
                                Text(
                                    text = curr.name,
                                    style = WildTypography.headlineMedium.copy(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LeatherDark
                                    )
                                )
                                Text(
                                    text = "學名: ${curr.scientificName}",
                                    style = WildTypography.bodyMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        color = InkGrey
                                    )
                                )
                            }

                            // Conservation Badge
                            Surface(
                                color = when (curr.grade) {
                                    ConservationGrade.GREEN -> Color(0xFFE8F5E9)
                                    ConservationGrade.YELLOW -> Color(0xFFFFF8E1)
                                    ConservationGrade.RED -> Color(0xFFFFEBEE)
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (curr.grade) {
                                        ConservationGrade.GREEN -> BambooGreen
                                        ConservationGrade.YELLOW -> SunsetAmber
                                        ConservationGrade.RED -> DangerCoral
                                    }
                                )
                            ) {
                                Text(
                                    text = curr.gradeLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (curr.grade) {
                                        ConservationGrade.GREEN -> BambooGreen
                                        ConservationGrade.YELLOW -> Color(0xFFE65100)
                                        ConservationGrade.RED -> DangerCoral
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Middle: PBR 3D Realistic Texture View & Sound Trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2C221E))
                                    .border(1.5.dp, VintageBrass, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                PbrAnimalRenderer(
                                    animal = curr,
                                    lightAngleDeg = 60f,
                                    modifier = Modifier.size(130.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("📍 棲息生境：${curr.habitatName}", fontSize = 12.sp, color = InkBlack)
                                Text("☀️ 活動週期：${when (curr.activityCycle) {
                                    com.game.wildcatcher.data.model.ActivityCycle.DIURNAL -> "日行性 (白天出沒)"
                                    com.game.wildcatcher.data.model.ActivityCycle.NOCTURNAL -> "夜行性 (夜間現身)"
                                    com.game.wildcatcher.data.model.ActivityCycle.CREPUSCULAR -> "晨昏出沒 (清晨與暮色)"
                                }}", fontSize = 12.sp, color = InkBlack)
                                Text("💰 庇護所產值：${curr.coinYieldPerHour} 竹幣 / 小時", fontSize = 12.sp, color = SunsetAmber, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Play Sound Button
                                    Button(
                                        onClick = { onPlayAnimalSound(curr.callSoundName) },
                                        colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("🔊 真實叫聲", fontSize = 11.sp)
                                    }

                                    // TTS Voice Guide
                                    Button(
                                        onClick = { onSpeakGuide(curr) },
                                        colors = ButtonDefaults.buttonColors(containerColor = VintageBrass),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("🗣️ 波波語音導覽", fontSize = 11.sp, color = LeatherDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Bottom: Fun Fact & Ecological Role Notes
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ParchmentBg)
                                .border(1.dp, ParchmentBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 生物特性：${curr.funFact}",
                                fontSize = 12.sp,
                                color = InkBlack,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🌱 生態價值：${curr.ecologyNote}",
                                fontSize = 11.sp,
                                color = InkGrey,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
