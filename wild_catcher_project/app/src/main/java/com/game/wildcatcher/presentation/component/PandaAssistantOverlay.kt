package com.game.wildcatcher.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.game.wildcatcher.domain.health.HealthAlertState
import com.game.wildcatcher.presentation.theme.*

@Composable
fun PandaAssistantOverlay(
    healthState: HealthAlertState,
    speechBubbleText: String?,
    onDismissAlert: (enableWarmMode: Boolean) -> Unit,
    onRestNow: () -> Unit,
    onSpeakVoice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 1. Gentle Speech Bubble on top-right / corner (Tap to hear Bobo speak!)
    AnimatedVisibility(
        visible = speechBubbleText != null && healthState is HealthAlertState.Normal,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (speechBubbleText != null) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ParchmentCard)
                    .border(2.dp, VintageBrass, RoundedCornerShape(16.dp))
                    .clickable { onSpeakVoice(speechBubbleText) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SunsetAmber)
                        .border(1.5.dp, LeatherBrown, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐼", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "小熊貓 波波 🔊",
                        color = LeatherBrown,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = speechBubbleText,
                        color = InkBlack,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // 2. Health Alert Modal: Panda's Rest Time (Voice Story + Parent Lock Gate)
    if (healthState is HealthAlertState.RestTimeTriggered) {
        var showParentGate by remember { mutableStateOf(false) }
        var gateError by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            onSpeakVoice("小獵人，太陽要下山囉！小熊貓波波想睡覺了，我們讓眼睛休息一下，送給你500個乖寶寶快樂竹幣喔！")
        }

        Dialog(
            onDismissRequest = { /* Prevent 4yo accidental outside-tap dismiss */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ParchmentBg)
                    .border(4.dp, VintageBrass, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bobo sleeping illustration badge
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SunsetAmber.copy(alpha = 0.25f))
                            .border(3.dp, SunsetAmber, CircleShape)
                            .clickable {
                                onSpeakVoice("太陽下山囉，陪小熊貓波波一起讓眼睛休息一下，拿500個快樂竹幣吧！")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐼🌙", fontSize = 34.sp)
                    }

                    Text(
                        text = "🌙 波波的晚安護眼時間 🔊",
                        style = WildTypography.headlineMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LeatherDark
                        )
                    )

                    Text(
                        text = "「小動物們都蓋好棉被要去睡午覺囉！我們也讓眼睛休息一下吧！」",
                        style = WildTypography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkBlack,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Positive Reinforcement Reward Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(2.dp, SunsetAmber, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "🎁 乖寶寶護眼大禮包：+500 快樂竹幣 🎋",
                                color = BambooGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "點下方綠色大按鈕陪波波休息，立即領取 500 枚竹幣並開啟柔和護眼模式！",
                                color = InkBlack,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Giant Primary Toddler Button: Rest Now & Claim +500 Coins
                    Button(
                        onClick = {
                            onSpeakVoice("你好乖喔！送給你500個快樂竹幣，眼睛休息一下，我們待會見！")
                            onRestNow()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                    ) {
                        Text(
                            text = "🌙 陪波波乖乖休息 (領 +500 🎋)",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Subtle Parent Arithmetic Gate to Extend Time
                    if (!showParentGate) {
                        TextButton(onClick = { showParentGate = true }) {
                            Text(
                                text = "🔒 家長延長遊玩時間 (需家長解鎖)",
                                color = InkGrey,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background( ParchmentCard)
                                .border(1.dp, ParchmentBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🛡️ 家長確認：4 + 3 = ？ (解鎖並開啟柔光護眼繼續)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LeatherDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("5", "6", "7", "8").forEach { ans ->
                                    Button(
                                        onClick = {
                                            if (ans == "7") {
                                                gateError = false
                                                onDismissAlert(true)
                                            } else {
                                                gateError = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = VintageBrass),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Text(ans, color = LeatherDark, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                    }
                                }
                            }
                            if (gateError) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("答案不正確，請家長協助操作～", color = DangerCoral, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
