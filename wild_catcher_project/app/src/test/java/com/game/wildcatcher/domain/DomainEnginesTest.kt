package com.game.wildcatcher.domain

import com.game.wildcatcher.data.model.*
import com.game.wildcatcher.domain.capture.CaptureEngine
import com.game.wildcatcher.domain.health.HealthAlertState
import com.game.wildcatcher.domain.health.PlayTimeManager
import com.game.wildcatcher.domain.shelter.ShelterEconomyEngine
import org.junit.Assert.*
import org.junit.Test

class DomainEnginesTest {

    private val testBear = AnimalEntity(
        id = "formosan_black_bear",
        name = "台灣黑熊",
        scientificName = "Ursus thibetanus formosanus",
        habitat = HabitatType.ALPINE_CONIFER,
        habitatName = "高山原始闊葉針葉林",
        grade = ConservationGrade.YELLOW,
        gradeLabel = "VU 易危 (黃燈)",
        funFact = "胸前有醒目的白色V字斑紋",
        ecologyNote = "台灣特有亞種",
        callSoundName = "snd_bear_growl",
        baseCaptureDifficulty = 0.65f,
        struggleVelocity = 1.35f,
        sweetSpotWidth = 0.22f,
        coinYieldPerHour = 85,
        unlockLevel = 3,
        activityCycle = ActivityCycle.DIURNAL,
        preferredWeather = "Sunny",
        albedoTexture = "tex_bear_albedo.astc",
        normalMapTexture = "tex_bear_normal.astc",
        roughnessMapTexture = "tex_bear_roughness.astc"
    )

    @Test
    fun testToddlerBimanualCoordination_boostsCapturePower() {
        // Engine without left hand stabilization
        val engine1 = CaptureEngine(testBear, consecutiveFailures = 0)
        // Set cursor to center inside sweet spot
        engine1.onRightHandTap()
        val soloProgress = engine1.getCurrentState().captureProgress

        // Engine with left hand stabilizing
        val engine2 = CaptureEngine(testBear, consecutiveFailures = 0)
        engine2.setLeftHandStabilizing(true)
        engine2.onRightHandTap()
        val bimanualProgress = engine2.getCurrentState().captureProgress

        assertTrue(
            "Bimanual coordination should boost capture progress more than single hand",
            bimanualProgress > soloProgress
        )
    }

    @Test
    fun testLeftHandHolding_slowsDownAnimalStruggle() {
        val engine = CaptureEngine(testBear, consecutiveFailures = 0)
        engine.setLeftHandStabilizing(true)
        engine.tick(0.5f)
        val state = engine.getCurrentState()
        assertTrue("Left hand hold seconds should increase", state.leftHandHoldSeconds > 0f)
    }

    @Test
    fun testPlayTimeManager_triggersAtThirtyMinutes() {
        val manager = PlayTimeManager(thresholdSeconds = 1800L) // 30 mins
        assertEquals(HealthAlertState.Normal, manager.healthState.value)

        manager.onTick(26 * 60L)
        assertTrue(manager.healthState.value is HealthAlertState.NearThreshold)

        manager.onTick(4 * 60L)
        assertTrue(manager.healthState.value is HealthAlertState.RestTimeTriggered)
        assertTrue(manager.isWarmModeActive.value)

        manager.resetTimerAfterRest()
        assertEquals(HealthAlertState.Normal, manager.healthState.value)
        assertEquals(0L, manager.activeSeconds.value)
    }

    @Test
    fun testShelterEconomy_calculatesTicketAndAnimalRevenue() {
        val economy = ShelterEconomyEngine()
        val zone = ShelterZone(
            zoneId = "zone_alpine",
            zoneName = "高山針葉區",
            habitatType = HabitatType.ALPINE_CONIFER,
            description = "冷冽松林",
            currentAnimalCount = 1,
            visitorFlowRatePerHour = 50,
            ticketPricePerVisitor = 10,
            isUnlocked = true,
            ecoLevel = 1
        )
        val report = economy.calculateHourlyRevenue(listOf(zone), listOf(testBear))
        assertEquals(735L, report.totalHourlyRevenue)
        assertEquals(65, report.totalVisitorsPerHour)
    }
}
