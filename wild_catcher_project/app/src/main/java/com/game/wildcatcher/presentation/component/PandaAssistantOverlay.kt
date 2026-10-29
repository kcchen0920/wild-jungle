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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.game.wildcatcher.domain.health.HealthAlertState
import com.game.wildcatcher.presentation.theme.*

@Composable
fun PandaAssistantOverlay(
    healthState: HealthAlertState,
    speechBubbleText: String?,
    onDismissAlert: (enableWarmMode: Boolean) -> Unit,
    onRestNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Gentle Speech Bubble on top-right / corner
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
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bobo avatar circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SunsetAmber)
                        .border(1.5.dp, LeatherBrown, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐾", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "小熊貓 波波",
                        color = LeatherBrown,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = speechBubbleText,
                        color = InkBlack,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }

    // 2. Health Alert Modal: Panda's Rest Time (30-minute eye care reminder)
    if (healthState is HealthAlertState.RestTimeTriggered) {
        Dialog(onDismissRequest = { onDismissAlert(true) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ParchmentBg)
                    .border(4.dp, VintageBrass, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Bobo rest illustration badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SunsetAmber.copy(alpha = 0.2f))
                            .border(2.dp, SunsetAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌅", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🌿 波波的溫馨護眼提醒",
                        style = WildTypography.headlineMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LeatherDark
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "「太陽要下山囉，讓眼睛休息一下，明天再來探索！」",
                        style = WildTypography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = InkBlack,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Positive Reinforcement: Sleep reward announcement
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(1.dp, SunsetAmber, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "🌙 睡飽飽隔日登入獎勵已預約！",
                                color = LeatherBrown,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "只要現在放下平板休息，下次登入即可獲贈 500 枚竹幣與 10 顆金竹種子，並自動啟用護眼柔光模式！",
                                color = InkGrey,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDismissAlert(true) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "啟用護眼模式繼續",
                                color = LeatherDark,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = onRestNow,
                            colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "好好休息 (領取預約)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
