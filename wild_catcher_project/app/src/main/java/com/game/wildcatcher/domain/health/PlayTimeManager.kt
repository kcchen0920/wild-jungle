package com.game.wildcatcher.domain.health

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class HealthAlertState {
    object Normal : HealthAlertState()
    data class NearThreshold(val minutesRemaining: Int) : HealthAlertState()
    data class RestTimeTriggered(
        val message: String = "太陽要下山囉，讓眼睛休息一下，明天再來探索！",
        val warmFilterActive: Boolean = true
    ) : HealthAlertState()
}

class PlayTimeManager(
    private val thresholdSeconds: Long = 30 * 60L // 30 minutes
) {
    private val _activeSeconds = MutableStateFlow(0L)
    val activeSeconds: StateFlow<Long> = _activeSeconds.asStateFlow()

    private val _healthState = MutableStateFlow<HealthAlertState>(HealthAlertState.Normal)
    val healthState: StateFlow<HealthAlertState> = _healthState.asStateFlow()

    private val _isWarmModeActive = MutableStateFlow(false)
    val isWarmModeActive: StateFlow<Boolean> = _isWarmModeActive.asStateFlow()

    fun onTick(deltaSeconds: Long) {
        val newSec = _activeSeconds.value + deltaSeconds
        _activeSeconds.value = newSec

        val remain = thresholdSeconds - newSec
        when {
            newSec >= thresholdSeconds -> {
                _healthState.value = HealthAlertState.RestTimeTriggered()
                _isWarmModeActive.value = true
            }
            remain <= 5 * 60 -> { // Less than 5 minutes left
                val minutesLeft = (remain / 60).toInt().coerceAtLeast(1)
                _healthState.value = HealthAlertState.NearThreshold(minutesLeft)
            }
            else -> {
                _healthState.value = HealthAlertState.Normal
            }
        }
    }

    fun dismissRestAlert(activateWarmMode: Boolean = true) {
        _isWarmModeActive.value = activateWarmMode
        _healthState.value = HealthAlertState.Normal
    }

    fun resetTimerAfterRest() {
        _activeSeconds.value = 0L
        _healthState.value = HealthAlertState.Normal
        _isWarmModeActive.value = false
    }

    /**
     * For demonstration & immediate verification
     */
    fun fastForwardToThirtyMinutes() {
        _activeSeconds.value = thresholdSeconds
        _healthState.value = HealthAlertState.RestTimeTriggered()
        _isWarmModeActive.value = true
    }
}
