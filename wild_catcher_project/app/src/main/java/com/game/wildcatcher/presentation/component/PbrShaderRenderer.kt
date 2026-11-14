package com.game.wildcatcher.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.game.wildcatcher.data.model.AnimalEntity
import kotlin.math.*

private enum class AnimalArchetype {
    BEAR,
    PANDA,
    CAT,
    LION,
    CANID,
    DEER,
    GIRAFFE,
    PRIMATE,
    SLOTH,
    BIRD_RAPTOR,
    OWL,
    BIRD_TROPICAL,
    BIRD_WATER,
    PENGUIN,
    FROG,
    SALAMANDER,
    REPTILE_CROCODILE,
    REPTILE_LIZARD,
    SNAKE,
    TURTLE,
    FISH,
    ELEPHANT,
    RHINO,
    HIPPO,
    TAPIR,
    PANGOLIN,
    ANTEATER,
    BOAR,
    PLATYPUS,
    BAT,
    KANGAROO,
    SMALL_MAMMAL
}

private data class SpeciesVisualProfile(
    val archetype: AnimalArchetype,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val hasSpots: Boolean = false,
    val hasStripes: Boolean = false,
    val hasVMark: Boolean = false,
    val hasHorns: Boolean = false,
    val hasAntlers: Boolean = false,
    val hasLongBeak: Boolean = false,
    val hasBigTrunk: Boolean = false,
    val hasAxolotlGills: Boolean = false
)

private fun getSpeciesVisualProfile(id: String): SpeciesVisualProfile = when (id) {
    // 1-20 Taiwan & East Asia
    "formosan_black_bear" -> SpeciesVisualProfile(AnimalArchetype.BEAR, Color(0xFF23252B), Color(0xFF3A3E47), Color(0xFFF6F6F2), hasVMark = true)
    "leopard_cat" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFD19A49), Color(0xFFF5E6C8), Color(0xFF3E2411), hasSpots = true)
    "formosan_sambar" -> SpeciesVisualProfile(AnimalArchetype.DEER, Color(0xFF7B5636), Color(0xFFD7BE9E), Color(0xFF4A311C), hasAntlers = true)
    "crested_serpent_eagle" -> SpeciesVisualProfile(AnimalArchetype.BIRD_RAPTOR, Color(0xFF4E342E), Color(0xFFD7CCC8), Color(0xFFFFCA28))
    "collared_scops_owl" -> SpeciesVisualProfile(AnimalArchetype.OWL, Color(0xFF795548), Color(0xFFEFEBE9), Color(0xFFFFB300))
    "chinese_pangolin" -> SpeciesVisualProfile(AnimalArchetype.PANGOLIN, Color(0xFF8D6E63), Color(0xFFD7CCC8), Color(0xFF5D4037))
    "formosan_landlocked_salmon" -> SpeciesVisualProfile(AnimalArchetype.FISH, Color(0xFF546E7A), Color(0xFFCFD8DC), Color(0xFFFF80AB), hasSpots = true)
    "mikado_pheasant" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF1A237E), Color(0xFF3949AB), Color(0xFFE53935))
    "swinhoe_pheasant" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF0D47A1), Color(0xFFFFFFFF), Color(0xFFE53935))
    "formosan_serow" -> SpeciesVisualProfile(AnimalArchetype.DEER, Color(0xFF4E342E), Color(0xFFBCAAA4), Color(0xFF212121), hasHorns = true)
    "formosan_macaque" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFF8D6E63), Color(0xFFFFCCBC), Color(0xFF5D4037))
    "moltrecht_tree_frog" -> SpeciesVisualProfile(AnimalArchetype.FROG, Color(0xFF43A047), Color(0xFFC8E6C9), Color(0xFFFF7043))
    "taipei_tree_frog" -> SpeciesVisualProfile(AnimalArchetype.FROG, Color(0xFF66BB6A), Color(0xFFFFF9C4), Color(0xFF2E7D32))
    "formosan_blue_magpie" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF1E88E5), Color(0xFF1A1A1A), Color(0xFFE53935), hasLongBeak = true)
    "eurasian_otter" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF6D4C41), Color(0xFFD7CCC8), Color(0xFF3E2723))
    "tawny_fish_owl" -> SpeciesVisualProfile(AnimalArchetype.OWL, Color(0xFF8D6E63), Color(0xFFFFE0B2), Color(0xFFFFB300))
    "formosan_wild_boar" -> SpeciesVisualProfile(AnimalArchetype.BOAR, Color(0xFF4E342E), Color(0xFF8D6E63), Color(0xFFF5F5F5))
    "fairy_pitta" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF2E7D32), Color(0xFF29B6F6), Color(0xFFE53935))
    "formosan_salamander" -> SpeciesVisualProfile(AnimalArchetype.SALAMANDER, Color(0xFF5D4037), Color(0xFFA1887F), Color(0xFFFFCA28))
    "swinhoe_tree_lizard" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_LIZARD, Color(0xFF6D4C41), Color(0xFFAED581), Color(0xFFFFB300))

    // 21-40 Asia & Tropical Rainforest
    "golden_snub_nosed_monkey" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFFE67E22), Color(0xFFFDEBD0), Color(0xFF5DADE2))
    "indochinese_tiger" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFE67E22), Color(0xFFFFF3E0), Color(0xFF1C1C1C), hasStripes = true)
    "malayan_tapir" -> SpeciesVisualProfile(AnimalArchetype.TAPIR, Color(0xFF263238), Color(0xFFF5F5F5), Color(0xFF455A64))
    "binturong" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF262626), Color(0xFF757575), Color(0xFFFBC02D))
    "bornean_orangutan" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFFD84315), Color(0xFFFFAB91), Color(0xFF5D4037))
    "proboscis_monkey" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFFD98850), Color(0xFFFAD7A0), Color(0xFFE59866), hasBigTrunk = true)
    "rhinoceros_hornbill" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF212121), Color(0xFFF5F5F5), Color(0xFFFF6F00), hasLongBeak = true)
    "clouded_leopard" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFC5A880), Color(0xFFF5EFE6), Color(0xFF4E342E), hasSpots = true)
    "snow_leopard" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFCFD8DC), Color(0xFFF5F5F5), Color(0xFF455A64), hasSpots = true)
    "asian_elephant" -> SpeciesVisualProfile(AnimalArchetype.ELEPHANT, Color(0xFF78909C), Color(0xFFB0BEC5), Color(0xFFF5F5F5))
    "red_panda" -> SpeciesVisualProfile(AnimalArchetype.PANDA, Color(0xFFC0392B), Color(0xFFFDEBD0), Color(0xFF2C1810))
    "giant_panda" -> SpeciesVisualProfile(AnimalArchetype.PANDA, Color(0xFFF5F5F5), Color(0xFF1C1C1C), Color(0xFF1C1C1C))
    "bengal_tiger" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFEF6C00), Color(0xFFFFF8E1), Color(0xFF1A1A1A), hasStripes = true)
    "sun_bear" -> SpeciesVisualProfile(AnimalArchetype.BEAR, Color(0xFF212121), Color(0xFFD7CCC8), Color(0xFFFFCA28), hasVMark = true)
    "gharial" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_CROCODILE, Color(0xFF558B2F), Color(0xFFDCEDC8), Color(0xFF33691E), hasLongBeak = true)
    "toco_toucan" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF1C1C1C), Color(0xFFFFFFFF), Color(0xFFFF6D00), hasLongBeak = true)
    "three_toed_sloth" -> SpeciesVisualProfile(AnimalArchetype.SLOTH, Color(0xFF8D7B68), Color(0xFFF1DEC9), Color(0xFF4A3B32))
    "panther_chameleon" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_LIZARD, Color(0xFF00ACC1), Color(0xFFFF7043), Color(0xFFFFEB3B), hasStripes = true)
    "poison_dart_frog" -> SpeciesVisualProfile(AnimalArchetype.FROG, Color(0xFF1E88E5), Color(0xFF212121), Color(0xFFFFEB3B), hasSpots = true)
    "green_iguana" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_LIZARD, Color(0xFF4CAF50), Color(0xFFA5D6A7), Color(0xFF2E7D32), hasStripes = true)

    // 41-60 Americas & Africa
    "ruffed_lemur" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFF212121), Color(0xFFF5F5F5), Color(0xFFFFB300))
    "andean_condor" -> SpeciesVisualProfile(AnimalArchetype.BIRD_RAPTOR, Color(0xFF212121), Color(0xFFFFFFFF), Color(0xFFE57373))
    "sword_billed_hummingbird" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF2E7D32), Color(0xFF81C784), Color(0xFF212121), hasLongBeak = true)
    "jaguar" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFD68910), Color(0xFFFCF3CF), Color(0xFF2C1810), hasSpots = true)
    "capybara" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF8D6E63), Color(0xFFBCAAA4), Color(0xFF4E342E))
    "giant_anteater" -> SpeciesVisualProfile(AnimalArchetype.ANTEATER, Color(0xFF5D4037), Color(0xFFD7CCC8), Color(0xFF1C1C1C), hasStripes = true)
    "scarlet_macaw" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFFE53935), Color(0xFFFFEB3B), Color(0xFF1E88E5))
    "harpy_eagle" -> SpeciesVisualProfile(AnimalArchetype.BIRD_RAPTOR, Color(0xFF455A64), Color(0xFFECEFF1), Color(0xFF263238))
    "green_anaconda" -> SpeciesVisualProfile(AnimalArchetype.SNAKE, Color(0xFF558B2F), Color(0xFFC5E1A5), Color(0xFF212121), hasSpots = true)
    "basilisk_lizard" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_LIZARD, Color(0xFF66BB6A), Color(0xFFC8E6C9), Color(0xFF1B5E20))
    "cheetah" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFF5B041), Color(0xFFFEF9E7), Color(0xFF1C1C1C), hasSpots = true)
    "serval" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFE59866), Color(0xFFFDEBD0), Color(0xFF212121), hasSpots = true)
    "african_lion" -> SpeciesVisualProfile(AnimalArchetype.LION, Color(0xFFD4AC0D), Color(0xFFF9E79F), Color(0xFF873600))
    "black_rhinoceros" -> SpeciesVisualProfile(AnimalArchetype.RHINO, Color(0xFF546E7A), Color(0xFF90A4AE), Color(0xFFCFD8DC))
    "hippopotamus" -> SpeciesVisualProfile(AnimalArchetype.HIPPO, Color(0xFF7E57C2), Color(0xFFD1C4E9), Color(0xFFF48FB1))
    "giraffe" -> SpeciesVisualProfile(AnimalArchetype.GIRAFFE, Color(0xFFF4D03F), Color(0xFFFCF3CF), Color(0xFF935116), hasSpots = true)
    "mountain_gorilla" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFF2C3E50), Color(0xFF7F8C8D), Color(0xFF1C2833))
    "meerkat" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFFD2B48C), Color(0xFFF5E6C8), Color(0xFF3E2723), hasStripes = true)
    "honey_badger" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF212121), Color(0xFFE0E0E0), Color(0xFF424242))
    "shoebill" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFF607D8B), Color(0xFFB0BEC5), Color(0xFFFFCA28), hasLongBeak = true)

    // 61-80 Africa & Northern Forests
    "african_wild_dog" -> SpeciesVisualProfile(AnimalArchetype.CANID, Color(0xFFD4AC0D), Color(0xFF212121), Color(0xFFF5F5F5), hasSpots = true)
    "grey_crowned_crane" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFF546E7A), Color(0xFFFFFFFF), Color(0xFFFFD54F), hasLongBeak = true)
    "chameleon_jackson" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_LIZARD, Color(0xFF43A047), Color(0xFFA5D6A7), Color(0xFF1B5E20), hasHorns = true)
    "african_grey_parrot" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF90A4AE), Color(0xFFECEFF1), Color(0xFFE53935))
    "okapi" -> SpeciesVisualProfile(AnimalArchetype.GIRAFFE, Color(0xFF6D214F), Color(0xFFF5F5F5), Color(0xFF2C1810), hasStripes = true)
    "gray_wolf" -> SpeciesVisualProfile(AnimalArchetype.CANID, Color(0xFF78909C), Color(0xFFECEFF1), Color(0xFF37474F))
    "moose" -> SpeciesVisualProfile(AnimalArchetype.DEER, Color(0xFF5D4037), Color(0xFF8D6E63), Color(0xFFD7CCC8), hasAntlers = true)
    "wolverine" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF4E342E), Color(0xFFD7CCC8), Color(0xFF212121))
    "siberian_tiger" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFF39C12), Color(0xFFFFF8E1), Color(0xFF1A1A1A), hasStripes = true)
    "eurasian_lynx" -> SpeciesVisualProfile(AnimalArchetype.CAT, Color(0xFFC8A97E), Color(0xFFF5EFE6), Color(0xFF4E342E), hasSpots = true)
    "brown_bear" -> SpeciesVisualProfile(AnimalArchetype.BEAR, Color(0xFF6D4C41), Color(0xFFA1887F), Color(0xFF3E2723))
    "golden_eagle" -> SpeciesVisualProfile(AnimalArchetype.BIRD_RAPTOR, Color(0xFF5D4037), Color(0xFFD7CCC8), Color(0xFFFFCA28))
    "peregrine_falcon" -> SpeciesVisualProfile(AnimalArchetype.BIRD_RAPTOR, Color(0xFF455A64), Color(0xFFECEFF1), Color(0xFFFFCA28))
    "great_grey_owl" -> SpeciesVisualProfile(AnimalArchetype.OWL, Color(0xFF757575), Color(0xFFE0E0E0), Color(0xFFFFEB3B))
    "beaver" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF6D4C41), Color(0xFFA1887F), Color(0xFFF5F5F5))
    "sable" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF4E342E), Color(0xFFD7CCC8), Color(0xFF212121))
    "red_fox" -> SpeciesVisualProfile(AnimalArchetype.CANID, Color(0xFFE65100), Color(0xFFFFF3E0), Color(0xFF212121))
    "bald_eagle" -> SpeciesVisualProfile(AnimalArchetype.BIRD_RAPTOR, Color(0xFF3E2723), Color(0xFFFFFFFF), Color(0xFFFFCA28))
    "snowy_owl" -> SpeciesVisualProfile(AnimalArchetype.OWL, Color(0xFFF5F5F5), Color(0xFFCFD8DC), Color(0xFFFFD54F), hasSpots = true)
    "polar_bear" -> SpeciesVisualProfile(AnimalArchetype.BEAR, Color(0xFFF5F7FA), Color(0xFFDCE4EC), Color(0xFF263238))

    // 81-100 Rivers, Oceania & Polar
    "giant_river_otter" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF5D4037), Color(0xFFFFE0B2), Color(0xFF3E2723))
    "chinese_giant_salamander" -> SpeciesVisualProfile(AnimalArchetype.SALAMANDER, Color(0xFF5D4037), Color(0xFF8D6E63), Color(0xFF3E2723), hasSpots = true)
    "axolotl" -> SpeciesVisualProfile(AnimalArchetype.SALAMANDER, Color(0xFFFFCDD2), Color(0xFFFFEBEE), Color(0xFFEC407A), hasAxolotlGills = true)
    "platypus" -> SpeciesVisualProfile(AnimalArchetype.PLATYPUS, Color(0xFF6D4C41), Color(0xFFA1887F), Color(0xFF37474F))
    "shoebill_stork" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFF78909C), Color(0xFFECEFF1), Color(0xFFFFCA28), hasLongBeak = true)
    "roseate_spoonbill" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFFF48FB1), Color(0xFFFCE4EC), Color(0xFFFF80AB), hasLongBeak = true)
    "flamigo" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFFFF6F91), Color(0xFFFFC2D1), Color(0xFF212121), hasLongBeak = true)
    "kingfisher" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF0288D1), Color(0xFFFF7043), Color(0xFFFFFFFF), hasLongBeak = true)
    "black_necked_crane" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFFECEFF1), Color(0xFF212121), Color(0xFFE53935), hasLongBeak = true)
    "chinese_alligator" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_CROCODILE, Color(0xFF4E5D42), Color(0xFFA5B592), Color(0xFF263238))
    "saltwater_crocodile" -> SpeciesVisualProfile(AnimalArchetype.REPTILE_CROCODILE, Color(0xFF556B2F), Color(0xFFC5E1A5), Color(0xFF2E3B1F))
    "mata_mata" -> SpeciesVisualProfile(AnimalArchetype.TURTLE, Color(0xFF6D4C41), Color(0xFFA1887F), Color(0xFF3E2723))
    "giant_flying_fox" -> SpeciesVisualProfile(AnimalArchetype.BAT, Color(0xFF2C2C2C), Color(0xFFD84315), Color(0xFF424242))
    "snow_monkey" -> SpeciesVisualProfile(AnimalArchetype.PRIMATE, Color(0xFFBCAAA4), Color(0xFFFFCDD2), Color(0xFF8D6E63))
    "albatross" -> SpeciesVisualProfile(AnimalArchetype.BIRD_WATER, Color(0xFFF5F5F5), Color(0xFF455A64), Color(0xFFFFCA28), hasLongBeak = true)
    "koala" -> SpeciesVisualProfile(AnimalArchetype.BEAR, Color(0xFF90A4AE), Color(0xFFECEFF1), Color(0xFF37474F))
    "red_kangaroo" -> SpeciesVisualProfile(AnimalArchetype.KANGAROO, Color(0xFFC05621), Color(0xFFFED7AA), Color(0xFF5D4037))
    "tasmanian_devil" -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF212121), Color(0xFFF5F5F5), Color(0xFFE57373))
    "emperor_penguin" -> SpeciesVisualProfile(AnimalArchetype.PENGUIN, Color(0xFF1C2833), Color(0xFFFFFFFF), Color(0xFFFFB300))
    "kiwi_bird" -> SpeciesVisualProfile(AnimalArchetype.BIRD_TROPICAL, Color(0xFF795548), Color(0xFFA1887F), Color(0xFFFFD54F), hasLongBeak = true)

    else -> SpeciesVisualProfile(AnimalArchetype.SMALL_MAMMAL, Color(0xFF795548), Color(0xFFD7CCC8), Color(0xFF3E2723))
}

/**
 * Cute, high-contrast, species-accurate vector animal character renderer with soft PBR lighting.
 */
@Composable
fun PbrAnimalRenderer(
    animal: AnimalEntity,
    lightAngleDeg: Float,
    modifier: Modifier = Modifier
) {
    val profile = remember(animal.id) { getSpeciesVisualProfile(animal.id) }

    val infiniteTransition = rememberInfiniteTransition(label = "pbr_bob")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bobOffset
        val r = min(size.width, size.height) * 0.34f

        val lightRad = Math.toRadians(lightAngleDeg.toDouble()).toFloat()
        val lx = cos(lightRad)
        val ly = sin(lightRad) * 0.6f
        val lightOffset = Offset(cx + lx * r * 0.38f, cy + ly * r * 0.38f)

        // Ground shadow
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.38f), Color.Transparent),
                center = Offset(cx, cy + r * 1.18f),
                radius = r * 0.95f
            ),
            topLeft = Offset(cx - r * 0.95f, cy + r * 0.96f),
            size = Size(r * 1.9f, r * 0.42f)
        )

        val center = Offset(cx, cy)

        // 1. Back appendages (ears, wings, antlers, horns, gills, tails)
        drawBackAppendages(center, r, profile)

        // 2. Main head/body silhouette with PBR radial shading
        drawAnimalBody(center, r, lightOffset, profile)

        // 3. Markings (spots, stripes, V-mark, panda eye patches)
        drawSpeciesMarkings(center, r, profile, animal.id)

        // 4. Face (eyes, catchlights, cheeks, nose/snout/beak, mouth)
        drawAnimalFace(center, r, profile, animal.id)
    }
}

private fun DrawScope.drawShadedCircle(
    center: Offset,
    radius: Float,
    color: Color,
    lightOffset: Offset
) {
    val highlight = Color(
        red = (color.red * 1.28f + 0.08f).coerceAtMost(1f),
        green = (color.green * 1.28f + 0.08f).coerceAtMost(1f),
        blue = (color.blue * 1.28f + 0.08f).coerceAtMost(1f),
        alpha = color.alpha
    )
    val shadow = Color(
        red = color.red * 0.55f,
        green = color.green * 0.55f,
        blue = color.blue * 0.55f,
        alpha = color.alpha
    )
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to highlight,
                0.65f to color,
                1.0f to shadow
            ),
            center = lightOffset,
            radius = radius * 1.35f
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawBackAppendages(
    center: Offset,
    r: Float,
    profile: SpeciesVisualProfile
) {
    when (profile.archetype) {
        AnimalArchetype.BEAR, AnimalArchetype.PANDA -> {
            val earColor = if (profile.archetype == AnimalArchetype.PANDA && profile.primary == Color(0xFFF5F5F5)) profile.secondary else profile.primary
            listOf(-1f, 1f).forEach { dir ->
                val ec = Offset(center.x + dir * r * 0.68f, center.y - r * 0.68f)
                drawCircle(earColor, r * 0.30f, ec)
                drawCircle(Color(0xFFFFAB91).copy(alpha = 0.65f), r * 0.15f, ec)
            }
        }
        AnimalArchetype.CAT, AnimalArchetype.LION, AnimalArchetype.CANID, AnimalArchetype.KANGAROO, AnimalArchetype.BAT -> {
            if (profile.archetype == AnimalArchetype.LION) {
                // Fluffy Lion Mane
                for (i in 0 until 12) {
                    val angle = i * (2f * PI.toFloat() / 12f)
                    val mc = Offset(center.x + cos(angle) * r * 0.85f, center.y + sin(angle) * r * 0.85f)
                    drawCircle(profile.accent, r * 0.40f, mc)
                }
            }
            if (profile.archetype == AnimalArchetype.BAT) {
                // Bat wings
                listOf(-1f, 1f).forEach { dir ->
                    val wing = Path().apply {
                        moveTo(center.x + dir * r * 0.5f, center.y)
                        lineTo(center.x + dir * r * 1.55f, center.y - r * 0.45f)
                        lineTo(center.x + dir * r * 1.30f, center.y + r * 0.45f)
                        lineTo(center.x + dir * r * 0.65f, center.y + r * 0.35f)
                        close()
                    }
                    drawPath(wing, profile.primary.copy(alpha = 0.92f))
                }
            }
            val earHeight = if (profile.archetype == AnimalArchetype.KANGAROO || profile.archetype == AnimalArchetype.CANID) 1.25f else 1.05f
            listOf(-1f, 1f).forEach { dir ->
                val ear = Path().apply {
                    moveTo(center.x + dir * r * 0.35f, center.y - r * 0.65f)
                    lineTo(center.x + dir * r * 0.78f, center.y - r * earHeight)
                    lineTo(center.x + dir * r * 0.85f, center.y - r * 0.25f)
                    close()
                }
                drawPath(ear, profile.primary)
                val inner = Path().apply {
                    moveTo(center.x + dir * r * 0.45f, center.y - r * 0.62f)
                    lineTo(center.x + dir * r * 0.72f, center.y - r * (earHeight - 0.14f))
                    lineTo(center.x + dir * r * 0.76f, center.y - r * 0.34f)
                    close()
                }
                drawPath(inner, Color(0xFFFFAB91).copy(alpha = 0.75f))
            }
        }
        AnimalArchetype.PRIMATE, AnimalArchetype.SLOTH -> {
            listOf(-1f, 1f).forEach { dir ->
                val ec = Offset(center.x + dir * r * 0.92f, center.y - r * 0.08f)
                drawCircle(profile.primary, r * 0.28f, ec)
                drawCircle(profile.secondary, r * 0.16f, ec)
            }
        }
        AnimalArchetype.ELEPHANT -> {
            listOf(-1f, 1f).forEach { dir ->
                drawOval(
                    color = profile.primary,
                    topLeft = Offset(center.x + (if (dir < 0) -r * 1.48f else r * 0.58f), center.y - r * 0.68f),
                    size = Size(r * 0.90f, r * 1.22f)
                )
                drawOval(
                    color = Color(0xFFF8BBD0).copy(alpha = 0.55f),
                    topLeft = Offset(center.x + (if (dir < 0) -r * 1.32f else r * 0.68f), center.y - r * 0.48f),
                    size = Size(r * 0.62f, r * 0.85f)
                )
            }
        }
        AnimalArchetype.DEER, AnimalArchetype.GIRAFFE -> {
            // Ears
            listOf(-1f, 1f).forEach { dir ->
                rotate(dir * 28f, pivot = Offset(center.x + dir * r * 0.7f, center.y - r * 0.5f)) {
                    drawOval(
                        color = profile.primary,
                        topLeft = Offset(center.x + dir * r * 0.68f - r * 0.25f, center.y - r * 0.62f),
                        size = Size(r * 0.50f, r * 0.26f)
                    )
                }
            }
            if (profile.hasAntlers) {
                listOf(-1f, 1f).forEach { dir ->
                    val antler = Path().apply {
                        moveTo(center.x + dir * r * 0.32f, center.y - r * 0.75f)
                        lineTo(center.x + dir * r * 0.62f, center.y - r * 1.35f)
                        moveTo(center.x + dir * r * 0.48f, center.y - r * 1.05f)
                        lineTo(center.x + dir * r * 0.82f, center.y - r * 1.18f)
                        moveTo(center.x + dir * r * 0.44f, center.y - r * 0.98f)
                        lineTo(center.x + dir * r * 0.24f, center.y - r * 1.24f)
                    }
                    drawPath(antler, Color(0xFFD7CCC8), style = Stroke(width = r * 0.10f, cap = StrokeCap.Round))
                }
            } else if (profile.hasHorns || profile.archetype == AnimalArchetype.GIRAFFE) {
                listOf(-1f, 1f).forEach { dir ->
                    drawLine(
                        color = profile.accent,
                        start = Offset(center.x + dir * r * 0.30f, center.y - r * 0.72f),
                        end = Offset(center.x + dir * r * 0.42f, center.y - r * 1.15f),
                        strokeWidth = r * 0.11f,
                        cap = StrokeCap.Round
                    )
                    if (profile.archetype == AnimalArchetype.GIRAFFE) {
                        drawCircle(profile.accent, r * 0.10f, Offset(center.x + dir * r * 0.42f, center.y - r * 1.15f))
                    }
                }
            }
        }
        AnimalArchetype.BIRD_RAPTOR, AnimalArchetype.OWL, AnimalArchetype.BIRD_TROPICAL, AnimalArchetype.BIRD_WATER, AnimalArchetype.PENGUIN -> {
            // Side wings
            listOf(-1f, 1f).forEach { dir ->
                rotate(dir * -20f, pivot = Offset(center.x + dir * r * 0.85f, center.y)) {
                    drawOval(
                        color = profile.primary,
                        topLeft = Offset(center.x + (if (dir < 0) -r * 1.22f else r * 0.72f), center.y - r * 0.25f),
                        size = Size(r * 0.50f, r * 0.85f)
                    )
                }
            }
            if (profile.archetype == AnimalArchetype.OWL) {
                // Owl ear tufts
                listOf(-1f, 1f).forEach { dir ->
                    val tuft = Path().apply {
                        moveTo(center.x + dir * r * 0.35f, center.y - r * 0.75f)
                        lineTo(center.x + dir * r * 0.78f, center.y - r * 1.10f)
                        lineTo(center.x + dir * r * 0.70f, center.y - r * 0.55f)
                        close()
                    }
                    drawPath(tuft, profile.primary)
                }
            }
        }
        AnimalArchetype.SALAMANDER -> {
            if (profile.hasAxolotlGills) {
                listOf(-1f, 1f).forEach { dir ->
                    for (j in -1..1) {
                        drawLine(
                            color = profile.accent,
                            start = Offset(center.x + dir * r * 0.75f, center.y + j * r * 0.18f),
                            end = Offset(center.x + dir * r * 1.28f, center.y + j * r * 0.28f - r * 0.10f),
                            strokeWidth = r * 0.12f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
        AnimalArchetype.FISH -> {
            // Tail fin & dorsal fin
            val tail = Path().apply {
                moveTo(center.x + r * 0.75f, center.y)
                lineTo(center.x + r * 1.38f, center.y - r * 0.48f)
                lineTo(center.x + r * 1.22f, center.y)
                lineTo(center.x + r * 1.38f, center.y + r * 0.48f)
                close()
            }
            drawPath(tail, profile.primary)
            val dorsal = Path().apply {
                moveTo(center.x - r * 0.25f, center.y - r * 0.65f)
                lineTo(center.x + r * 0.22f, center.y - r * 1.05f)
                lineTo(center.x + r * 0.45f, center.y - r * 0.60f)
                close()
            }
            drawPath(dorsal, profile.accent)
        }
        AnimalArchetype.SMALL_MAMMAL, AnimalArchetype.TAPIR, AnimalArchetype.BOAR, AnimalArchetype.HIPPO, AnimalArchetype.PLATYPUS -> {
            listOf(-1f, 1f).forEach { dir ->
                val ec = Offset(center.x + dir * r * 0.62f, center.y - r * 0.68f)
                drawCircle(profile.primary, r * 0.22f, ec)
                drawCircle(Color(0xFFFFAB91).copy(alpha = 0.6f), r * 0.11f, ec)
            }
        }
        else -> {}
    }
}

private fun DrawScope.drawAnimalBody(
    center: Offset,
    r: Float,
    lightOffset: Offset,
    profile: SpeciesVisualProfile
) {
    when (profile.archetype) {
        AnimalArchetype.FISH -> {
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(profile.secondary, profile.primary),
                    center = lightOffset,
                    radius = r * 1.4f
                ),
                topLeft = Offset(center.x - r * 1.10f, center.y - r * 0.72f),
                size = Size(r * 2.10f, r * 1.44f)
            )
        }
        AnimalArchetype.REPTILE_CROCODILE -> {
            // Wide oval reptile head + snout
            drawShadedCircle(center, r * 0.92f, profile.primary, lightOffset)
            drawOval(
                color = profile.secondary,
                topLeft = Offset(center.x - r * 0.58f, center.y + r * 0.05f),
                size = Size(r * 1.16f, r * 0.72f)
            )
        }
        AnimalArchetype.PENGUIN, AnimalArchetype.BIRD_RAPTOR, AnimalArchetype.BIRD_TROPICAL, AnimalArchetype.BIRD_WATER, AnimalArchetype.OWL -> {
            drawShadedCircle(center, r * 0.96f, profile.primary, lightOffset)
            // Soft chest/belly patch
            drawOval(
                color = profile.secondary,
                topLeft = Offset(center.x - r * 0.62f, center.y + r * 0.10f),
                size = Size(r * 1.24f, r * 0.80f)
            )
        }
        else -> {
            drawShadedCircle(center, r * 0.96f, profile.primary, lightOffset)
            // Cute chubby belly/cheek undercoat
            if (profile.archetype == AnimalArchetype.PRIMATE || profile.archetype == AnimalArchetype.SLOTH) {
                drawOval(
                    color = profile.secondary,
                    topLeft = Offset(center.x - r * 0.68f, center.y - r * 0.42f),
                    size = Size(r * 1.36f, r * 1.12f)
                )
            }
        }
    }
}

private fun DrawScope.drawSpeciesMarkings(
    center: Offset,
    r: Float,
    profile: SpeciesVisualProfile,
    animalId: String
) {
    if (profile.hasVMark) {
        val vPath = Path().apply {
            moveTo(center.x - r * 0.42f, center.y + r * 0.48f)
            lineTo(center.x, center.y + r * 0.85f)
            lineTo(center.x + r * 0.42f, center.y + r * 0.48f)
            lineTo(center.x + r * 0.28f, center.y + r * 0.48f)
            lineTo(center.x, center.y + r * 0.70f)
            lineTo(center.x - r * 0.28f, center.y + r * 0.48f)
            close()
        }
        drawPath(vPath, profile.accent)
    }
    if (animalId == "giant_panda") {
        listOf(-1f, 1f).forEach { dir ->
            rotate(dir * -20f, pivot = Offset(center.x + dir * r * 0.34f, center.y - r * 0.10f)) {
                drawOval(
                    color = profile.secondary,
                    topLeft = Offset(center.x + dir * r * 0.34f - r * 0.22f, center.y - r * 0.26f),
                    size = Size(r * 0.44f, r * 0.34f)
                )
            }
        }
    }
    if (animalId == "red_panda") {
        listOf(-1f, 1f).forEach { dir ->
            drawOval(
                color = profile.secondary,
                topLeft = Offset(center.x + dir * r * 0.44f - r * 0.20f, center.y + r * 0.04f),
                size = Size(r * 0.40f, r * 0.24f)
            )
        }
    }
    if (profile.hasStripes) {
        val stripeColor = profile.accent.copy(alpha = 0.85f)
        // Forehead & cheek stripes
        for (i in -1..1) {
            drawLine(
                color = stripeColor,
                start = Offset(center.x + i * r * 0.16f, center.y - r * 0.82f),
                end = Offset(center.x + i * r * 0.10f, center.y - r * 0.48f),
                strokeWidth = r * 0.08f,
                cap = StrokeCap.Round
            )
        }
        listOf(-1f, 1f).forEach { dir ->
            drawLine(
                color = stripeColor,
                start = Offset(center.x + dir * r * 0.88f, center.y - r * 0.15f),
                end = Offset(center.x + dir * r * 0.56f, center.y - r * 0.05f),
                strokeWidth = r * 0.08f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = stripeColor,
                start = Offset(center.x + dir * r * 0.85f, center.y + r * 0.12f),
                end = Offset(center.x + dir * r * 0.54f, center.y + r * 0.16f),
                strokeWidth = r * 0.08f,
                cap = StrokeCap.Round
            )
        }
    }
    if (profile.hasSpots) {
        val spotColor = profile.accent.copy(alpha = 0.78f)
        val spots = listOf(
            Offset(-0.45f, -0.45f) to 0.09f,
            Offset(0.45f, -0.42f) to 0.08f,
            Offset(-0.58f, 0.18f) to 0.08f,
            Offset(0.56f, 0.20f) to 0.09f,
            Offset(0.0f, -0.62f) to 0.07f
        )
        spots.forEach { (off, sr) ->
            drawCircle(spotColor, r * sr, Offset(center.x + off.x * r, center.y + off.y * r))
        }
    }
    if (profile.archetype == AnimalArchetype.PANGOLIN) {
        // Overlapping diamond armor scales
        for (row in -1..1) {
            for (col in -1..1) {
                val sx = center.x + col * r * 0.34f
                val sy = center.y - r * 0.55f + row * r * 0.22f
                drawCircle(profile.accent.copy(alpha = 0.45f), r * 0.14f, Offset(sx, sy), style = Stroke(width = r * 0.04f))
            }
        }
    }
    if (animalId == "formosan_landlocked_salmon") {
        drawOval(
            brush = Brush.horizontalGradient(listOf(Color(0xFFFF80AB), Color(0xFFEA80FC), Color(0xFF80D8FF))),
            topLeft = Offset(center.x - r * 0.65f, center.y - r * 0.08f),
            size = Size(r * 1.30f, r * 0.20f)
        )
    }
}

private fun DrawScope.drawAnimalFace(
    center: Offset,
    r: Float,
    profile: SpeciesVisualProfile,
    animalId: String
) {
    val isBird = profile.archetype in listOf(
        AnimalArchetype.BIRD_RAPTOR, AnimalArchetype.OWL,
        AnimalArchetype.BIRD_TROPICAL, AnimalArchetype.BIRD_WATER, AnimalArchetype.PENGUIN
    )
    val isFrog = profile.archetype == AnimalArchetype.FROG

    // 1. Eyes
    val eyeY = when {
        profile.archetype == AnimalArchetype.FISH -> center.y - r * 0.15f
        isFrog -> center.y - r * 0.55f
        else -> center.y - r * 0.12f
    }
    val eyeSpread = if (profile.archetype == AnimalArchetype.FISH) 0f else r * 0.34f

    if (profile.archetype == AnimalArchetype.FISH) {
        val ex = center.x - r * 0.55f
        drawCircle(Color.White, r * 0.16f, Offset(ex, eyeY))
        drawCircle(Color(0xFF111111), r * 0.09f, Offset(ex - r * 0.02f, eyeY))
        drawCircle(Color.White, r * 0.035f, Offset(ex - r * 0.04f, eyeY - r * 0.04f))
    } else {
        listOf(-1f, 1f).forEach { dir ->
            val ex = center.x + dir * eyeSpread
            if (isFrog) {
                drawCircle(profile.primary, r * 0.28f, Offset(ex, eyeY))
            }
            if (profile.archetype == AnimalArchetype.OWL) {
                drawCircle(profile.secondary, r * 0.28f, Offset(ex, eyeY))
            }
            // White sclera + dark pupil + sparkle catchlight
            drawCircle(Color.White, r * 0.16f, Offset(ex, eyeY))
            val pupilColor = if (profile.archetype == AnimalArchetype.OWL || profile.archetype == AnimalArchetype.CAT) Color(0xFF111111) else Color(0xFF1A1A1A)
            drawCircle(pupilColor, r * 0.105f, Offset(ex, eyeY))
            drawCircle(Color.White, r * 0.04f, Offset(ex - r * 0.035f, eyeY - r * 0.035f))
        }
    }

    // Rosy toddler-friendly cheeks
    if (profile.archetype != AnimalArchetype.FISH && profile.archetype != AnimalArchetype.REPTILE_CROCODILE) {
        listOf(-1f, 1f).forEach { dir ->
            drawCircle(
                color = Color(0xFFFF8A80).copy(alpha = 0.42f),
                radius = r * 0.13f,
                center = Offset(center.x + dir * r * 0.52f, center.y + r * 0.12f)
            )
        }
    }

    // 2. Nose / Snout / Beak / Trunk / Mouth
    when {
        isBird -> {
            val beakColor = if (profile.accent == Color(0xFF212121)) Color(0xFFFFB300) else profile.accent
            val beakLen = if (profile.hasLongBeak) r * 0.72f else r * 0.34f
            val beak = Path().apply {
                moveTo(center.x - r * 0.18f, center.y - r * 0.02f)
                lineTo(center.x + r * 0.18f, center.y - r * 0.02f)
                lineTo(center.x, center.y + beakLen)
                close()
            }
            drawPath(beak, beakColor)
        }
        profile.archetype == AnimalArchetype.ELEPHANT -> {
            val trunk = Path().apply {
                moveTo(center.x - r * 0.16f, center.y + r * 0.05f)
                quadraticBezierTo(center.x - r * 0.25f, center.y + r * 0.80f, center.x + r * 0.25f, center.y + r * 0.90f)
                quadraticBezierTo(center.x + r * 0.12f, center.y + r * 0.65f, center.x + r * 0.16f, center.y + r * 0.05f)
                close()
            }
            drawPath(trunk, profile.secondary)
        }
        profile.archetype == AnimalArchetype.PLATYPUS -> {
            drawOval(
                color = Color(0xFFFFB300),
                topLeft = Offset(center.x - r * 0.34f, center.y + r * 0.04f),
                size = Size(r * 0.68f, r * 0.38f)
            )
        }
        profile.archetype == AnimalArchetype.RHINO -> {
            val horn = Path().apply {
                moveTo(center.x - r * 0.16f, center.y + r * 0.25f)
                lineTo(center.x, center.y - r * 0.22f)
                lineTo(center.x + r * 0.16f, center.y + r * 0.25f)
                close()
            }
            drawPath(horn, Color(0xFFECEFF1))
        }
        profile.archetype == AnimalArchetype.BOAR -> {
            drawOval(Color(0xFFF48FB1), Offset(center.x - r * 0.28f, center.y + r * 0.04f), Size(r * 0.56f, r * 0.34f))
            // Tusks
            listOf(-1f, 1f).forEach { dir ->
                val tusk = Path().apply {
                    moveTo(center.x + dir * r * 0.26f, center.y + r * 0.26f)
                    lineTo(center.x + dir * r * 0.44f, center.y + r * 0.02f)
                    lineTo(center.x + dir * r * 0.34f, center.y + r * 0.34f)
                    close()
                }
                drawPath(tusk, Color.White)
            }
        }
        profile.archetype == AnimalArchetype.FISH -> {
            drawArc(
                color = Color(0xFF263238),
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(center.x - r * 0.92f, center.y + r * 0.02f),
                size = Size(r * 0.32f, r * 0.22f),
                style = Stroke(width = r * 0.05f, cap = StrokeCap.Round)
            )
        }
        else -> {
            // Muzzle oval + cute nose + smile
            val muzzleColor = if (animalId == "giant_panda") Color.White else profile.secondary
            drawOval(
                color = muzzleColor.copy(alpha = 0.92f),
                topLeft = Offset(center.x - r * 0.34f, center.y + r * 0.04f),
                size = Size(r * 0.68f, r * 0.46f)
            )
            val noseColor = if (profile.hasBigTrunk) Color(0xFFE57373) else Color(0xFF1F1F1F)
            val noseRadius = if (profile.hasBigTrunk) r * 0.22f else r * 0.13f
            drawOval(
                color = noseColor,
                topLeft = Offset(center.x - noseRadius, center.y + r * 0.06f),
                size = Size(noseRadius * 2f, noseRadius * 1.4f)
            )
            // Smile
            drawArc(
                color = Color(0xFF212121),
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(center.x - r * 0.18f, center.y + r * 0.20f),
                size = Size(r * 0.36f, r * 0.20f),
                style = Stroke(width = r * 0.05f, cap = StrokeCap.Round)
            )
            // Cat / Canid whiskers
            if (profile.archetype == AnimalArchetype.CAT || profile.archetype == AnimalArchetype.LION || profile.archetype == AnimalArchetype.CANID) {
                listOf(-1f, 1f).forEach { dir ->
                    for (w in -1..1) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.7f),
                            start = Offset(center.x + dir * r * 0.38f, center.y + r * 0.22f + w * r * 0.07f),
                            end = Offset(center.x + dir * r * 0.78f, center.y + r * 0.18f + w * r * 0.12f),
                            strokeWidth = r * 0.028f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}
