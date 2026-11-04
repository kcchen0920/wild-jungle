package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier: Modifier = Modifier
) {
    DaveNotebookFrame(
        title = "🏡 小動物好朋友遊樂園",
        subtitle = "💖 住著 ${revenueReport.shelteredAnimalsCount} 隻可愛野生動物  |  快樂竹幣：$playerCoins 🎋",
        actions = {
            Button(
                onClick = { onCollectRevenue(50L) },
                colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🍎 餵小動物吃點心 (+50)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = { onCollectRevenue(revenueReport.totalHourlyRevenue) },
                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("💰 收取快樂幣 (+${revenueReport.totalHourlyRevenue})", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
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
        // Habitat Zones 2x2 Tablet Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(zones) { zone ->
                val animalsInZone = shelteredAnimals.filter { it.habitat == zone.habitatType }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (zone.isUnlocked) ParchmentCard else Color(0xFFE0DACB)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.5.dp,
                        color = if (zone.isUnlocked) VintageBrass else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Card Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (zone.zoneId) {
                                        "zone_rainforest" -> "🌴"
                                        "zone_alpine" -> "🌲"
                                        "zone_sky" -> "🦅"
                                        else -> "💧"
                                    },
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = zone.zoneName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = LeatherDark
                                    )
                                    Text(
                                        text = if (zone.isUnlocked) "容納量: ${animalsInZone.size} / ${zone.maxCapacity}" else "尚未開闢",
                                        fontSize = 11.sp,
                                        color = if (zone.isUnlocked) BambooGreen else DangerCoral
                                    )
                                }
                            }

                            if (zone.isUnlocked) {
                                Surface(
                                    color = BambooGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "生態等級 Lv.${zone.ecoLevel}",
                                        color = BambooGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Description
                        Text(
                            text = zone.description,
                            fontSize = 11.sp,
                            color = InkGrey,
                            lineHeight = 15.sp
                        )

                        // Animals List or Unlock button
                        if (zone.isUnlocked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (animalsInZone.isEmpty()) "尚未安置動物，出發捕捉吧！" else "收容中：${animalsInZone.take(3).joinToString("、") { it.name }}",
                                    fontSize = 11.sp,
                                    color = InkBlack,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "人流: ${zone.visitorFlowRatePerHour}/h",
                                    fontSize = 10.sp,
                                    color = SunsetAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("開闢成本：${zone.unlockCost} 竹幣", fontSize = 12.sp, color = InkBlack)
                                Button(
                                    onClick = { onUnlockZone(zone.zoneId, zone.unlockCost) },
                                    enabled = playerCoins >= zone.unlockCost,
                                    colors = ButtonDefaults.buttonColors(containerColor = VintageBrass),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("解鎖生態園區", fontSize = 11.sp, color = LeatherDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
