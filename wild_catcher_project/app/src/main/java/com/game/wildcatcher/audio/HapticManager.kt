package com.game.wildcatcher.audio

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

/**
 * 雙通道分立微體感震動引擎 (Dual-Channel Haptic Feedback Engine)
 * - 左手頻道：低頻柔和律動 (模擬安撫心跳)，回饋左手指力持續施力恰當
 * - 右手頻道：清脆短促點擊 (模擬琴弦撥動)，回饋甜蜜點節奏打擊
 */
class HapticManager(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var isHapticEnabled = true

    /**
     * 左手持續按壓：低頻微幅心跳震動
     */
    fun playLeftHandHeartbeatPulse() {
        if (!isHapticEnabled || vibrator == null || !vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Gentle low amplitude pulse (amplitude 40/255, duration 25ms)
                val effect = VibrationEffect.createOneShot(25, 40)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (e: Exception) {
            Log.e("HapticManager", "Error triggering left-hand heartbeat haptic", e)
        }
    }

    /**
     * 右手甜蜜點命中：清脆短促回饋
     */
    fun playRightHandSweetSpotClick() {
        if (!isHapticEnabled || vibrator == null || !vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                vibrator.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(35, 180)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(35)
            }
        } catch (e: Exception) {
            Log.e("HapticManager", "Error triggering right-hand sweet-spot haptic", e)
        }
    }

    /**
     * 動物掙脫警示震動 (雙短震)
     */
    fun playEscapeWarning() {
        if (!isHapticEnabled || vibrator == null || !vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 50, 60, 50)
                val amplitudes = intArrayOf(0, 150, 0, 150)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 50, 60, 50), -1)
            }
        } catch (e: Exception) {
            Log.e("HapticManager", "Error triggering escape warning haptic", e)
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        isHapticEnabled = enabled
    }
}
