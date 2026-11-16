package com.game.wildcatcher.presentation.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.AnimalEntity
import com.game.wildcatcher.data.model.ShelterZone
import com.game.wildcatcher.domain.shelter.ShelterRevenueReport
import com.game.wildcatcher.presentation.component.DaveNotebookFrame
import com.game.wildcatcher.presentation.theme.*

@Composable
fun ShelterManagementScreen(
    zones: List<ShelterZone>,
    shelteredAnimals: List<AnimalEntity>,
    revenueReport: ShelterRevenueReport,
    playerCoins: Long,
    onCollectRevenue: (Long) -> Unit,
    onUnlockZone: (String, Long) -> Unit,
    onBack: () -> Unit,
    onSpeakVoice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Track interactive feeding/petting animations per zone for 4yo tactile joy
    var ZoneHeartCounts by remember { mutableStateOf(mapOf<String, Int>()) }

    DaveNotebookFrame(
        title = "🏡 小動物好朋友遊樂園",
        subtitle = "💖 住著 ${revenueReport.shelteredAnimalsCount} 隻動物好朋友  |  快樂竹幣：$playerCoins 🎋",
        actions = {
            Button(
                onClick = {
                    onCollectRevenue(50L)
                    onSpeakVoice("哇！小動物們吃到香甜的紅蘋果，送給你50個快樂竹幣喔！")
                },
                colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("🍎 全園餵蘋果 (+50 🎋)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onCollectRevenue(revenueReport.totalHourlyRevenue)
                    onSpeakVoice("太棒了！收到小動物送的快樂竹幣禮物囉！")
                },
                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("🎁 領取竹幣 (+${revenueReport.totalHourlyRevenue})", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = LeatherDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("🏠 回首頁", color = BrassHighlight, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
        },
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(zones) { zone ->
                val animalsInZone = shelteredAnimals.filter { it.habitat == zone.habitatType }
                val tapCount = ZoneHeartCounts[zone.zoneId] ?: 0
                val cardBounce by animateFloatAsState(
                    targetValue = if (tapCount % 2 == 1) 1.03f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "zoneBounce"
                )

                val zoneIcon = when (zone.zoneId) {
                    "zone_rainforest" -> "🌴🦜"
                    "zone_alpine" -> "🌲🐻"
                    "zone_sky" -> "🦅☁️"
                    else -> "💧🦦"
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (zone.isUnlocked) Color(0xFFFFFDF7) else Color(0xFFE0DACB)
                    ),
                    border = BorderStroke(
                        width = 2.5.dp,
                        color = if (zone.isUnlocked) VintageBrass else Color.Gray
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                        .scale(cardBounce)
                        .clickable {
                            if (zone.isUnlocked) {
                                ZoneHeartCounts = ZoneHeartCounts + (zone.zoneId to (tapCount + 1))
                                onCollectRevenue(25L)
                                val animalNames = animalsInZone.take(2).joinToString("和") { it.name }.ifEmpty { "小動物" }
                                onSpeakVoice("摸摸${zone.zoneName}的$animalNames，牠們好喜歡你喔！")
                            } else {
                                onSpeakVoice("這是${zone.zoneName}，收集竹幣就能打開讓小動物住進來喔！")
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Row: Big Icon + Zone Name + Floating Hearts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = zoneIcon, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = zone.zoneName,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = LeatherDark
                                    )
                                    Text(
                                        text = if (zone.isUnlocked) "🐾 住著 ${animalsInZone.size} 隻好朋友" else "🔒 準備開放中",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (zone.isUnlocked) BambooGreen else DangerCoral
                                    )
                                }
                            }

                            if (tapCount > 0) {
                                Text(
                                    text = if (tapCount % 2 == 0) "💖🍎✨" else "🥰🎋💖",
                                    fontSize = 22.sp
                                )
                            }
                        }

                        // Middle: Visual Animal Friends Row
                        if (zone.isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF4ECD8), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (animalsInZone.isEmpty())
                                        "🏕️ 叢林樹屋營地準備好了！去森林帶小動物來住喔！"
                                    else
                                        "🏕️🔥 營地好朋友：${animalsInZone.take(3).joinToString("、") { it.name }}",
                                    fontSize = 13.sp,
                                    color = InkBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Bottom Interactive Toddler Play Buttons (Feed Apple & Pet Head)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        ZoneHeartCounts = ZoneHeartCounts + (zone.zoneId to (tapCount + 1))
                                        onCollectRevenue(30L)
                                        onSpeakVoice("啊姆啊姆！好甜的紅蘋果喔！謝謝你！送你30個竹幣！")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Text("🍎 餵蘋果 (+30)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        ZoneHeartCounts = ZoneHeartCounts + (zone.zoneId to (tapCount + 1))
                                        onCollectRevenue(20L)
                                        onSpeakVoice("摸摸頭、抱抱小動物，小動物對你笑咪咪！")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Text("💖 摸摸頭 (+20)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                        } else {
                            Text(
                                text = zone.description,
                                fontSize = 13.sp,
                                color = InkGrey,
                                fontWeight = FontWeight.Medium
                            )
                            Button(
                                onClick = {
                                    onUnlockZone(zone.zoneId, zone.unlockCost)
                                    onSpeakVoice("太棒了！打開${zone.zoneName}囉！")
                                },
                                enabled = playerCoins >= zone.unlockCost,
                                colors = ButtonDefaults.buttonColors(containerColor = VintageBrass),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "🔑 打開新樂園 (${zone.unlockCost} 🎋)",
                                    fontSize = 14.sp,
                                    color = LeatherDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
