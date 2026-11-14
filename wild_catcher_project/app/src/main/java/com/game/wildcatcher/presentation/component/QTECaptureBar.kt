package com.game.wildcatcher.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
 * 四歲幼兒專用雙手協同控制器 (4yo Toddler Visual Star & Bimanual Controller)
 * - 移除複雜百分比文字，改以「5顆大星星集氣」呈現進度
 * - 左手大按鈕：長按抱抱安撫
 * - 右手大按鈕：連拍吹大泡泡
 */
@Composable
fun QTECaptureBar(
    qteState: QTEState,
    onLeftHandPressChanged: (isHolding: Boolean) -> Unit,
    onRightHandTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_sweet")
    val sweetPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweetPulse"
    )

    val leftButtonScale by animateFloatAsState(
        targetValue = if (qteState.isLeftHandStabilizing) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "leftButtonScale"
    )

    val litStars = (qteState.captureProgress * 5f).toInt().coerceIn(0, 5)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(LeatherDark.copy(alpha = 0.95f))
            .border(3.dp, VintageBrass, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Visual Star Progress Bar (5 Giant Stars instead of abstract percentages)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(SunsetAmber.copy(alpha = 0.25f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("👐 ", fontSize = 16.sp)
                Text(
                    text = qteState.fingerAgilityLevel,
                    color = BrassHighlight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // 5 Giant Friendship Stars
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "好朋友愛心集氣：",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                for (i in 1..5) {
                    val isLit = i <= litStars
                    Text(
                        text = if (isLit) "⭐" else "⚪",
                        fontSize = if (isLit) 22.sp else 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center: Rainbow Bubble Progress & Sweet-Spot Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(Color(0xFF141312))
                .border(2.dp, ParchmentBorder.copy(alpha = 0.7f), RoundedCornerShape(19.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barW = size.width
                val barH = size.height

                // Background overall capture progress fill
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00ACC1).copy(alpha = 0.35f), BambooGreen.copy(alpha = 0.55f))
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(barW * qteState.captureProgress.coerceIn(0f, 1f), barH),
                    cornerRadius = CornerRadius(19f, 19f)
                )

                // Draw Sweet Spot
                val sweetW = barW * qteState.sweetSpotWidth
                val sweetLeft = (barW * qteState.sweetSpotCenter) - (sweetW / 2f)

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BambooLight.copy(alpha = 0.35f),
                            BambooGreen.copy(alpha = 0.9f * sweetPulse),
                            BambooLight.copy(alpha = 0.35f)
                        ),
                        startX = sweetLeft,
                        endX = sweetLeft + sweetW
                    ),
                    topLeft = Offset(sweetLeft, 2f),
                    size = Size(sweetW, barH - 4f),
                    cornerRadius = CornerRadius(16f, 16f)
                )

                // Moving Cute Bubble Cursor
                val cursorX = barW * qteState.currentCursorPos
                val cursorColor = if (qteState.isInSweetSpot) BrassHighlight else BubbleCyan

                drawCircle(
                    color = cursorColor.copy(alpha = 0.4f),
                    radius = 22f,
                    center = Offset(cursorX, barH / 2f)
                )
                drawCircle(
                    color = cursorColor,
                    radius = 13f,
                    center = Offset(cursorX, barH / 2f)
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(cursorX, barH / 2f)
                )
            }

            Text(
                text = "⭐ 集滿 5 顆星星就抓到好朋友囉！",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Single Giant Toddler-Friendly Tap Button
        Button(
            onClick = onRightHandTap,
            colors = ButtonDefaults.buttonColors(
                containerColor = BambooGreen
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .scale(sweetPulse)
                .border(3.dp, BrassHighlight, RoundedCornerShape(20.dp))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👆🫧",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "點這裡吹大泡泡抓動物！($litStars / 5 ⭐)",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
