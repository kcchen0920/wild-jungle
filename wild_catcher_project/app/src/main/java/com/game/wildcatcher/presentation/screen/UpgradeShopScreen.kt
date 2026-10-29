package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.game.wildcatcher.data.model.PlayerProfile
import com.game.wildcatcher.presentation.component.DaveNotebookFrame
import com.game.wildcatcher.presentation.theme.*

data class UpgradeItem(
    val id: String,
    val title: String,
    val icon: String,
    val currentLevel: Int,
    val maxLevel: Int,
    val description: String,
    val upgradeCost: Long
)

@Composable
fun UpgradeShopScreen(
    profile: PlayerProfile,
    onUpgradeItem: (String, Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        UpgradeItem(
            id = "gun_power",
            title = "熊貓槍 · 氣壓收線馬達",
            icon = "🔫",
            currentLevel = profile.pandaGunPowerLevel,
            maxLevel = 5,
            description = "提升甜蜜點點擊壓制效率 (+15% 捕捉能量累積速度)",
            upgradeCost = (profile.pandaGunPowerLevel * 300L)
        ),
        UpgradeItem(
            id = "ammo_capacity",
            title = "多功能戰術背包 · 泡泡網擴容",
            icon = "🎒",
            currentLevel = profile.maxAmmoCapacity / 50,
            maxLevel = 4,
            description = "擴充夢幻泡泡網上限至 ${profile.maxAmmoCapacity + 50} 發",
            upgradeCost = 250L
        ),
        UpgradeItem(
            id = "net_radius",
            title = "竹香彈霧化噴頭",
            icon = "🎋",
            currentLevel = profile.bubbleNetRadiusLevel,
            maxLevel = 3,
            description = "加強安撫竹香擴散效果，動物掙扎幅度額外降低 15%",
            upgradeCost = 400L
        ),
        UpgradeItem(
            id = "safari_vehicle",
            title = "叢林探險越野吉普車",
            icon = "🚙",
            currentLevel = profile.vehicleLevel,
            maxLevel = 3,
            description = "提高前往深山與雲海探索生態區的速度與穩定度",
            upgradeCost = 800L
        )
    )

    DaveNotebookFrame(
        title = "🛠️ 野生裝備升級工作檯",
        subtitle = "可用竹幣：${profile.coins}  |  金竹種子：${profile.goldenBambooSeeds}",
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items.size) { index ->
                val item = items[index]
                val canAfford = profile.coins >= item.upgradeCost
                val isMax = item.currentLevel >= item.maxLevel

                Card(
                    colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = LeatherDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Lv.${item.currentLevel} / ${item.maxLevel}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BambooGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = InkGrey
                                )
                            }
                        }

                        Button(
                            onClick = { onUpgradeItem(item.id, item.upgradeCost) },
                            enabled = canAfford && !isMax,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMax) Color.Gray else BambooGreen
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isMax) "已達上限" else "升級 (${item.upgradeCost} 幣)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
