package com.omrsheetscanner.domain.model

import java.io.Serializable

data class StudentGrade(
    val id : Int,
    val name: String,
    val grade: String,
    val examId : Int
) : Serializable
