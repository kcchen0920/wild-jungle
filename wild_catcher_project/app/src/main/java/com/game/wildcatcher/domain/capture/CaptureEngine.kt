package com.game.wildcatcher.domain.capture

import com.game.wildcatcher.data.model.AnimalEntity
import kotlin.math.sin

data class QTEState(
    val currentCursorPos: Float = 0.5f,     // 0.0f to 1.0f on the bar
    val sweetSpotCenter: Float = 0.5f,      // Center of sweet spot
    val sweetSpotWidth: Float = 0.28f,      // Toddler-friendly wide sweet spot
    val captureProgress: Float = 0.0f,      // 0.0f to 1.0f (reaches 1.0 = success)
    val animalTension: Float = 0.0f,        // 0.0f to 1.0f (reaches 1.0 = escape)
    val isInSweetSpot: Boolean = false,
    val isCompleted: Boolean = false,
    val isEscaped: Boolean = false,
    val comboCount: Int = 0,
    val ddaToleranceBonus: Float = 0.0f,

    // --- Toddler Fine Motor & Bimanual Coordination Metrics ---
    val isLeftHandStabilizing: Boolean = false, // 左手持續穩定按壓 (指力本體感覺訓練)
    val leftHandHoldSeconds: Float = 0.0f,      // 左手維持施力秒數
    val rightHandTapCount: Int = 0,             // 右手靈活節奏連擊次數
    val bimanualSyncScore: Float = 0.0f,        // 雙手協同評分 (0.0f ~ 1.0f)
    val fingerAgilityLevel: String = "探索小萌手", // 幼兒指肌成長評級
    val squishScaleX: Float = 1.0f,             // 物理彈性壓縮形變
    val squishScaleY: Float = 1.0f
)

class CaptureEngine(
    val targetAnimal: AnimalEntity,
    private val consecutiveFailures: Int,
    private val gunPowerLevel: Int = 1
) {
    private var state = QTEState()
    private var internalTime = 0.0f

    init {
        // Generous base width and DDA for early childhood confidence building
        val ddaBonus = when {
            consecutiveFailures >= 3 -> 0.22f // Extra generous for toddlers
            consecutiveFailures >= 2 -> 0.15f
            consecutiveFailures >= 1 -> 0.08f
            else -> 0.04f                     // Default toddler comfort margin
        }
        val calculatedWidth = (targetAnimal.sweetSpotWidth + ddaBonus).coerceIn(0.28f, 0.70f)
        state = state.copy(
            sweetSpotCenter = 0.5f,
            sweetSpotWidth = calculatedWidth,
            ddaToleranceBonus = ddaBonus
        )
    }

    fun getCurrentState(): QTEState = state

    /**
     * Physics and QTE tick loop
     */
    fun tick(deltaSec: Float) {
        if (state.isCompleted || state.isEscaped) return

        internalTime += deltaSec

        // Left hand stabilization dampens erratic movement by 50%
        // (Encourages toddler to use non-dominant left hand to stabilize)
        val leftHandDamping = if (state.isLeftHandStabilizing) 0.50f else 1.0f
        val newHoldSec = if (state.isLeftHandStabilizing) state.leftHandHoldSeconds + deltaSec else 0.0f

        val ddaVelocityDamping = if (consecutiveFailures > 0) {
            (1.0f - (consecutiveFailures * 0.15f)).coerceAtLeast(0.4f)
        } else {
            0.85f // Gentle baseline speed tailored for toddler reaction times
        }

        val baseSpeed = targetAnimal.struggleVelocity * leftHandDamping * ddaVelocityDamping

        // Harmonic gentle floating motion (simulating peaceful bubbles and wildlife bobbing)
        val osc1 = sin(internalTime * baseSpeed * 2.8f)
        val osc2 = sin(internalTime * baseSpeed * 1.2f + 0.8f) * 0.3f
        val rawPos = 0.5f + (osc1 + osc2) * 0.40f
        val newCursorPos = rawPos.coerceIn(0.04f, 0.96f)

        // Sweet spot detection
        val halfW = state.sweetSpotWidth / 2.0f
        val minSweet = state.sweetSpotCenter - halfW
        val maxSweet = state.sweetSpotCenter + halfW
        val inSweet = newCursorPos in minSweet..maxSweet

        // Passive tension decay: Left-hand holding prevents rapid escape
        var newTension = state.animalTension
        if (!inSweet) {
            val tensionRate = if (state.isLeftHandStabilizing) 0.02f else 0.04f
            newTension = (newTension + deltaSec * tensionRate).coerceIn(0.0f, 1.0f)
        } else {
            newTension = (newTension - deltaSec * 0.06f).coerceIn(0.0f, 1.0f)
        }

        // Bimanual Synchrony calculation
        val syncScore = if (state.isLeftHandStabilizing && state.rightHandTapCount > 0) {
            (newHoldSec * 0.2f + state.rightHandTapCount * 0.08f).coerceIn(0.0f, 1.0f)
        } else {
            (state.bimanualSyncScore - deltaSec * 0.05f).coerceAtLeast(0.0f)
        }

        val agilityTitle = when {
            syncScore >= 0.8f -> "🌟 雙手協同大師"
            syncScore >= 0.5f -> "✨ 靈活指尖探險家"
            syncScore >= 0.25f -> "🌱 雙手合力小能手"
            else -> "🐾 探索小萌手"
        }

        // Return gentle natural squish back to 1.0
        val targetScaleX = 1.0f
        val targetScaleY = 1.0f
        val currentScaleX = state.squishScaleX + (targetScaleX - state.squishScaleX) * 0.15f
        val currentScaleY = state.squishScaleY + (targetScaleY - state.squishScaleY) * 0.15f

        val escaped = newTension >= 1.0f

        state = state.copy(
            currentCursorPos = newCursorPos,
            isInSweetSpot = inSweet,
            animalTension = newTension,
            leftHandHoldSeconds = newHoldSec,
            bimanualSyncScore = syncScore,
            fingerAgilityLevel = agilityTitle,
            squishScaleX = currentScaleX,
            squishScaleY = currentScaleY,
            isEscaped = escaped
        )
    }

    /**
     * Left Hand: Constant Pressure Holding (訓練左手指肌與本體感覺穩定施力)
     */
    fun setLeftHandStabilizing(isHolding: Boolean) {
        state = state.copy(
            isLeftHandStabilizing = isHolding,
            // Visual tactile squish feedback on press
            squishScaleX = if (isHolding) 1.08f else 1.0f,
            squishScaleY = if (isHolding) 0.92f else 1.0f
        )
    }

    /**
     * Right Hand: Rhythmic Precision Tapping (右手節奏點擊與微調靈活性)
     */
    fun onRightHandTap() {
        if (state.isCompleted || state.isEscaped) return

        val newTaps = state.rightHandTapCount + 1

        if (state.isInSweetSpot) {
            // Dual-hand synergy bonus: When left hand is stabilizing, capture power is boosted +40%!
            val bimanualBonus = if (state.isLeftHandStabilizing) 1.40f else 1.0f
            val powerBonus = (1.0f + (gunPowerLevel - 1) * 0.15f) * bimanualBonus
            val baseGain = 0.14f * powerBonus
            val newProgress = (state.captureProgress + baseGain).coerceIn(0.0f, 1.0f)
            val newCombo = state.comboCount + 1
            val newTension = (state.animalTension - 0.10f).coerceAtLeast(0.0f)

            state = state.copy(
                captureProgress = newProgress,
                comboCount = newCombo,
                rightHandTapCount = newTaps,
                animalTension = newTension,
                squishScaleX = 0.88f, // Spring bouncy squish on hit
                squishScaleY = 1.15f,
                isCompleted = newProgress >= 1.0f
            )
        } else {
            // Gentle feedback: small tension increase without harsh penalties for kids
            val mildTension = (state.animalTension + 0.05f).coerceIn(0.0f, 1.0f)
            state = state.copy(
                rightHandTapCount = newTaps,
                comboCount = 0,
                animalTension = mildTension,
                squishScaleX = 1.05f,
                squishScaleY = 0.95f,
                isEscaped = mildTension >= 1.0f
            )
        }
    }
}
