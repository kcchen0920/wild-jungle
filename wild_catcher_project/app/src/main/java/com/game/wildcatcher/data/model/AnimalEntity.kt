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
    val bopomofoName: String = "",
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
) {
    fun getFullBopomofo(): String {
        if (bopomofoName.isNotEmpty()) return bopomofoName
        return when (id) {
            "formosan_black_bear" -> "ㄊㄞˊ ㄨㄢ ㄏㄟ ㄒㄩㄥˊ"
            "leopard_cat" -> "ㄕˊ ㄏㄨˇ"
            "formosan_sambar" -> "ㄊㄞˊ ㄨㄢ ㄕㄨㄟˇ ㄌㄨˋ"
            "crested_serpent_eagle" -> "ㄉㄚˋ ㄍㄨㄢ ㄐㄧㄡˋ"
            "collared_scops_owl" -> "ㄌㄧㄥˇ ㄐㄧㄠˇ ㄒㄧㄠ"
            "chinese_pangolin" -> "ㄔㄨㄢ ㄕㄢ ㄐㄧㄚˇ"
            "golden_snub_nosed_monkey" -> "ㄔㄨㄢ ㄐㄧㄣ ㄙ ㄏㄡˊ"
            "formosan_landlocked_salmon" -> "ㄧㄥ ㄏㄨㄚ ㄍㄡ ㄨㄣˇ ㄍㄨㄟ"
            "indochinese_tiger" -> "ㄧㄣˋ ㄓ ㄏㄨˇ"
            "malayan_tapir" -> "ㄇㄚˇ ㄌㄞˊ ㄇㄛˋ"
            "toco_toucan" -> "ㄊㄨㄛ ㄍㄜ ㄐㄩˋ ㄗㄨㄟˇ ㄋㄧㄠˇ"
            "three_toed_sloth" -> "ㄙㄢ ㄓˇ ㄕㄨˋ ㄌㄢˇ"
            "panther_chameleon" -> "ㄑㄧ ㄘㄞˇ ㄅㄧㄢˋ ㄙㄜˋ ㄌㄨㄥˊ"
            "poison_dart_frog" -> "ㄧㄡ ㄌㄧㄥˊ ㄐㄧㄢˋ ㄉㄨˊ ㄨㄚ"
            "rhinoceros_hornbill" -> "ㄇㄚˇ ㄌㄞˊ ㄒㄧ ㄋㄧㄠˇ"
            "bornean_orangutan" -> "ㄆㄛˊ ㄌㄨㄛˊ ㄓㄡ ㄏㄨㄥˊ ㄇㄠˊ ㄒㄧㄥ ㄒㄧㄥ"
            "green_iguana" -> "ㄇㄟˇ ㄓㄡ ㄌㄧㄝˋ ㄒㄧ"
            "ruffed_lemur" -> "ㄅㄞˊ ㄐㄧㄥˇ ㄏㄨˊ ㄇㄡˊ"
            "formosan_macaque" -> "ㄊㄞˊ ㄨㄢ ㄇㄧˊ ㄏㄡˊ"
            "swinhoe_pheasant" -> "ㄌㄢˊ ㄈㄨˋ ㄒㄧㄢˊ"
            "mikado_pheasant" -> "ㄉㄧˋ ㄓˊ"
            "moltrecht_tree_frog" -> "ㄇㄛˋ ㄕˋ ㄕㄨˋ ㄨㄚ"
            "eurasian_otter" -> "ㄡ ㄧㄚˋ ㄕㄨㄟˇ ㄊㄚˋ"
            "asian_elephant" -> "ㄧㄚˋ ㄓㄡ ㄒㄧㄤˋ"
            "clouded_leopard" -> "ㄩㄣˊ ㄅㄠˋ"
            "cheetah" -> "ㄌㄧㄝˋ ㄅㄠˋ"
            "andean_condor" -> "ㄢ ㄉㄧˋ ㄙ ㄕㄣˊ ㄧㄥ"
            "snow_leopard" -> "ㄒㄩㄝˇ ㄅㄠˋ"
            "serval" -> "ㄙㄡˇ ㄇㄠ"
            "binturong" -> "ㄒㄩㄥˊ ㄌㄧˊ"
            "proboscis_monkey" -> "ㄔㄤˊ ㄅㄧˊ ㄏㄡˊ"
            "sword_billed_hummingbird" -> "ㄉㄠ ㄗㄨㄟˇ ㄈㄥ ㄋㄧㄠˇ"
            else -> ""
        }
    }
}
