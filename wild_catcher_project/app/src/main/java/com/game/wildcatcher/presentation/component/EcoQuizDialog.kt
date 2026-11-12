package com.game.wildcatcher.presentation.component

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
import com.game.wildcatcher.data.model.EcoQuiz
import com.game.wildcatcher.presentation.theme.*

@Composable
fun EcoQuizDialog(
    quiz: EcoQuiz,
    onAnswerSubmitted: (isCorrect: Boolean, rewardCoins: Int) -> Unit,
    onDismiss: () -> Unit,
    onSpeakVoice: (String) -> Unit = {}
) {
    var isSolved by remember(quiz) { mutableStateOf(false) }
    var triedIndices by remember(quiz) { mutableStateOf(setOf<Int>()) }

    val options = listOf(quiz.optionA, quiz.optionB, quiz.optionC)
    val badges = listOf("🟢 1", "🟡 2", "🔵 3")

    // Auto-read the question aloud when the dialog opens for 4-year-olds
    LaunchedEffect(quiz) {
        onSpeakVoice("動物好朋友大猜謎！${quiz.question}。第1個是${quiz.optionA}，第2個是${quiz.optionB}，第3個是${quiz.optionC}。請點大按鈕！")
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(ParchmentBg)
                .border(4.dp, VintageBrass, RoundedCornerShape(24.dp))
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header Row with Giant Close & Reward Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐼🌟", fontSize = 30.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "波波有聲大猜謎",
                            style = WildTypography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = LeatherDark
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.5.dp, BambooGreen, RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🎁 送 +${quiz.rewardCoins} 🎋",
                                color = BambooGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(LeatherDark)
                        ) {
                            Text("✕", color = BrassHighlight, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                // Question Card (Tap anywhere to hear Bobo read it again!)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF9C4))
                        .border(2.dp, SunsetAmber, RoundedCornerShape(16.dp))
                        .clickable {
                            onSpeakVoice("${quiz.question}。第1個是${quiz.optionA}，第2個是${quiz.optionB}，第3個是${quiz.optionC}。")
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quiz.question,
                            style = WildTypography.bodyLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = InkBlack,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SunsetAmber)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("🔊 聽題目", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }

                // 3 Giant Visual Option Buttons (One-Tap Action, Zero-Frustration Retry!)
                options.forEachIndexed { index, optionText ->
                    val isCorrect = index == quiz.correctIndex
                    val wasTriedWrong = triedIndices.contains(index) && !isCorrect

                    val bgColor = when {
                        isSolved && isCorrect -> Color(0xFFC8E6C9)
                        wasTriedWrong -> Color(0xFFFFECB3)
                        else -> ParchmentCard
                    }

                    val borderColor = when {
                        isSolved && isCorrect -> BambooGreen
                        wasTriedWrong -> SunsetAmber
                        else -> VintageBrass
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(2.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isSolved) {
                                if (isCorrect) {
                                    isSolved = true
                                    onSpeakVoice("叮咚叮咚！答對了！就是${optionText}！${quiz.explanation}")
                                    onAnswerSubmitted(true, quiz.rewardCoins)
                                } else {
                                    triedIndices = triedIndices + index
                                    onSpeakVoice("這是${optionText}喔～再試試看別的顏色按鈕吧！")
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = badges[index],
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LeatherDark
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = optionText,
                                    color = InkBlack,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Text(
                                text = when {
                                    isSolved && isCorrect -> "🎉 答對囉！"
                                    wasTriedWrong -> "😊 再試別的"
                                    else -> "👆 點選"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSolved && isCorrect) BambooGreen else LeatherBrown
                            )
                        }
                    }
                }

                // Celebration Explanation & Big Collect Reward Button
                if (isSolved) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.5.dp, BambooGreen, RoundedCornerShape(14.dp))
                            .clickable { onSpeakVoice(quiz.explanation) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "🌟 波波誇獎你：${quiz.explanation}",
                            color = InkBlack,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Text(
                            text = "🎁 太棒了！收下 +${quiz.rewardCoins} 快樂竹幣 ⭐",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
