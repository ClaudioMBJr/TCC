package com.omrsheetscanner.data.database.entities

import androidx.room.Entity

@Entity
data class Student(
    val name: String,
    val grade: Double
)
