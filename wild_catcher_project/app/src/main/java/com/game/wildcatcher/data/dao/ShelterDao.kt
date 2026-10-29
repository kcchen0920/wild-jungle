package com.game.wildcatcher.data.dao

import androidx.room.*
import com.game.wildcatcher.data.model.ShelterZone
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelterDao {
    @Query("SELECT * FROM shelter_zones")
    fun getAllZones(): Flow<List<ShelterZone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zones: List<ShelterZone>)

    @Update
    suspend fun updateZone(zone: ShelterZone)

    @Query("UPDATE shelter_zones SET isUnlocked = 1 WHERE zoneId = :zoneId")
    suspend fun unlockZone(zoneId: String)
}
