package com.game.wildcatcher.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.domain.capture.QTEState
import com.game.wildcatcher.presentation.theme.*

/**
 * 幼兒左右手協同訓練專用控制器 (Bimanual Early Motor Skill QTE Controller)
 * - 左手側：大拇指/掌心持續穩定按壓 (鍛鍊本體感覺施力控制，穩定動物氣場)
 * - 右手側：食指/雙指交替靈活節奏點擊 (鍛鍊指間獨立性與打擊靈活度)
 * - 具備超大防呆觸控區域、微物理回彈動畫與即時「小肌肉活力評級」
 */
@Composable
fun QTECaptureBar(
    qteState: QTEState,
    onLeftHandPressChanged: (isHolding: Boolean) -> Unit,
    onRightHandTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Soft breathing pulse for sweet spot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_sweet")
    val sweetPulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweetPulse"
    )

    // Left button press spring scale
    val leftButtonScale by animateFloatAsState(
        targetValue = if (qteState.isLeftHandStabilizing) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "leftButtonScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LeatherDark.copy(alpha = 0.94f))
            .border(3.dp, VintageBrass, RoundedCornerShape(22.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Status Header: Toddler Finger Agility Badge & Capture Meters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toddler Motor Agility Level
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SunsetAmber.copy(alpha = 0.22f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🌱 雙手協調指標：",
                    color = BrassHighlight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = qteState.fingerAgilityLevel,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Capture Progress & Gentle Tension
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "🌐 泡泡網收攏: ${(qteState.captureProgress * 100).toInt()}%",
                    color = BubbleCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⚠️ 警覺度: ${(qteState.animalTension * 100).toInt()}%",
                    color = if (qteState.animalTension > 0.7f) DangerCoral else Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center: Smooth Horizontal Sweet-Spot Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(Color(0xFF141312))
                .border(2.dp, ParchmentBorder.copy(alpha = 0.6f), RoundedCornerShape(21.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barW = size.width
                val barH = size.height

                // Draw Sweet Spot
                val sweetW = barW * qteState.sweetSpotWidth
                val sweetLeft = (barW * qteState.sweetSpotCenter) - (sweetW / 2f)

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BambooLight.copy(alpha = 0.3f),
                            BambooGreen.copy(alpha = 0.9f * sweetPulse),
                            BambooLight.copy(alpha = 0.3f)
                        ),
                        startX = sweetLeft,
                        endX = sweetLeft + sweetW
                    ),
                    topLeft = Offset(sweetLeft, 2f),
                    size = Size(sweetW, barH - 4f),
                    cornerRadius = CornerRadius(18f, 18f)
                )

                // Sweet spot bracket highlight
                drawRoundRect(
                    color = BrassHighlight,
                    topLeft = Offset(sweetLeft, 2f),
                    size = Size(sweetW, barH - 4f),
                    cornerRadius = CornerRadius(18f, 18f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                )

                // Moving Wildlife Cursor (soft round ball for kids)
                val cursorX = barW * qteState.currentCursorPos
                val cursorColor = if (qteState.isInSweetSpot) BrassHighlight else DangerCoral

                // Dynamic glow halo
                drawCircle(
                    color = cursorColor.copy(alpha = 0.35f),
                    radius = 24f,
                    center = Offset(cursorX, barH / 2f)
                )

                drawCircle(
                    color = cursorColor,
                    radius = 14f,
                    center = Offset(cursorX, barH / 2f)
                )

                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = Offset(cursorX, barH / 2f)
                )
            }

            Text(
                text = if (qteState.isInSweetSpot) "🎯 綠色區內！右手快快點擊！" else "👀 觀察游標，雙手準備好～",
                color = if (qteState.isInSweetSpot) BrassHighlight else Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Bimanual Wings: Left Hand Stabilizer & Right Hand Tap Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- LEFT HAND WING: Force & Pressure Holding (左手持續安撫) ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .scale(leftButtonScale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (qteState.isLeftHandStabilizing)
                            Brush.verticalGradient(listOf(Color(0xFF8D6E63), Color(0xFF4E342E)))
                        else
                            Brush.verticalGradient(listOf(Color(0xFF5D4037), Color(0xFF3E2723)))
                    )
                    .border(
                        width = if (qteState.isLeftHandStabilizing) 3.dp else 1.5.dp,
                        color = if (qteState.isLeftHandStabilizing) BrassHighlight else ParchmentBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onLeftHandPressChanged(true)
                                tryAwaitRelease()
                                onLeftHandPressChanged(false)
                            }
                        )
                    }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (qteState.isLeftHandStabilizing) "🌿" else "✋",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (qteState.isLeftHandStabilizing) "左手穩住中！(-50%晃動)" else "左手大拇指【長按住】",
                            color = if (qteState.isLeftHandStabilizing) BrassHighlight else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "鍛鍊手指持續施力與穩定平衡",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- RIGHT HAND WING: Rhythmic Precision Tapping (右手節奏連擊) ---
            Button(
                onClick = onRightHandTap,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (qteState.isInSweetSpot) BambooGreen else SunsetAmber
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (qteState.isInSweetSpot) "⚡" else "👆",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (qteState.isInSweetSpot) "右手連擊！(收線 +40%)" else "右手食指【節奏輕點】",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "連擊次數: ${qteState.rightHandTapCount} 次 · 鍛鍊靈巧度",
                            color = BrassHighlight,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
