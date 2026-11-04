package com.game.wildcatcher.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.AnimalEntity
import com.game.wildcatcher.domain.capture.CaptureEngine
import com.game.wildcatcher.domain.capture.QTEState
import com.game.wildcatcher.domain.weather.EnvironmentState
import com.game.wildcatcher.presentation.component.PbrAnimalRenderer
import com.game.wildcatcher.presentation.component.QTECaptureBar
import com.game.wildcatcher.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun CaptureBattleScreen(
    animal: AnimalEntity,
    consecutiveFailures: Int,
    gunPowerLevel: Int,
    incenseAmmoCount: Int,
    environment: EnvironmentState,
    onCaptureSuccess: (AnimalEntity) -> Unit,
    onCaptureFailed: () -> Unit,
    onBackToMenu: () -> Unit,
    onSpeakVoice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val captureEngine = remember(animal.id) {
        CaptureEngine(
            targetAnimal = animal,
            consecutiveFailures = consecutiveFailures,
            gunPowerLevel = gunPowerLevel
        )
    }

    var qteState by remember { mutableStateOf(captureEngine.getCurrentState()) }
    var isNetFired by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showEscapeDialog by remember { mutableStateOf(false) }
    var rightTapParticleCount by remember { mutableStateOf(0) }

    // Toddler voice guidance triggers
    LaunchedEffect(isNetFired) {
        if (isNetFired) {
            onSpeakVoice("伸出左手按住抱抱，右手拍拍吹大泡泡！")
        }
    }

    LaunchedEffect(qteState.isCompleted) {
        if (qteState.isCompleted) {
            showSuccessDialog = true
            onSpeakVoice("哇！太棒了！小動物成為你的好朋友了！")
        }
    }

    // Dynamic directional light rotation angle for PBR rendering
    val infiniteTransition = rememberInfiniteTransition(label = "pbr_light")
    val dynamicLightAngle by infiniteTransition.animateFloat(
        initialValue = 35f,
        targetValue = 145f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightAngle"
    )

    // Gentle physical breathing floating bounce
    val floatBounce by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatBounce"
    )

    // QTE Engine frame loop (~60 FPS)
    LaunchedEffect(isNetFired, qteState.isCompleted, qteState.isEscaped) {
        if (!isNetFired) return@LaunchedEffect
        while (!qteState.isCompleted && !qteState.isEscaped) {
            delay(16)
            captureEngine.tick(0.016f)
            qteState = captureEngine.getCurrentState()

            if (qteState.isCompleted) {
                showSuccessDialog = true
            } else if (qteState.isEscaped) {
                showEscapeDialog = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = when (environment.timeOfDay.isNight) {
                        true -> listOf(Color(0xFF0F1B29), Color(0xFF1E2D3D))
                        false -> listOf(Color(0xFF264653), Color(0xFF1B3B48))
                    }
                )
            )
            .padding(14.dp)
    ) {
        // Top HUD Bar: Tablet Landscape Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button & Target Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToMenu,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LeatherDark.copy(alpha = 0.8f))
                        .border(1.dp, VintageBrass, CircleShape)
                ) {
                    Text("◀", color = VintageBrass, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = animal.name,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = animal.gradeLabel,
                            color = when (animal.grade) {
                                com.game.wildcatcher.data.model.ConservationGrade.GREEN -> BambooLight
                                com.game.wildcatcher.data.model.ConservationGrade.YELLOW -> SunsetAmber
                                com.game.wildcatcher.data.model.ConservationGrade.RED -> DangerCoral
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "棲地：${animal.habitatName}  |  學名：${animal.scientificName}",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            // Toddler Dual-Hand Guidance Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SunsetAmber.copy(alpha = 0.25f))
                    .border(1.dp, SunsetAmber, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👐 幼兒雙手協同訓練模式：左手穩住 · 右手收網",
                    color = BrassHighlight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Arena: Squishy Bouncy PBR Realistic Animal Renderer with Spring Physics
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset(y = floatBounce.dp)
                    .scale(scaleX = qteState.squishScaleX, scaleY = qteState.squishScaleY)
            ) {
                PbrAnimalRenderer(
                    animal = animal,
                    lightAngleDeg = dynamicLightAngle,
                    modifier = Modifier
                        .size(260.dp)
                        .clickable {
                            if (!isNetFired) {
                                isNetFired = true
                            }
                        }
                )

                // Dream Bubble Net Visual Overlay
                if (isNetFired) {
                    Box(
                        modifier = Modifier
                            .size(290.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .border(3.5.dp, BubbleCyan.copy(alpha = 0.85f), CircleShape)
                            .background(BubbleCyan.copy(alpha = 0.12f))
                    )
                }
            }

            // Initial Launch Hint
            if (!isNetFired) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 145.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LeatherDark.copy(alpha = 0.85f))
                        .border(1.dp, VintageBrass, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🎯 點一下動物發射「夢幻泡泡網」，開啟雙手訓練！",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- Full Screen Split Blind-Touch Overlay for 4-year-olds (左右全螢幕大面積盲按與防掌壓) ---
        if (isNetFired && !showSuccessDialog) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp) // 24dp Tablet Edge Palm Rejection
            ) {
                // Left Half: Blind Palm / Thumb Holding Zone
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    captureEngine.setLeftHandStabilizing(true)
                                    qteState = captureEngine.getCurrentState()
                                    tryAwaitRelease()
                                    captureEngine.setLeftHandStabilizing(false)
                                    qteState = captureEngine.getCurrentState()
                                }
                            )
                        }
                ) {
                    if (qteState.isLeftHandStabilizing) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(SunsetAmber.copy(alpha = 0.45f), Color.Transparent)))
                        )
                    }
                }

                // Right Half: Blind Tapping Zone (隨手拍打即吹泡泡收網)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    captureEngine.onRightHandTap()
                                    qteState = captureEngine.getCurrentState()
                                    rightTapParticleCount++
                                    if (qteState.isCompleted) showSuccessDialog = true
                                }
                            )
                        }
                ) {
                    // Floating celebratory star particles on tapping
                    if (rightTapParticleCount > 0) {
                        Text(
                            text = if (rightTapParticleCount % 2 == 0) "⭐✨" else "🫧💖",
                            fontSize = 32.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(
                                    x = ((rightTapParticleCount * 29) % 100 - 50).dp,
                                    y = ((rightTapParticleCount * 19) % 80 - 40).dp
                                )
                        )
                    }
                }
            }
        }

        // Bottom Bimanual Early Childhood Controller
        if (isNetFired && !showSuccessDialog && !showEscapeDialog) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.92f)
            ) {
                QTECaptureBar(
                    qteState = qteState,
                    onLeftHandPressChanged = { isHolding ->
                        captureEngine.setLeftHandStabilizing(isHolding)
                        qteState = captureEngine.getCurrentState()
                    },
                    onRightHandTap = {
                        captureEngine.onRightHandTap()
                        qteState = captureEngine.getCurrentState()
                        rightTapParticleCount++
                        if (qteState.isCompleted) showSuccessDialog = true
                    }
                )
            }
        }

        // Victory Dialog with Motor Skill Encouragement
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { /* forced */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "交到好朋友囉！${animal.name}",
                            style = WildTypography.titleLarge.copy(color = BambooGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "太棒了！小動物超級喜歡你！",
                            color = InkBlack,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "雙手協調評級：${qteState.fingerAgilityLevel} · 達成 ${qteState.rightHandTapCount} 次拍手！",
                            color = SunsetAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 小動物悄悄話：${animal.funFact}",
                            color = InkGrey,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { onCaptureSuccess(animal) },
                        colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("領取好朋友貼紙！送入森林樂園 🐾")
                    }
                }
            )
        }

        // Gentle Departure Fallback (Zero Frustration)
        if (showEscapeDialog) {
            AlertDialog(
                onDismissRequest = { /* forced */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Text(
                        text = "🍓 小動物吃飽飽去休息囉～",
                        style = WildTypography.titleLarge.copy(color = LeatherBrown, fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        text = "小動物送你一顆幸運紅草莓：「謝謝你跟我玩～明天見！」",
                        color = InkBlack,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onCaptureFailed,
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("收下禮物！再找下一隻小動物 🌟")
                    }
                }
            )
        }
    }
}
