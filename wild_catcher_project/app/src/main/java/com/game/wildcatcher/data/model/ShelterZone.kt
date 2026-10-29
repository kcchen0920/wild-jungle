package com.game.wildcatcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelter_zones")
data class ShelterZone(
    @PrimaryKey
    val zoneId: String,
    val zoneName: String,
    val habitatType: HabitatType,
    val description: String,
    val currentAnimalCount: Int = 0,
    val maxCapacity: Int = 8,
    val visitorFlowRatePerHour: Int = 50,
    val ticketPricePerVisitor: Int = 10,
    val isUnlocked: Boolean = false,
    val unlockCost: Long = 0L,
    val ecoLevel: Int = 1
)
