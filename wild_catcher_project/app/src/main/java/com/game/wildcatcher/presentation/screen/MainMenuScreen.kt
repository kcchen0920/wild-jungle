package com.game.wildcatcher.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.PlayerProfile
import com.game.wildcatcher.presentation.theme.*

@Composable
fun MainMenuScreen(
    profile: PlayerProfile,
    onStartExpedition: () -> Unit,
    onOpenShelter: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenShop: () -> Unit,
    onTriggerQuiz: () -> Unit,
    onSimulateThirtyMinutes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LeatherDark)
            .padding(24.dp)
    ) {
        // Main Leather-bound Adventure Book Cover
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF5D3A24), Color(0xFF23140E)),
                        radius = 1200f
                    )
                )
                .border(4.dp, VintageBrass, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Left Panel: Hero Dave & Assistant Bobo profile and statistics
                Column(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ParchmentCard)
                        .border(1.5.dp, ParchmentBorder, RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Character badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(BambooGreen)
                                    .border(2.dp, VintageBrass, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👓", fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile.playerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = LeatherDark
                                )
                                Text(
                                    text = "等級 ${profile.playerLevel} 資深保育官",
                                    fontSize = 12.sp,
                                    color = BambooGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats ledger
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ParchmentBg),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("💰 生態竹幣:", fontSize = 13.sp, color = InkGrey)
                                    Text("${profile.coins}", fontWeight = FontWeight.Bold, color = SunsetAmber)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("🌱 金竹種子:", fontSize = 13.sp, color = InkGrey)
                                    Text("${profile.goldenBambooSeeds}", fontWeight = FontWeight.Bold, color = BambooGreen)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("🌐 泡泡網儲量:", fontSize = 13.sp, color = InkGrey)
                                    Text("${profile.bubbleAmmoCount} / ${profile.maxAmmoCapacity}", fontWeight = FontWeight.Bold, color = BubbleCyan)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("⏱️ 本次探索時長:", fontSize = 13.sp, color = InkGrey)
                                    val mins = profile.totalActiveSeconds / 60
                                    Text("${mins} 分鐘", fontWeight = FontWeight.Bold, color = if (mins >= 30) DangerCoral else InkBlack)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bobo dialogue box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF9C4))
                                .border(1.dp, SunsetAmber, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🐾 波波導覽：「戴夫，今天的天氣很適合去森林深處觀察動物喔！熊貓槍已經校準完畢！」",
                                fontSize = 12.sp,
                                color = LeatherDark,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Bottom fast health test button
                    OutlinedButton(
                        onClick = onSimulateThirtyMinutes,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("⚡ 測試 30分鐘護眼提醒機制", fontSize = 11.sp, color = LeatherDark)
                    }
                }

                // Right Panel: Book Title & Main Exploration Gateways
                Column(
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Big Game Title Header
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎋", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "野性捕捉大師",
                                style = WildTypography.headlineLarge.copy(
                                    fontSize = 34.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrassHighlight
                                )
                            )
                        }
                        Text(
                            text = "WILD CATCHER: JUNGLE & SKY  |  橫向動態生態冒險",
                            style = WildTypography.bodyMedium.copy(
                                color = ParchmentBorder,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(start = 42.dp)
                        )
                    }

                    // Gateway Cards Grid (Dave the Diver Style Big Clickable Plates)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Primary Action: Start Expedition
                        Button(
                            onClick = onStartExpedition,
                            colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("出發捕捉動物 (出征叢林與天空)", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("出動熊貓槍發射泡泡網，展開寫實生態捕捉！", fontSize = 11.sp, color = BambooLight)
                                    }
                                }
                                Text("出發 ▶", fontWeight = FontWeight.Bold, color = BrassHighlight, fontSize = 16.sp)
                            }
                        }

                        // 2. Secondary Row: Eco Shelter & Animal Journal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Eco Shelter Sanctuary
                            Card(
                                onClick = onOpenShelter,
                                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                                border = androidx.compose.foundation.BorderStroke(2.dp, VintageBrass),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(82.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("🏡 自然生態庇護所", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LeatherDark)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("安置已捕獲動物，接待遊客獲取門票收益！", fontSize = 10.sp, color = InkGrey)
                                }
                            }

                            // Field Journal Encyclopedia
                            Card(
                                onClick = onOpenJournal,
                                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                                border = androidx.compose.foundation.BorderStroke(2.dp, VintageBrass),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(82.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("📖 32種寫實動物圖鑑", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LeatherDark)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("保育燈號、PBR寫實模型與真實叫聲檔案！", fontSize = 10.sp, color = InkGrey)
                                }
                            }
                        }

                        // 3. Third Row: Equipment Shop & Eco Trivia Quiz
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Upgrade Shop
                            Card(
                                onClick = onOpenShop,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, VintageBrass),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🛠️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("裝備升級工作檯", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrassHighlight)
                                        Text("強化熊貓槍壓制力與彈藥", fontSize = 10.sp, color = Color.LightGray)
                                    }
                                }
                            }

                            // Trivia Quiz
                            Card(
                                onClick = onTriggerQuiz,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, BambooLight),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💡", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("科普小學堂問答", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("答對領取竹幣獎勵", fontSize = 10.sp, color = BambooLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
