package com.game.wildcatcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eco_quizzes")
data class EcoQuiz(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val correctIndex: Int, // 0 for A, 1 for B, 2 for C
    val explanation: String,
    val rewardCoins: Int = 150
)
