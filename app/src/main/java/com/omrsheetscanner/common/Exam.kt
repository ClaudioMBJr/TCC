package com.omrsheetscanner.common

data class Exam(
    val title: String = "",
    val description: String = "",
    val options: Int = 2,
    val questions: Int = 10,
    val examAnswers: List<Pair<Int, String>> = emptyList(),
    val maxScore : Int = 10
)
