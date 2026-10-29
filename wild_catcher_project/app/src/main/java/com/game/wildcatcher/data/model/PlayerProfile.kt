package com.game.wildcatcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfile(
    @PrimaryKey
    val playerId: Int = 1,
    val playerName: String = "野生動物守護員 戴夫",
    val coins: Long = 1200L,
    val goldenBambooSeeds: Int = 15,
    val playerLevel: Int = 1,
    val exp: Long = 0L,
    
    // Equipment Stats (Panda Gun)
    val pandaGunPowerLevel: Int = 1,     // Sweet spot capture efficiency multiplier
    val bubbleAmmoCount: Int = 50,       // Dream Bubble Net ammo
    val bambooIncenseAmmo: Int = 30,     // Bamboo Incense Dart ammo (calming effect)
    val maxAmmoCapacity: Int = 100,
    val bubbleNetRadiusLevel: Int = 1,   // Catch hit-box radius
    val vehicleLevel: Int = 1,           // Safari rover exploration speed
    
    // Dynamic Difficulty Adjustment state
    val consecutiveFailures: Int = 0,    // Triggers sweet-spot enlargement if >= 1
    val totalCaptures: Int = 0,
    
    // Health & Eye Care Tracking
    val totalActiveSeconds: Long = 0L,   // Continuous play time
    val eyeCareFilterActive: Boolean = false,
    val lastRestTimestamp: Long = 0L,
    val restRewardAvailable: Boolean = false
)
