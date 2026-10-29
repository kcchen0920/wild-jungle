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
import androidx.compose.ui.draw.shadow
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
    var currentIncenseCount by remember { mutableStateOf(incenseAmmoCount) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showEscapeDialog by remember { mutableStateOf(false) }

    // Dynamic directional light rotation angle for PBR rendering
    val infiniteTransition = rememberInfiniteTransition(label = "pbr_light")
    val dynamicLightAngle by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightAngle"
    )

    // QTE Engine frame loop
    LaunchedEffect(isNetFired, qteState.isCompleted, qteState.isEscaped) {
        if (!isNetFired) return@LaunchedEffect
        while (!qteState.isCompleted && !qteState.isEscaped) {
            delay(16) // ~60 FPS
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
                        false -> listOf(Color(0xFF335C67), Color(0xFF1D3557))
                    }
                )
            )
            .padding(16.dp)
    ) {
        // Top HUD Bar
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
                        .clip(CircleShape)
                        .background(LeatherDark.copy(alpha = 0.7f))
                ) {
                    Text("◀", color = VintageBrass, fontSize = 16.sp)
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

            // Weather & Day/Night Indicator
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(LeatherDark.copy(alpha = 0.75f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌤️ ${environment.timeOfDay.label} · ${environment.weather.label}",
                    color = BrassHighlight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Center Battlefield: Dynamic PBR Realistic Animal Renderer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            PbrAnimalRenderer(
                animal = animal,
                lightAngleDeg = dynamicLightAngle,
                modifier = Modifier
                    .size(280.dp)
                    .clickable {
                        if (!isNetFired) {
                            isNetFired = true
                        }
                    }
            )

            // Fired Dream Bubble Net Visual Effect
            if (isNetFired) {
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .clip(CircleShape)
                        .border(3.dp, BubbleCyan.copy(alpha = 0.7f), CircleShape)
                        .background(BubbleCyan.copy(alpha = 0.12f))
                )
            }

            // Initial Launch Aiming Hint (before first click)
            if (!isNetFired) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LeatherDark.copy(alpha = 0.8f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎯 點擊動物發射「夢幻泡泡網」開始捕捉！",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom QTE Bar (Appears upon net hit)
        if (isNetFired && !showSuccessDialog && !showEscapeDialog) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.85f)
            ) {
                QTECaptureBar(
                    qteState = qteState,
                    onReelTap = {
                        captureEngine.onUserReelAction()
                        qteState = captureEngine.getCurrentState()
                        if (qteState.isCompleted) showSuccessDialog = true
                        if (qteState.isEscaped) showEscapeDialog = true
                    },
                    onUseIncenseDart = {
                        if (currentIncenseCount > 0) {
                            currentIncenseCount -= 1
                            captureEngine.applyBambooIncenseDart()
                            qteState = captureEngine.getCurrentState()
                        }
                    },
                    incenseAmmoCount = currentIncenseCount
                )
            }
        }

        // Victory Dialog: Animal Captured!
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { /* forced action */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "成功捕捉：${animal.name}！",
                            style = WildTypography.titleLarge.copy(color = BambooGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "「太棒了戴夫！這隻${animal.name}已經溫和地被收納進庇護所，每小時將為園區帶來 +${animal.coinYieldPerHour} 竹幣的門票收益！」",
                            color = InkBlack,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 生態小科普：${animal.funFact}",
                            color = InkGrey,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { onCaptureSuccess(animal) },
                        colors = ButtonDefaults.buttonColors(containerColor = BambooGreen)
                    ) {
                        Text("帶回庇護所安置")
                    }
                }
            )
        }

        // Escape Dialog: Gentle fallback (Encouraging and DDA friendly)
        if (showEscapeDialog) {
            AlertDialog(
                onDismissRequest = { /* forced action */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Text(
                        text = "🍃 動物機警地掙脫了...",
                        style = WildTypography.titleLarge.copy(color = LeatherBrown, fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        text = "小熊貓波波安慰道：「沒關係的戴夫！野生動物天生就充滿警覺心～波波已經記錄了牠的動態，下次捕捉甜蜜點會大幅放寬喔！」",
                        color = InkBlack,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onCaptureFailed,
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber)
                    ) {
                        Text("重整旗鼓，再次嘗試")
                    }
                }
            )
        }
    }
}
