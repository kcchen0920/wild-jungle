package com.game.wildcatcher.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showEscapeDialog by remember { mutableStateOf(false) }

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
                        if (qteState.isCompleted) showSuccessDialog = true
                        if (qteState.isEscaped) showEscapeDialog = true
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
                        Text("🌟", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "捕獲成功！${animal.name}",
                            style = WildTypography.titleLarge.copy(color = BambooGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "太厲害了！你的左右手配合得天衣無縫！",
                            color = InkBlack,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "雙手協調評級：${qteState.fingerAgilityLevel} · 達成 ${qteState.rightHandTapCount} 次靈活點擊！",
                            color = SunsetAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 生態小百科：${animal.funFact}",
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
                        Text("送入庇護所，領取榮譽徽章！")
                    }
                }
            )
        }

        // Gentle Escape Fallback
        if (showEscapeDialog) {
            AlertDialog(
                onDismissRequest = { /* forced */ },
                containerColor = ParchmentBg,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Text(
                        text = "🐾 動物跑去休息囉～",
                        style = WildTypography.titleLarge.copy(color = LeatherBrown, fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        text = "波波為你拍拍手：「小手指頭動得非常好喔！下次我們左手按得更穩一點，一定能順利收網！」",
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
                        Text("再來一次小手指體操！")
                    }
                }
            )
        }
    }
}
