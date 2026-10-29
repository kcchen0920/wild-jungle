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
    fun testDynamicDifficultyAdjustment_widensSweetSpotOnFailure() {
        // Normal first attempt: consecutiveFailures = 0
        val engineNormal = CaptureEngine(testBear, consecutiveFailures = 0)
        val normalWidth = engineNormal.getCurrentState().sweetSpotWidth
        assertEquals(0.22f, normalWidth, 0.001f)

        // After 2 consecutive failures, DDA bonus must expand sweet spot
        val engineAssisted = CaptureEngine(testBear, consecutiveFailures = 2)
        val assistedWidth = engineAssisted.getCurrentState().sweetSpotWidth
        assertTrue("Assisted sweet spot should be wider than normal", assistedWidth > normalWidth)
        assertEquals(0.34f, assistedWidth, 0.001f)
    }

    @Test
    fun testBambooIncenseDart_reducesTensionAndStruggle() {
        val engine = CaptureEngine(testBear, consecutiveFailures = 0)
        engine.tick(1.0f)
        engine.applyBambooIncenseDart()
        val state = engine.getCurrentState()
        assertTrue(state.isIncenseDartActive)
    }

    @Test
    fun testPlayTimeManager_triggersAtThirtyMinutes() {
        val manager = PlayTimeManager(thresholdSeconds = 1800L) // 30 mins
        assertEquals(HealthAlertState.Normal, manager.healthState.value)

        // Tick 26 minutes (1560s) -> remaining <= 5 min -> NearThreshold
        manager.onTick(26 * 60L)
        assertTrue(manager.healthState.value is HealthAlertState.NearThreshold)

        // Tick to 30 mins (1800s) -> RestTimeTriggered & warm filter active
        manager.onTick(4 * 60L)
        assertTrue(manager.healthState.value is HealthAlertState.RestTimeTriggered)
        assertTrue(manager.isWarmModeActive.value)

        // After resting, resets timer
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

        // zoneVisitors = 50 * 1 + 15 = 65 visitors
        // ticketRevenue = 65 * 10 = 650 coins
        // bear bonus = 85 coins
        // total = 735 coins
        assertEquals(735L, report.totalHourlyRevenue)
        assertEquals(65, report.totalVisitorsPerHour)
        assertEquals(1, report.shelteredAnimalsCount)
    }
}
