package com.omrsheetscanner.domain.model

import java.io.Serializable

data class MyExam(
    val id : Int = 0,
    val title: String = "",
    val description: String = "",
    val options: Int = 2,
    val questions: Int = 10,
    val examAnswers: List<Int> = emptyList(),
    val maxScore: Int = 10
) : Serializable {

    fun getColumns() = when(questions) {
        in 0..25 -> 1
        in 26..50 -> 2
        in 51..75 -> 3
        else -> 4
    }
}

