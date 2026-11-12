package com.game.wildcatcher

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.game.wildcatcher.presentation.ScreenState
import com.game.wildcatcher.presentation.WildViewModel
import com.game.wildcatcher.presentation.component.EcoQuizDialog
import com.game.wildcatcher.presentation.component.PandaAssistantOverlay
import com.game.wildcatcher.presentation.screen.*
import com.game.wildcatcher.presentation.theme.EyeCareOverlayColor
import com.game.wildcatcher.presentation.theme.WildCatcherTheme

class MainActivity : ComponentActivity() {
    private val viewModel: WildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen awake while playing in the jungle
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Enforce Immersive Fullscreen Mode for Android Tablet Gaming
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val warmFilterActive by viewModel.playTimeManager.isWarmModeActive.collectAsState()

            WildCatcherTheme(warmFilterActive = warmFilterActive) {
                MainGameContainer(viewModel = viewModel, warmFilterActive = warmFilterActive)
            }
        }
    }
}

@Composable
fun MainGameContainer(
    viewModel: WildViewModel,
    warmFilterActive: Boolean
) {
    val screen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()
    val allAnimals by viewModel.allAnimals.collectAsState()
    val shelteredAnimals by viewModel.shelteredAnimals.collectAsState()
    val shelterZones by viewModel.shelterZones.collectAsState()
    val environment by viewModel.currentEnvironment.collectAsState()
    val healthState by viewModel.playTimeManager.healthState.collectAsState()
    val boboSpeech by viewModel.boboSpeech.collectAsState()
    val activeQuiz by viewModel.activeQuiz.collectAsState()
    val targetAnimal by viewModel.currentTargetAnimal.collectAsState()

    val revenueReport = viewModel.shelterEngine.calculateHourlyRevenue(shelterZones, shelteredAnimals)

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Screen Routing
        when (screen) {
            ScreenState.MAIN_MENU -> {
                MainMenuScreen(
                    profile = profile ?: com.game.wildcatcher.data.model.PlayerProfile(),
                    capturedAnimalsCount = shelteredAnimals.size,
                    onStartExpedition = { viewModel.startRandomExpedition() },
                    onOpenJungleExplore = { viewModel.navigateTo(ScreenState.JUNGLE_EXPLORE) },
                    onOpenShelter = { viewModel.navigateTo(ScreenState.SHELTER_PARK) },
                    onOpenJournal = { viewModel.navigateTo(ScreenState.JOURNAL_ENCYCLOPEDIA) },
                    onOpenShop = { viewModel.navigateTo(ScreenState.UPGRADE_SHOP) },
                    onTriggerQuiz = { viewModel.triggerEcoQuiz() },
                    onSimulateThirtyMinutes = { viewModel.simulateThirtyMinutes() },
                    onOpenParentDashboard = { viewModel.navigateTo(ScreenState.PARENT_DASHBOARD) },
                    onSpeakVoice = { text -> viewModel.ttsManager.speakCustomText(text) }
                )
            }
            ScreenState.JUNGLE_EXPLORE -> {
                JungleExploreScreen(
                    onBackToMenu = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
                )
            }
            ScreenState.CAPTURE_BATTLE -> {
                targetAnimal?.let { animal ->
                    CaptureBattleScreen(
                        animal = animal,
                        consecutiveFailures = profile?.consecutiveFailures ?: 0,
                        gunPowerLevel = profile?.pandaGunPowerLevel ?: 1,
                        incenseAmmoCount = profile?.bambooIncenseAmmo ?: 30,
                        environment = environment,
                        onCaptureSuccess = { viewModel.onCaptureSuccess(it) },
                        onCaptureFailed = { viewModel.onCaptureFailed() },
                        onBackToMenu = { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                        onSpeakVoice = { text -> viewModel.ttsManager.speakCustomText(text) },
                        onLeftHandHaptic = { viewModel.hapticManager.playLeftHandHeartbeatPulse() },
                        onRightHandHaptic = {
                            viewModel.hapticManager.playRightHandSweetSpotClick()
                            viewModel.soundManager.playSound(com.game.wildcatcher.audio.SoundEffect.SWEET_SPOT_HIT)
                        }
                    )
                } ?: run {
                    viewModel.navigateTo(ScreenState.MAIN_MENU)
                }
            }
            ScreenState.SHELTER_PARK -> {
                ShelterManagementScreen(
                    zones = shelterZones,
                    shelteredAnimals = shelteredAnimals,
                    revenueReport = revenueReport,
                    playerCoins = profile?.coins ?: 0L,
                    onCollectRevenue = { viewModel.collectShelterRevenue(it) },
                    onUnlockZone = { id, cost -> viewModel.unlockZone(id, cost) },
                    onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                    onSpeakVoice = { text -> viewModel.ttsManager.speakCustomText(text) }
                )
            }
            ScreenState.JOURNAL_ENCYCLOPEDIA -> {
                AdventureJournalScreen(
                    animals = allAnimals,
                    onPlayAnimalSound = { _ ->
                        viewModel.soundManager.playSound(com.game.wildcatcher.audio.SoundEffect.SWEET_SPOT_HIT)
                    },
                    onSpeakGuide = { animal ->
                        viewModel.speakAnimalGuide(animal)
                    },
                    onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
                )
            }
            ScreenState.UPGRADE_SHOP -> {
                UpgradeShopScreen(
                    profile = profile ?: com.game.wildcatcher.data.model.PlayerProfile(),
                    onUpgradeItem = { id, cost -> viewModel.upgradeItem(id, cost) },
                    onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                    onSpeakVoice = { text -> viewModel.ttsManager.speakCustomText(text) }
                )
            }
            ScreenState.PARENT_DASHBOARD -> {
                val threshold by viewModel.restThresholdMinutes.collectAsState()
                ParentDashboardScreen(
                    currentRestThresholdMinutes = threshold,
                    onRestThresholdChanged = { viewModel.setRestThreshold(it) },
                    totalPlayMinutes = (profile?.totalActiveSeconds ?: 0L) / 60,
                    consecutiveCaptures = profile?.totalCaptures ?: 0,
                    onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
                )
            }
        }

        // Panda Assistant Speech Balloon & Rest Time Alert
        PandaAssistantOverlay(
            healthState = healthState,
            speechBubbleText = boboSpeech,
            onDismissAlert = { activateWarm ->
                viewModel.playTimeManager.dismissRestAlert(activateWarm)
            },
            onRestNow = {
                viewModel.playTimeManager.resetTimerAfterRest()
                viewModel.navigateTo(ScreenState.MAIN_MENU)
            }
        )

        // Eco Science Quiz Dialog (Voice-first for 4yo)
        activeQuiz?.let { quiz ->
            EcoQuizDialog(
                quiz = quiz,
                onAnswerSubmitted = { correct, reward ->
                    viewModel.onQuizAnswered(correct, reward)
                },
                onDismiss = { viewModel.dismissQuiz() },
                onSpeakVoice = { text -> viewModel.ttsManager.speakCustomText(text) }
            )
        }

        // Warm Eye-Care Filter Overlay
        if (warmFilterActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EyeCareOverlayColor)
            )
        }
    }
}
