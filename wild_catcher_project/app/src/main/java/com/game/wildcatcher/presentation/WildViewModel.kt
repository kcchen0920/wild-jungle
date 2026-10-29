package com.game.wildcatcher.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.game.wildcatcher.audio.SoundEffect
import com.game.wildcatcher.audio.SoundManager
import com.game.wildcatcher.data.WildDatabase
import com.game.wildcatcher.data.model.*
import com.game.wildcatcher.data.repository.WildRepository
import com.game.wildcatcher.domain.health.HealthAlertState
import com.game.wildcatcher.domain.health.PlayTimeManager
import com.game.wildcatcher.domain.shelter.ShelterEconomyEngine
import com.game.wildcatcher.domain.weather.EnvironmentState
import com.game.wildcatcher.domain.weather.WeatherDayNightEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenState {
    MAIN_MENU,
    CAPTURE_BATTLE,
    SHELTER_PARK,
    JOURNAL_ENCYCLOPEDIA,
    UPGRADE_SHOP
}

class WildViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WildDatabase.getDatabase(application)
    val repository = WildRepository(
        db.animalDao(),
        db.playerDao(),
        db.shelterDao(),
        db.quizDao()
    )

    val soundManager = SoundManager(application)
    val playTimeManager = PlayTimeManager(thresholdSeconds = 30 * 60L)
    val weatherEngine = WeatherDayNightEngine()
    val shelterEngine = ShelterEconomyEngine()

    private val _currentScreen = MutableStateFlow(ScreenState.MAIN_MENU)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _currentTargetAnimal = MutableStateFlow<AnimalEntity?>(null)
    val currentTargetAnimal: StateFlow<AnimalEntity?> = _currentTargetAnimal.asStateFlow()

    private val _currentEnvironment = MutableStateFlow(EnvironmentState())
    val currentEnvironment: StateFlow<EnvironmentState> = _currentEnvironment.asStateFlow()

    private val _activeQuiz = MutableStateFlow<EcoQuiz?>(null)
    val activeQuiz: StateFlow<EcoQuiz?> = _activeQuiz.asStateFlow()

    private val _boboSpeech = MutableStateFlow<String?>("歡迎來到野性冒險世界！今天想先探索哪座自然園區呢？")
    val boboSpeech: StateFlow<String?> = _boboSpeech.asStateFlow()

    val allAnimals = repository.allAnimals.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val shelteredAnimals = repository.shelterAnimals.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val shelterZones = repository.allZones.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val playerProfile = repository.playerProfile.stateIn(viewModelScope, SharingStarted.Lazily, PlayerProfile())

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        // Ticking loop for PlayTime, Weather, and Background updates
        viewModelScope.launch {
            var seconds = 0L
            while (true) {
                delay(1000)
                seconds += 1
                playTimeManager.onTick(1L)
                repository.incrementPlayTime(1L)
                _currentEnvironment.value = weatherEngine.getEnvironmentState(seconds)
            }
        }
    }

    fun navigateTo(screen: ScreenState) {
        soundManager.playSound(SoundEffect.NOTEBOOK_PAGE_FLIP)
        _currentScreen.value = screen
    }

    fun startRandomExpedition() {
        val list = allAnimals.value
        val target = list.shuffled().firstOrNull() ?: WildRepository.FULL_32_REALISTIC_ANIMALS[0]
        _currentTargetAnimal.value = target
        navigateTo(ScreenState.CAPTURE_BATTLE)
    }

    fun onCaptureSuccess(animal: AnimalEntity) {
        soundManager.playSound(SoundEffect.NET_CATCH_SUCCESS)
        viewModelScope.launch {
            repository.markCaptured(animal.id)
            repository.addCoins(animal.coinYieldPerHour * 2L)
            repository.updateFailures(0)
            _boboSpeech.value = "哇！成功收納了珍貴的${animal.name}！庇護所的遊客一定會超高興的！"
            navigateTo(ScreenState.MAIN_MENU)
        }
    }

    fun onCaptureFailed() {
        soundManager.playSound(SoundEffect.ANIMAL_ESCAPE)
        viewModelScope.launch {
            val curFailures = playerProfile.value?.consecutiveFailures ?: 0
            repository.updateFailures(curFailures + 1)
            _boboSpeech.value = "別灰心！波波已經微調了瞄準阻尼，下次甜蜜點會更寬大好抓！"
            navigateTo(ScreenState.MAIN_MENU)
        }
    }

    fun triggerEcoQuiz() {
        viewModelScope.launch {
            _activeQuiz.value = repository.getRandomQuiz()
        }
    }

    fun dismissQuiz() {
        _activeQuiz.value = null
    }

    fun onQuizAnswered(isCorrect: Boolean, rewardCoins: Int) {
        if (isCorrect) {
            soundManager.playSound(SoundEffect.COIN_REWARD)
            viewModelScope.launch {
                repository.addCoins(rewardCoins.toLong())
                _boboSpeech.value = "答對了！恭喜獲得 $rewardCoins 枚竹幣！大自然知識又增加了！"
            }
        } else {
            _boboSpeech.value = "沒關係，大自然充滿奧秘，多看圖鑑就能學到更多！"
        }
    }

    fun collectShelterRevenue(amount: Long) {
        soundManager.playSound(SoundEffect.COIN_REWARD)
        viewModelScope.launch {
            repository.addCoins(amount)
            _boboSpeech.value = "門票收益已存入背包，可以用來升級熊貓槍囉！"
        }
    }

    fun unlockZone(zoneId: String, cost: Long) {
        viewModelScope.launch {
            val prof = playerProfile.value ?: return@launch
            if (prof.coins >= cost) {
                repository.addCoins(-cost)
                repository.unlockZone(zoneId)
                soundManager.playSound(SoundEffect.WOOD_CLICK)
                _boboSpeech.value = "全新生態園區已開闢！現在可以安置更多動物了！"
            }
        }
    }

    fun upgradeItem(itemId: String, cost: Long) {
        viewModelScope.launch {
            val prof = playerProfile.value ?: return@launch
            if (prof.coins >= cost) {
                repository.addCoins(-cost)
                soundManager.playSound(SoundEffect.WOOD_CLICK)
                when (itemId) {
                    "gun_power" -> repository.updateProfile(prof.copy(coins = prof.coins - cost, pandaGunPowerLevel = prof.pandaGunPowerLevel + 1))
                    "ammo_capacity" -> repository.updateProfile(prof.copy(coins = prof.coins - cost, maxAmmoCapacity = prof.maxAmmoCapacity + 50, bubbleAmmoCount = prof.bubbleAmmoCount + 50))
                    "net_radius" -> repository.updateProfile(prof.copy(coins = prof.coins - cost, bubbleNetRadiusLevel = prof.bubbleNetRadiusLevel + 1))
                    "safari_vehicle" -> repository.updateProfile(prof.copy(coins = prof.coins - cost, vehicleLevel = prof.vehicleLevel + 1))
                }
                _boboSpeech.value = "裝備已升級！探索效率大幅提升！"
            }
        }
    }

    fun simulateThirtyMinutes() {
        playTimeManager.fastForwardToThirtyMinutes()
        soundManager.playSound(SoundEffect.BOBO_ALERT_VOICE)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
