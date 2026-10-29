package com.game.wildcatcher.data.dao

import androidx.room.*
import com.game.wildcatcher.data.model.EcoQuiz
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM eco_quizzes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuiz(): EcoQuiz?

    @Query("SELECT * FROM eco_quizzes")
    fun getAllQuizzes(): Flow<List<EcoQuiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<EcoQuiz>)
}
