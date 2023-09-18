package com.omrsheetscanner.domain.model

import java.io.Serializable

data class StudentGrade(
    val id : Int = 0,
    val name: String,
    val grade: String
) : Serializable
