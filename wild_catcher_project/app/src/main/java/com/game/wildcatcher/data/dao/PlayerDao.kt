package com.game.wildcatcher.data.dao

import androidx.room.*
import com.game.wildcatcher.data.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_profile WHERE playerId = 1")
    fun getPlayerProfile(): Flow<PlayerProfile?>

    @Query("SELECT * FROM player_profile WHERE playerId = 1")
    suspend fun getPlayerProfileDirect(): PlayerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PlayerProfile)

    @Update
    suspend fun updateProfile(profile: PlayerProfile)

    @Query("UPDATE player_profile SET coins = coins + :amount WHERE playerId = 1")
    suspend fun addCoins(amount: Long)

    @Query("UPDATE player_profile SET consecutiveFailures = :count WHERE playerId = 1")
    suspend fun updateFailures(count: Int)

    @Query("UPDATE player_profile SET totalActiveSeconds = totalActiveSeconds + :deltaSeconds WHERE playerId = 1")
    suspend fun incrementPlayTime(deltaSeconds: Long)

    @Query("UPDATE player_profile SET eyeCareFilterActive = :active WHERE playerId = 1")
    suspend fun setEyeCareFilter(active: Boolean)
}
