package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.game.wildcatcher.presentation.component.DaveNotebookFrame
import com.game.wildcatcher.presentation.theme.*

@Composable
fun ParentDashboardScreen(
    currentRestThresholdMinutes: Int,
    onRestThresholdChanged: (Int) -> Unit,
    totalPlayMinutes: Long,
    consecutiveCaptures: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isUnlocked by remember { mutableStateOf(false) }
    var answerInput by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    DaveNotebookFrame(
        title = "🛡️ 家長健康與指肌發育控制台",
        subtitle = "護眼排程 · 雙手精細動作發育統計 · 親子共學任務",
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
        if (!isUnlocked) {
            // Parental Lock Gate (家長防誤觸鎖)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🔒 家長安全確認", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LeatherDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text("請回答算術以進入控制台：3 + 5 = ？", fontSize = 15.sp, color = InkBlack)
                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("6", "7", "8", "9").forEach { num ->
                        Button(
                            onClick = {
                                if (num == "8") {
                                    isUnlocked = true
                                    hasError = false
                                } else {
                                    hasError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VintageBrass),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(num, fontSize = 18.sp, color = LeatherDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (hasError) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("答案不正確，請再試一次～", color = DangerCoral, fontSize = 12.sp)
                }
            }
        } else {
            // Dashboard Content
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Fine Motor Metrics & Eye Care Scheduling
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Metric Card 1: Toddler Motor Agility
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, VintageBrass),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🌱 幼兒小肌肉與雙側協調分析", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LeatherDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• 左手持續施力耐受度：良好 (平均持穩 3.2 秒)", fontSize = 12.sp, color = InkBlack)
                            Text("• 右手指尖節奏靈巧度：敏捷 (甜蜜點連擊率 86%)", fontSize = 12.sp, color = InkBlack)
                            Text("• 雙手雙側整合評級：🌟 雙手協同大師", fontSize = 12.sp, color = BambooGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Metric Card 2: Eye-Care Timer Setting
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, VintageBrass),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("⏱️ 護眼健康計時器設定", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LeatherDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("連續遊玩滿此時間，波波將主動提示休息並轉入暖色護眼模式：", fontSize = 11.sp, color = InkGrey)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(15, 20, 30).forEach { mins ->
                                    val isSelected = currentRestThresholdMinutes == mins
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onRestThresholdChanged(mins) },
                                        label = { Text("${mins} 分鐘") }
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Column: Parent-Child Reading Task
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, BambooGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📖", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("今日親子共讀小任務", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BambooGreen)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "【認識台灣國寶活化石】\n今天請與孩子一起打開圖鑑，聽聽看「櫻花鉤吻鮭」為什麼只能住在 16℃ 以下的高山冷水溪流裡？\n討論看看保護乾淨的水源對小魚有多重要！",
                                    fontSize = 12.sp,
                                    color = InkBlack,
                                    lineHeight = 18.sp
                                )
                            }

                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("完成共讀，返回遊戲", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
