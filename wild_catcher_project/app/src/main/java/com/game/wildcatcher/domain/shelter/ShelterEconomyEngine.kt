package com.game.wildcatcher.domain.shelter

import com.game.wildcatcher.data.model.AnimalEntity
import com.game.wildcatcher.data.model.ShelterZone

data class ShelterRevenueReport(
    val totalHourlyRevenue: Long,
    val totalVisitorsPerHour: Int,
    val activeZoneCount: Int,
    val shelteredAnimalsCount: Int
)

class ShelterEconomyEngine {
    fun calculateHourlyRevenue(
        zones: List<ShelterZone>,
        shelteredAnimals: List<AnimalEntity>
    ): ShelterRevenueReport {
        var totalCoins = 0L
        var totalVisitors = 0
        var activeZones = 0

        val unlockedZones = zones.filter { it.isUnlocked }
        activeZones = unlockedZones.size

        for (zone in unlockedZones) {
            val animalsInThisZone = shelteredAnimals.filter { it.habitat == zone.habitatType }
            val zoneAnimalCount = animalsInThisZone.size
            
            // Base visitor attraction from zone capacity and ecoLevel
            val zoneVisitors = zone.visitorFlowRatePerHour * zone.ecoLevel + (zoneAnimalCount * 15)
            totalVisitors += zoneVisitors
            
            // Ticket revenue
            val ticketRevenue = zoneVisitors * zone.ticketPricePerVisitor
            
            // Extra animal popularity bonus
            val animalBonus = animalsInThisZone.sumOf { it.coinYieldPerHour }

            totalCoins += (ticketRevenue + animalBonus)
        }

        return ShelterRevenueReport(
            totalHourlyRevenue = totalCoins,
            totalVisitorsPerHour = totalVisitors,
            activeZoneCount = activeZones,
            shelteredAnimalsCount = shelteredAnimals.size
        )
    }
}
