package com.game.wildcatcher.domain.capture

import com.game.wildcatcher.data.model.AnimalEntity
import kotlin.math.sin

data class QTEState(
    val currentCursorPos: Float = 0.5f,     // 0.0f to 1.0f on the bar
    val sweetSpotCenter: Float = 0.5f,      // Center of sweet spot
    val sweetSpotWidth: Float = 0.25f,      // Width of sweet spot
    val captureProgress: Float = 0.0f,      // 0.0f to 1.0f (reaches 1.0 = success)
    val animalTension: Float = 0.0f,        // 0.0f to 1.0f (reaches 1.0 = escape)
    val isInSweetSpot: Boolean = false,
    val isCompleted: Boolean = false,
    val isEscaped: Boolean = false,
    val comboCount: Int = 0,
    val ddaToleranceBonus: Float = 0.0f,    // Dynamic Difficulty Adjustment bonus
    val isIncenseDartActive: Boolean = false
)

class CaptureEngine(
    val targetAnimal: AnimalEntity,
    private val consecutiveFailures: Int,
    private val gunPowerLevel: Int = 1
) {
    private var state = QTEState()
    private var internalTime = 0.0f

    init {
        // Dynamic Difficulty Adjustment (DDA):
        // If player previously failed, widen sweet spot and reduce struggle
        val ddaBonus = when {
            consecutiveFailures >= 3 -> 0.18f // +18% wider sweet spot
            consecutiveFailures >= 2 -> 0.12f // +12% wider
            consecutiveFailures >= 1 -> 0.06f // +6% wider
            else -> 0.0f
        }
        val calculatedWidth = (targetAnimal.sweetSpotWidth + ddaBonus).coerceIn(0.20f, 0.65f)
        state = state.copy(
            sweetSpotCenter = 0.5f,
            sweetSpotWidth = calculatedWidth,
            ddaToleranceBonus = ddaBonus
        )
    }

    fun getCurrentState(): QTEState = state

    /**
     * Call on each frame (deltaSec: Float)
     */
    fun tick(deltaSec: Float) {
        if (state.isCompleted || state.isEscaped) return

        internalTime += deltaSec

        // Calming incense slows oscillation down by 40%
        val speedMultiplier = if (state.isIncenseDartActive) 0.6f else 1.0f
        
        // Consecutive failure DDA softens struggle velocity
        val ddaVelocityDamping = if (consecutiveFailures > 0) {
            (1.0f - (consecutiveFailures * 0.12f)).coerceAtLeast(0.5f)
        } else {
            1.0f
        }

        val baseSpeed = targetAnimal.struggleVelocity * speedMultiplier * ddaVelocityDamping
        
        // Multi-harmonic oscillation simulating realistic wildlife resistance
        val osc1 = sin(internalTime * baseSpeed * 3.5f)
        val osc2 = sin(internalTime * baseSpeed * 1.8f + 1.2f) * 0.35f
        val rawPos = 0.5f + (osc1 + osc2) * 0.42f
        val newCursorPos = rawPos.coerceIn(0.02f, 0.98f)

        // Check if cursor is inside sweet spot
        val halfW = state.sweetSpotWidth / 2.0f
        val minSweet = state.sweetSpotCenter - halfW
        val maxSweet = state.sweetSpotCenter + halfW
        val inSweet = newCursorPos in minSweet..maxSweet

        // Passive tension decay if not actively reeling
        var newTension = state.animalTension
        if (!inSweet) {
            newTension = (newTension + deltaSec * 0.05f).coerceIn(0.0f, 1.0f)
        } else {
            newTension = (newTension - deltaSec * 0.04f).coerceIn(0.0f, 1.0f)
        }

        val escaped = newTension >= 1.0f

        state = state.copy(
            currentCursorPos = newCursorPos,
            isInSweetSpot = inSweet,
            animalTension = newTension,
            isEscaped = escaped
        )
    }

    /**
     * User tap or hold in sweet spot
     */
    fun onUserReelAction() {
        if (state.isCompleted || state.isEscaped) return

        if (state.isInSweetSpot) {
            // Success hit in sweet spot
            val powerBonus = 1.0f + (gunPowerLevel - 1) * 0.15f
            val baseGain = 0.12f * powerBonus
            val newProgress = (state.captureProgress + baseGain).coerceIn(0.0f, 1.0f)
            val newCombo = state.comboCount + 1
            val newTension = (state.animalTension - 0.08f).coerceAtLeast(0.0f)

            state = state.copy(
                captureProgress = newProgress,
                comboCount = newCombo,
                animalTension = newTension,
                isCompleted = newProgress >= 1.0f
            )
        } else {
            // Missed sweet spot: animal struggles harder
            val tensionSpike = 0.10f
            val newTension = (state.animalTension + tensionSpike).coerceIn(0.0f, 1.0f)
            state = state.copy(
                comboCount = 0,
                animalTension = newTension,
                isEscaped = newTension >= 1.0f
            )
        }
    }

    /**
     * Fire Bamboo Incense calming dart
     */
    fun applyBambooIncenseDart() {
        state = state.copy(
            isIncenseDartActive = true,
            animalTension = (state.animalTension - 0.25f).coerceAtLeast(0.0f)
        )
    }
}
