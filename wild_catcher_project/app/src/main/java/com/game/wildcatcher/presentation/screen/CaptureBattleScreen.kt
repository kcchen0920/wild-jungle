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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.AnimalEntity
import com.game.wildcatcher.domain.capture.CaptureEngine
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
    isFromJungleMap: Boolean = false,
    onCaptureSuccess: (AnimalEntity) -> Unit,
    onCaptureSuccessNextAnimal: (AnimalEntity) -> Unit = {},
    onCaptureSuccessGoToShelter: (AnimalEntity) -> Unit = {},
    onCaptureFailed: () -> Unit,
    onBackToMenu: () -> Unit,
    onSpeakVoice: (String) -> Unit = {},
    onLeftHandHaptic: () -> Unit = {},
    onRightHandHaptic: () -> Unit = {},
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

    // Auto-launch bubble net after 1.6s if toddler hasn't tapped yet (Zero-Frustration Entry)
    LaunchedEffect(animal.id) {
        onSpeakVoice("哇！發現可愛的${animal.name}！我們一起吹大泡泡跟牠做朋友吧！")
        delay(1600)
        if (!isNetFired) {
            isNetFired = true
        }
    }

    // Toddler voice guidance when bubble net activates
    LaunchedEffect(isNetFired) {
        if (isNetFired) {
            onSpeakVoice("伸出左手按住左邊抱抱，右手拍拍右邊吹大泡泡！")
        }
    }

    // Periodic gentle heartbeat haptic while toddler holds left hand
    LaunchedEffect(qteState.isLeftHandStabilizing, isNetFired, showSuccessDialog) {
        while (isNetFired && qteState.isLeftHandStabilizing && !showSuccessDialog) {
            onLeftHandHaptic()
            delay(450)
        }
    }

    LaunchedEffect(qteState.isCompleted) {
        if (qteState.isCompleted) {
            showSuccessDialog = true
            onRightHandHaptic()
            onSpeakVoice("太棒了！你成功跟${animal.name}成為好朋友囉！")
        }
    }

    // Dynamic directional light & animations
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

    val floatBounce by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatBounce"
    )

    // Animated Ghost Hand Guide pulse for 4yo visual imitation
    val handCoachPulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handCoachPulse"
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

    val handleRightTap = {
        if (!isNetFired) {
            isNetFired = true
        }
        captureEngine.onRightHandTap()
        qteState = captureEngine.getCurrentState()
        rightTapParticleCount++
        onRightHandHaptic()
        if (qteState.isCompleted) showSuccessDialog = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = when (environment.timeOfDay.isNight) {
                        true -> listOf(Color(0xFF0F1B29), Color(0xFF1E2D3D))
                        false -> listOf(Color(0xFF1E4D3B), Color(0xFF133328))
                    }
                )
            )
            // Full-screen tap anywhere to launch net if not fired yet
            .pointerInput(isNetFired) {
                if (!isNetFired) {
                    detectTapGestures(onTap = { isNetFired = true })
                }
            }
            .padding(14.dp)
    ) {
        // =========================================================================
        // TOP HUD BAR: Giant Home Button + Animal Name with Bopomofo & Voice Button
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Giant 58dp Home Button for 4-year-olds
                Button(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = LeatherDark),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .border(2.5.dp, VintageBrass, RoundedCornerShape(18.dp))
                ) {
                    Text(if (isFromJungleMap) "🚙" else "🏠", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFromJungleMap) "回地圖" else "回首頁",
                        color = BrassHighlight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Animal Name + Bopomofo + Voice Guide Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(LeatherDark.copy(alpha = 0.82f))
                        .border(1.5.dp, ParchmentBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            onSpeakVoice("${animal.name}！${animal.funFact}")
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = animal.name,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${animal.getFullBopomofo()})",
                                color = BrassHighlight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "🏞️ 住在：${animal.habitatName}",
                            color = BambooLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("🔊", fontSize = 24.sp)
                }
            }

            // Cheerful Visual Hint Badge for 4-year-olds
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SunsetAmber.copy(alpha = 0.28f))
                    .border(2.dp, SunsetAmber, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!isNetFired) "👆 點一下小動物吹泡泡！" else "💖 一直拍拍小動物就會抓到喔！",
                    color = BrassHighlight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // =========================================================================
        // CENTER ARENA: Bouncy Animal & Growing Rainbow Bubble Net
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 125.dp),
            contentAlignment = Alignment.Center
        ) {
            val bubbleSize = (240f + qteState.captureProgress * 85f).dp

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = floatBounce.dp)
                    .scale(scaleX = qteState.squishScaleX, scaleY = qteState.squishScaleY)
            ) {
                PbrAnimalRenderer(
                    animal = animal,
                    lightAngleDeg = dynamicLightAngle,
                    modifier = Modifier
                        .size(250.dp)
                        .clickable { handleRightTap() }
                )

                // Growing Rainbow Dream Bubble Net Visual Overlay
                if (isNetFired) {
                    Box(
                        modifier = Modifier
                            .size(bubbleSize)
                            .clip(CircleShape)
                            .border(
                                width = (4f + qteState.captureProgress * 4f).dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        BubbleCyan,
                                         Color(0xFFFF80AB),
                                        BrassHighlight,
                                        BambooLight,
                                        BubbleCyan
                                    )
                                ),
                                shape = CircleShape
                            )
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BubbleCyan.copy(alpha = 0.18f + qteState.captureProgress * 0.15f)
                                    )
                                )
                            )
                    )
                }
            }

            // Pre-launch giant prompt if net hasn't fired yet
            if (!isNetFired) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 135.dp)
                        .scale(handCoachPulse)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BambooGreen)
                        .border(3.dp, BrassHighlight, RoundedCornerShape(20.dp))
                        .padding(horizontal = 22.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "👆 點一下小動物吹出七彩大泡泡！🫧",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // =========================================================================
        // FULL-SCREEN TODDLER-FRIENDLY TAP ZONE (Tap anywhere to pat & catch!)
        // =========================================================================
        if (isNetFired && !showSuccessDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 68.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { handleRightTap() }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (rightTapParticleCount > 0) {
                    Text(
                        text = when (rightTapParticleCount % 3) {
                            0 -> "⭐🫧✨"
                            1 -> "💖🎵🫧"
                            else -> "🌈⭐💖"
                        },
                        fontSize = 44.sp,
                        modifier = Modifier
                            .offset(
                                x = ((rightTapParticleCount * 47) % 180 - 90).dp,
                                y = ((rightTapParticleCount * 31) % 120 - 80).dp
                            )
                    )
                }
            }
        }

        // =========================================================================
        // BOTTOM CONTROLLER BAR
        // =========================================================================
        if (isNetFired && !showSuccessDialog && !showEscapeDialog) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.94f)
            ) {
                QTECaptureBar(
                    qteState = qteState,
                    onLeftHandPressChanged = { isHolding ->
                        captureEngine.setLeftHandStabilizing(isHolding)
                        qteState = captureEngine.getCurrentState()
                        if (isHolding) onLeftHandHaptic()
                    },
                    onRightHandTap = { handleRightTap() }
                )
            }
        }

        // =========================================================================
        // VICTORY CELEBRATION DIALOG (Giant Sticker & Voice Celebration)
        // =========================================================================
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { /* forced */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉🌟", fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "交到好朋友囉！",
                                style = WildTypography.titleLarge.copy(
                                    color = BambooGreen,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                )
                            )
                            Text(
                                text = "${animal.name} (${animal.getFullBopomofo()})",
                                color = LeatherDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "⭐⭐⭐⭐⭐ 太棒了！小動物超級喜歡你！",
                            color = SunsetAmber,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFF9C4))
                                .border(1.5.dp, SunsetAmber, RoundedCornerShape(14.dp))
                                .clickable { onSpeakVoice(animal.funFact) }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🔊 小動物悄悄話：${animal.funFact}",
                                color = InkBlack,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isFromJungleMap) {
                            Button(
                                onClick = { onCaptureSuccess(animal) },
                                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                            ) {
                                Text(
                                    text = "🚙 貼上貼紙！回大森林繼續散步 🐾",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Button(
                                onClick = { onCaptureSuccessNextAnimal(animal) },
                                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                            ) {
                                Text(
                                    text = "🫧 貼上貼紙！再找下一隻動物玩 🌟",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }

                        Button(
                            onClick = { onCaptureSuccessGoToShelter(animal) },
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                text = "🏡 去動物遊樂園餵牠吃紅蘋果 🍎",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            )
        }

        // =========================================================================
        // ZERO-FRUSTRATION GENTLE DEPARTURE DIALOG
        // =========================================================================
        if (showEscapeDialog) {
            AlertDialog(
                onDismissRequest = { /* forced */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "🍓 小動物吃飽飽去睡午覺囉～",
                        style = WildTypography.titleLarge.copy(
                            color = LeatherBrown,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                    )
                },
                text = {
                    Text(
                        text = "小動物送你一顆甜草莓：「謝謝你陪我玩～我們待會見喔！」",
                        color = InkBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onCaptureFailed,
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Text(
                            text = "🍓 收下草莓！找下一隻小動物玩 🌟",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            )
        }
    }
}
