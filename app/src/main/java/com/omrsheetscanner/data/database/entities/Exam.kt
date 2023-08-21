package com.omrsheetscanner.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val options: Int,
    val questions: Int,
    val examAnswers: List<Int>,
    val maxScore: Int
)
