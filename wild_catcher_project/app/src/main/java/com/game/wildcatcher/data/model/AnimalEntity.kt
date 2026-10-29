package com.game.wildcatcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Conservation Status Level:
 * GREEN: Least Concern (無危 - 綠燈)
 * YELLOW: Vulnerable / Near Threatened (易危 / 近危 - 黃燈)
 * RED: Endangered / Critically Endangered (瀕危 / 極危 - 紅燈)
 */
enum class ConservationGrade {
    GREEN,
    YELLOW,
    RED
}

enum class HabitatType {
    TROPICAL_RAINFOREST,
    ALPINE_CONIFER,
    CANOPY_AND_SKY,
    RIVER_AND_STREAM,
    BAMBOO_GROVE
}

enum class ActivityCycle {
    DIURNAL,    // 日行性
    NOCTURNAL,  // 夜行性
    CREPUSCULAR // 晨昏性 / 全日
}

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val scientificName: String,
    val habitat: HabitatType,
    val habitatName: String,
    val grade: ConservationGrade,
    val gradeLabel: String,
    val funFact: String,
    val ecologyNote: String,
    val callSoundName: String,
    val baseCaptureDifficulty: Float, // 0.2f (easy) to 0.9f (hard)
    val struggleVelocity: Float,      // QTE marker oscillation speed
    val sweetSpotWidth: Float,        // QTE sweet spot zone width (0.15f to 0.40f)
    val coinYieldPerHour: Int,        // Park visitor revenue per hour
    val unlockLevel: Int,
    val activityCycle: ActivityCycle,
    val preferredWeather: String,     // Sunny, Fog, Rain, All
    val albedoTexture: String,        // 2K/4K Albedo color texture
    val normalMapTexture: String,     // PBR Normal map (rock, bark, fur bump)
    val roughnessMapTexture: String,  // PBR Roughness map for specular highlights
    val isCaptured: Boolean = false,
    val captureCount: Int = 0,
    val isInShelter: Boolean = false
)
