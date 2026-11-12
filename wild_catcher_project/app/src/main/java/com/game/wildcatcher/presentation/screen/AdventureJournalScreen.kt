package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.AnimalEntity
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

    val habitatIcon: (HabitatType) -> String = { habitat ->
        when (habitat) {
            HabitatType.TROPICAL_RAINFOREST -> "🌴"
            HabitatType.ALPINE_CONIFER -> "🌲"
            HabitatType.CANOPY_AND_SKY -> "🦅"
            HabitatType.RIVER_AND_STREAM -> "💧"
            HabitatType.BAMBOO_GROVE -> "🎋"
        }
    }

    DaveNotebookFrame(
        title = "📖 100種動物有聲貼紙簿",
        subtitle = "🌟 已收集 ${animals.count { it.isCaptured }} 張閃亮貼紙  |  點一下貼紙自動聽波波講故事喔！",
        actions = {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = LeatherDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("🏠 回首頁", color = BrassHighlight, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
        },
        modifier = modifier
    ) {
        // Big Icon Habitat Filter Ribbon for 4-Year-Olds
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val filters = listOf(
                null to "🌍 全部 (${animals.size})",
                HabitatType.TROPICAL_RAINFOREST to "🌴 雨林",
                HabitatType.ALPINE_CONIFER to "🌲 高山",
                HabitatType.CANOPY_AND_SKY to "🦅 天空",
                HabitatType.RIVER_AND_STREAM to "💧 溪流",
                HabitatType.BAMBOO_GROVE to "🎋 竹林"
            )
            items(filters) { (hab, label) ->
                val isSelected = selectedHabitat == hab
                Button(
                    onClick = { selectedHabitat = hab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) BambooGreen else ParchmentCard
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(42.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color.White else LeatherDark
                    )
                }
            }
        }

        // Split Page Tablet Layout: Left Sticker Cards (40%) & Right Audio Storybook Page (60%)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Sticker Column (Auto-Speaks when Tapped!)
            LazyColumn(
                modifier = Modifier
                    .weight(0.40f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ParchmentCard)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { animal ->
                    val isSelected = animal.id == selectedAnimal?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 58.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color(0xFFFFF8E1) else Color(0xFFFFFDF7)
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) SunsetAmber else ParchmentBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedAnimal = animal
                                onSpeakGuide(animal)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = habitatIcon(animal.habitat),
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = animal.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LeatherDark
                                )
                                if (animal.getFullBopomofo().isNotEmpty()) {
                                    Text(
                                        text = animal.getFullBopomofo(),
                                        fontSize = 12.sp,
                                        color = SunsetAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (animal.isCaptured) {
                            Text("🌟 貼紙", fontSize = 13.sp, color = BambooGreen, fontWeight = FontWeight.ExtraBold)
                        } else {
                            Text("🔊 點聽", fontSize = 13.sp, color = InkGrey, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Right Large Page: Big 3D Animal + Giant Voice Story Buttons
            Box(
                modifier = Modifier
                    .weight(0.60f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ParchmentCard)
                    .border(2.dp, VintageBrass, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                if (selectedAnimal != null) {
                    val curr = selectedAnimal!!
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Title & Bopomofo Banner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                if (curr.getFullBopomofo().isNotEmpty()) {
                                    Text(
                                        text = "注音：${curr.getFullBopomofo()}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SunsetAmber
                                    )
                                }
                                Text(
                                    text = "${habitatIcon(curr.habitat)} ${curr.name}",
                                    style = WildTypography.headlineMedium.copy(
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = LeatherDark
                                    )
                                )
                            }

                            Surface(
                                color = if (curr.isCaptured) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(2.dp, if (curr.isCaptured) BambooGreen else SunsetAmber)
                            ) {
                                Text(
                                    text = if (curr.isCaptured) "🌟 好朋友貼紙已收集！" else "🔍 住在${curr.habitatName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (curr.isCaptured) BambooGreen else Color(0xFFE65100),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Middle: Giant Tappable 3D Animal Preview + 2 Huge Audio Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF23382C))
                                    .border(2.5.dp, VintageBrass, RoundedCornerShape(18.dp))
                                    .clickable {
                                        onPlayAnimalSound(curr.callSoundName)
                                        onSpeakGuide(curr)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                PbrAnimalRenderer(
                                    animal = curr,
                                    lightAngleDeg = 60f,
                                    modifier = Modifier.size(140.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "🏞️ 家在：${curr.habitatName}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = InkBlack
                                )
                                Text(
                                    text = when (curr.activityCycle) {
                                        com.game.wildcatcher.data.model.ActivityCycle.DIURNAL -> "☀️ 喜歡大太陽出來玩"
                                        com.game.wildcatcher.data.model.ActivityCycle.NOCTURNAL -> "🌙 喜歡晚上看月亮出來玩"
                                        com.game.wildcatcher.data.model.ActivityCycle.CREPUSCULAR -> "🌅 喜歡太陽起床時出來玩"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BambooGreen
                                )

                                // Two Giant Audio Buttons for 4-Year-Olds
                                Button(
                                    onClick = { onSpeakGuide(curr) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                ) {
                                    Text(
                                        text = "🗣️ 聽波波講故事 🔊",
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Button(
                                    onClick = {
                                        onPlayAnimalSound(curr.callSoundName)
                                        onSpeakGuide(curr)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = "🐾 聽小動物打招呼",
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        // Bottom: Kid-Friendly Story Box (Tappable to Read Aloud)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFF9C4))
                                .border(2.dp, SunsetAmber, RoundedCornerShape(14.dp))
                                .clickable { onSpeakGuide(curr) }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🔊 點聽小秘密：${curr.funFact}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkBlack,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
