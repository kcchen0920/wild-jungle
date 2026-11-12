package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onSpeakVoice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = listOf(
        UpgradeItem(
            id = "gun_power",
            title = "🌈 彩虹魔法泡泡槍",
            icon = "🔫🫧",
            currentLevel = profile.pandaGunPowerLevel,
            maxLevel = 5,
            description = "吹出超大七彩星星泡泡，跟小動物做朋友變得超級快！",
            upgradeCost = (profile.pandaGunPowerLevel * 200L)
        ),
        UpgradeItem(
            id = "ammo_capacity",
            title = "🎒 波波的探險百寶袋",
            icon = "🎒✨",
            currentLevel = (profile.maxAmmoCapacity / 50).coerceIn(1, 4),
            maxLevel = 4,
            description = "裝入滿滿的彩色泡泡水和好吃點心！",
            upgradeCost = 200L
        ),
        UpgradeItem(
            id = "net_radius",
            title = "🍓 草莓果香抱抱噴霧",
            icon = "🍓💖",
            currentLevel = profile.bubbleNetRadiusLevel,
            maxLevel = 3,
            description = "香香甜甜的紅草莓味道，小動物最喜歡跑過來跟你抱抱！",
            upgradeCost = 250L
        ),
        UpgradeItem(
            id = "safari_vehicle",
            title = "🚌 森林歡樂探險巴士",
            icon = "🚌🌴",
            currentLevel = profile.vehicleLevel,
            maxLevel = 3,
            description = "載著波波和小朋友開向森林、高山跟小動物玩捉迷藏！",
            upgradeCost = 400L
        )
    )

    DaveNotebookFrame(
        title = "🎨 波波的彩虹道具屋",
        subtitle = "🎋 快樂竹幣：${profile.coins}  |  點點道具聽波波介紹喔！",
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items.size) { index ->
                val item = items[index]
                val canAfford = profile.coins >= item.upgradeCost
                val isMax = item.currentLevel >= item.maxLevel
                val starsText = "⭐".repeat(item.currentLevel.coerceAtMost(item.maxLevel)) +
                    "⚪".repeat((item.maxLevel - item.currentLevel).coerceAtLeast(0))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)),
                    border = BorderStroke(2.dp, VintageBrass),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSpeakVoice("${item.title}！${item.description}")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(item.icon, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = LeatherDark
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = starsText,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🔊 ${item.description}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = InkBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                onUpgradeItem(item.id, item.upgradeCost)
                                onSpeakVoice("太棒了！${item.title}變得更厲害囉！")
                            },
                            enabled = canAfford && !isMax,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMax) Color.Gray else BambooGreen
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text(
                                text = if (isMax) "🌟 超級滿級" else "✨ 換新 (${item.upgradeCost} 🎋)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
