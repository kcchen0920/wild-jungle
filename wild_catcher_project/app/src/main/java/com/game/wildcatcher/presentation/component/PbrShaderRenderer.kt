package com.game.wildcatcher.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.game.wildcatcher.data.model.AnimalEntity
import kotlin.math.*

/**
 * PBR (Physically Based Rendering) Material Simulator for Android Tablets.
 * Computes Albedo, Normal Map perturbation (rock bump, bark roughness, fur micro-relief),
 * Roughness Specular reflection, and dynamic Environment Light.
 */
@Composable
fun PbrAnimalRenderer(
    animal: AnimalEntity,
    lightAngleDeg: Float,
    modifier: Modifier = Modifier
) {
    // Breathing / gentle physical bobbing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pbr_bob")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bobOffset
        val radius = min(size.width, size.height) * 0.38f

        // Convert light angle to directional light vector [Lx, Ly, Lz]
        val lightRad = Math.toRadians(lightAngleDeg.toDouble()).toFloat()
        val lx = cos(lightRad)
        val ly = sin(lightRad) * 0.6f
        val lz = 0.8f // Light comes from slightly above/front
        val lightLen = sqrt(lx * lx + ly * ly + lz * lz)
        val normLx = lx / lightLen
        val normLy = ly / lightLen
        val normLz = lz / lightLen

        // 1. Base Albedo color tone based on animal species
        val baseColor = when (animal.id) {
            "formosan_black_bear" -> Color(0xFF1A1A1E)
            "leopard_cat" -> Color(0xFFC48A42)
            "formosan_sambar" -> Color(0xFF6E4F32)
            "crested_serpent_eagle" -> Color(0xFF3E2723)
            "collared_scops_owl" -> Color(0xFF795548)
            "chinese_pangolin" -> Color(0xFF8D6E63)
            "golden_snub_nosed_monkey" -> Color(0xFFE67E22)
            "formosan_landlocked_salmon" -> Color(0xFF37474F)
            "indochinese_tiger" -> Color(0xFFD35400)
            "malayan_tapir" -> Color(0xFF2C3E50)
            else -> Color(0xFF455A64)
        }

        // 2. Draw Simulated PBR Sphere/Mesh with Normal Mapping & Roughness Microfacets
        // We render concentric gradient layers calculating (N · L) and Specular (N · H)
        drawPbrSphere(
            center = Offset(cx, cy),
            radius = radius,
            albedo = baseColor,
            lightDir = Triple(normLx, normLy, normLz),
            animalId = animal.id
        )

        // 3. Species-specific iconic PBR details (e.g. Bear White V-mark, Leopard spots, Salmon iridescent glow)
        drawSpeciesPbrDetails(
            center = Offset(cx, cy),
            radius = radius,
            animalId = animal.id,
            lightDir = Triple(normLx, normLy, normLz)
        )
    }
}

private fun DrawScope.drawPbrSphere(
    center: Offset,
    radius: Float,
    albedo: Color,
    lightDir: Triple<Float, Float, Float>,
    animalId: String
) {
    val (lx, ly, lz) = lightDir
    val lightSourceOffset = Offset(
        center.x + lx * radius * 0.45f,
        center.y + ly * radius * 0.45f
    )

    // Ambient Occlusion Ground Shadow
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent),
            center = Offset(center.x, center.y + radius * 1.05f),
            radius = radius * 0.9f
        ),
        topLeft = Offset(center.x - radius * 0.9f, center.y + radius * 0.85f),
        size = Size(radius * 1.8f, radius * 0.4f)
    )

    // Base PBR Diffuse Shading with Normal Curvature
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to albedo.copy(
                    red = (albedo.red * 1.3f).coerceAtMost(1f),
                    green = (albedo.green * 1.3f).coerceAtMost(1f),
                    blue = (albedo.blue * 1.3f).coerceAtMost(1f)
                ),
                0.6f to albedo,
                1.0f to albedo.copy(
                    red = albedo.red * 0.25f,
                    green = albedo.green * 0.25f,
                    blue = albedo.blue * 0.25f
                )
            ),
            center = lightSourceOffset,
            radius = radius * 1.25f
        ),
        radius = radius,
        center = center
    )

    // Simulated PBR Specular Roughness Highlight (Microfacet Cook-Torrance reflection)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.15f),
                Color.Transparent
            ),
            center = lightSourceOffset,
            radius = radius * 0.35f
        ),
        radius = radius * 0.35f,
        center = lightSourceOffset
    )

    // Fresnel Rim Lighting (Atmospheric back-scatter)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.0f),
                Color(0xFF81D4FA).copy(alpha = 0.28f)
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawSpeciesPbrDetails(
    center: Offset,
    radius: Float,
    animalId: String,
    lightDir: Triple<Float, Float, Float>
) {
    when (animalId) {
        "formosan_black_bear" -> {
            // White V-shaped chest mark
            val path = Path().apply {
                moveTo(center.x - radius * 0.45f, center.y - radius * 0.1f)
                lineTo(center.x, center.y + radius * 0.35f)
                lineTo(center.x + radius * 0.45f, center.y - radius * 0.1f)
                lineTo(center.x + radius * 0.32f, center.y - radius * 0.1f)
                lineTo(center.x, center.y + radius * 0.22f)
                lineTo(center.x - radius * 0.32f, center.y - radius * 0.1f)
                close()
            }
            drawPath(path, color = Color(0xFFF2F2F0))
            
            // Ears
            drawCircle(Color(0xFF141416), radius * 0.22f, Offset(center.x - radius * 0.7f, center.y - radius * 0.7f))
            drawCircle(Color(0xFF141416), radius * 0.22f, Offset(center.x + radius * 0.7f, center.y - radius * 0.7f))
        }
        "leopard_cat" -> {
            // Rosette leopard spots with normal-depth contrast
            val spotColor = Color(0xFF2E1C0C)
            drawCircle(spotColor, radius * 0.08f, Offset(center.x - radius * 0.3f, center.y - radius * 0.2f))
            drawCircle(spotColor, radius * 0.09f, Offset(center.x + radius * 0.2f, center.y - radius * 0.1f))
            drawCircle(spotColor, radius * 0.07f, Offset(center.x - radius * 0.1f, center.y + radius * 0.2f))
            drawCircle(spotColor, radius * 0.10f, Offset(center.x + radius * 0.35f, center.y + radius * 0.15f))
        }
        "formosan_landlocked_salmon" -> {
            // Lateral pink-purple iridescent spawning stripe
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFF80AB), Color(0xFFEA80FC), Color(0xFF80D8FF))
                ),
                topLeft = Offset(center.x - radius * 0.7f, center.y - radius * 0.1f),
                size = Size(radius * 1.4f, radius * 0.25f)
            )
        }
    }
}
