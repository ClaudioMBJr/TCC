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
        10 -> 1
        20 -> 1
        30 -> 2
        40 -> 2
        50 -> 2
        60 -> 3
        70 -> 2
        80 -> 4
        90 -> 3
        100 -> 4
        else -> 0
    }
    fun getRows() = when(questions) {
        10 -> 10
        20 -> 20
        30 -> 15
        40 -> 20
        50 -> 25
        60 -> 20
        70 -> 35
        80 -> 20
        90 -> 30
        100 -> 25
        else -> 0
    }
}

