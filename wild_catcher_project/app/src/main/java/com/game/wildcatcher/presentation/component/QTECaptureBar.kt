package com.game.wildcatcher.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.domain.capture.QTEState
import com.game.wildcatcher.presentation.theme.*

@Composable
fun QTECaptureBar(
    qteState: QTEState,
    onReelTap: () -> Unit,
    onUseIncenseDart: () -> Unit,
    incenseAmmoCount: Int,
    modifier: Modifier = Modifier
) {
    // Pulse animation for Sweet Spot hitting
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val sweetPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweetPulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LeatherDark.copy(alpha = 0.92f))
            .border(2.dp, VintageBrass, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Status: Progress & Tension
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Net Capture Progress
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🌐 夢幻泡泡網捕捉度",
                        color = BubbleCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(qteState.captureProgress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { qteState.captureProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = BubbleCyan,
                    trackColor = Color(0xFF1E3A40)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Animal Escape Tension
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⚠️ 動物掙脫警戒度",
                        color = DangerCoral,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(qteState.animalTension * 100).toInt()}%",
                        color = if (qteState.animalTension > 0.75f) DangerCoral else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { qteState.animalTension },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = DangerCoral,
                    trackColor = Color(0xFF4A1A1A)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Difficulty Adjustment notification badge (if active)
        if (qteState.ddaToleranceBonus > 0f) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SunsetAmber.copy(alpha = 0.25f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "✨ 波波輔助模式啟動：甜蜜點放寬 +${(qteState.ddaToleranceBonus * 100).toInt()}%，動物掙扎趨於溫和！",
                    color = BrassHighlight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // The Main Horizontal QTE Capture Bar Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF161514))
                .border(2.dp, ParchmentBorder.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barW = size.width
                val barH = size.height

                // 1. Draw Sweet Spot zone
                val sweetW = barW * qteState.sweetSpotWidth
                val sweetLeft = (barW * qteState.sweetSpotCenter) - (sweetW / 2f)

                // Sweet spot glow background
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BambooLight.copy(alpha = 0.2f),
                            BambooGreen.copy(alpha = 0.85f * sweetPulse),
                            BambooLight.copy(alpha = 0.2f)
                        ),
                        startX = sweetLeft,
                        endX = sweetLeft + sweetW
                    ),
                    topLeft = Offset(sweetLeft, 2f),
                    size = Size(sweetW, barH - 4f),
                    cornerRadius = CornerRadius(16f, 16f)
                )

                // Sweet spot border brackets
                drawRoundRect(
                    color = BrassHighlight,
                    topLeft = Offset(sweetLeft, 2f),
                    size = Size(sweetW, barH - 4f),
                    cornerRadius = CornerRadius(16f, 16f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )

                // 2. Draw Moving Wildlife Dynamic Cursor
                val cursorX = barW * qteState.currentCursorPos
                val cursorColor = if (qteState.isInSweetSpot) BrassHighlight else DangerCoral

                // Dynamic halo ring
                drawCircle(
                    color = cursorColor.copy(alpha = 0.35f),
                    radius = 22f,
                    center = Offset(cursorX, barH / 2f)
                )

                // Solid center pin
                drawCircle(
                    color = cursorColor,
                    radius = 12f,
                    center = Offset(cursorX, barH / 2f)
                )

                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(cursorX, barH / 2f)
                )
            }

            // Overlay indicator text
            Text(
                text = if (qteState.isInSweetSpot) "🎯 完美時機！立即點擊！" else "⏳ 等待游標進入綠色甜蜜區",
                color = if (qteState.isInSweetSpot) BrassHighlight else Color.LightGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interaction Action Controls: Large Tablet-Friendly Touch Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Calming Bamboo Incense Dart Button
            Button(
                onClick = onUseIncenseDart,
                enabled = incenseAmmoCount > 0 && !qteState.isIncenseDartActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41),
                    disabledContainerColor = Color(0xFF3E2723)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.35f)
                    .height(52.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎋 竹香安撫彈",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (qteState.isIncenseDartActive) "安撫中 (-40%掙扎)" else "剩餘: $incenseAmmoCount",
                        fontSize = 10.sp,
                        color = BrassHighlight
                    )
                }
            }

            // Big Reel Action Button: Press in Sweet Spot!
            Button(
                onClick = onReelTap,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (qteState.isInSweetSpot) BambooGreen else SunsetAmber
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .weight(0.65f)
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (qteState.isInSweetSpot) "⚡ 壓制收線！(連擊: ${qteState.comboCount})" else "收線收網",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
