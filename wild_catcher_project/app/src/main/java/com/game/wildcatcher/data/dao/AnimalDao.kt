package com.game.wildcatcher.data.dao

import androidx.room.*
import com.game.wildcatcher.data.model.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals ORDER BY unlockLevel ASC, name ASC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE isCaptured = 1")
    fun getCapturedAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE isInShelter = 1")
    fun getShelterAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :animalId")
    suspend fun getAnimalById(animalId: String): AnimalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<AnimalEntity>)

    @Update
    suspend fun updateAnimal(animal: AnimalEntity)

    @Query("UPDATE animals SET isCaptured = 1, captureCount = captureCount + 1, isInShelter = 1 WHERE id = :animalId")
    suspend fun markCaptured(animalId: String)
}
