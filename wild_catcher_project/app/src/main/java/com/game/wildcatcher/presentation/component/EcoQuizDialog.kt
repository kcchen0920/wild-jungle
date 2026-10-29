package com.game.wildcatcher.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
    onDismiss: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(18.dp))
                .background(ParchmentBg)
                .border(3.dp, VintageBrass, RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 自然生態趣味小學堂",
                        style = WildTypography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = LeatherDark
                        )
                    )
                    Text(
                        text = "獎勵: +${quiz.rewardCoins} 竹幣",
                        color = BambooGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question
                Text(
                    text = quiz.question,
                    style = WildTypography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = InkBlack,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val options = listOf(quiz.optionA, quiz.optionB, quiz.optionC)
                options.forEachIndexed { index, optionText ->
                    val isSelected = selectedIndex == index
                    val isCorrect = index == quiz.correctIndex

                    val bgColor = when {
                        !hasAnswered && isSelected -> SunsetAmber.copy(alpha = 0.25f)
                        hasAnswered && isCorrect -> BambooGreen.copy(alpha = 0.2f)
                        hasAnswered && isSelected && !isCorrect -> DangerCoral.copy(alpha = 0.2f)
                        else -> ParchmentCard
                    }

                    val borderColor = when {
                        !hasAnswered && isSelected -> SunsetAmber
                        hasAnswered && isCorrect -> BambooGreen
                        hasAnswered && isSelected && !isCorrect -> DangerCoral
                        else -> ParchmentBorder
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable(enabled = !hasAnswered) {
                                selectedIndex = index
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "${('A' + index)}. $optionText",
                            color = InkBlack,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                if (hasAnswered) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = quiz.explanation,
                        color = InkGrey,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!hasAnswered) {
                        Button(
                            onClick = {
                                if (selectedIndex != null) {
                                    hasAnswered = true
                                    val correct = selectedIndex == quiz.correctIndex
                                    onAnswerSubmitted(correct, if (correct) quiz.rewardCoins else 0)
                                }
                            },
                            enabled = selectedIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("確認答案")
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = LeatherBrown),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("領取完成")
                        }
                    }
                }
            }
        }
    }
}
