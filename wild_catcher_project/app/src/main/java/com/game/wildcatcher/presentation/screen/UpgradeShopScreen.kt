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
            title = "🌈 彩虹魔法泡泡槍",
            icon = "🔫",
            currentLevel = profile.pandaGunPowerLevel,
            maxLevel = 5,
            description = "發射七彩星星光芒，拍拍手收線變得超級快！(+15% 收網速度)",
            upgradeCost = (profile.pandaGunPowerLevel * 300L)
        ),
        UpgradeItem(
            id = "ammo_capacity",
            title = "🎒 波波的探險百寶袋",
            icon = "🎒",
            currentLevel = profile.maxAmmoCapacity / 50,
            maxLevel = 4,
            description = "裝入更多好玩的夢幻彩色泡泡！(上限擴充至 ${profile.maxAmmoCapacity + 50} 發)",
            upgradeCost = 250L
        ),
        UpgradeItem(
            id = "net_radius",
            title = "🍓 草莓果香安撫噴霧",
            icon = "🍓",
            currentLevel = profile.bubbleNetRadiusLevel,
            maxLevel = 3,
            description = "香香甜甜的草莓味，小動物最喜歡靠近跟你抱抱！",
            upgradeCost = 400L
        ),
        UpgradeItem(
            id = "safari_vehicle",
            title = "🚙 彩色探險歡樂巴士",
            icon = "🚌",
            currentLevel = profile.vehicleLevel,
            maxLevel = 3,
            description = "載著波波和小朋友開向森林、高山跟小動物玩捉迷藏！",
            upgradeCost = 800L
        )
    )

    DaveNotebookFrame(
        title = "🎨 波波的歡樂探險更衣室與道具屋",
        subtitle = "快樂竹幣：${profile.coins} 🎋 | 金竹種子：${profile.goldenBambooSeeds} ⭐",
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
