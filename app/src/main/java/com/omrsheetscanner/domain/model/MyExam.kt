package com.omrsheetscanner.domain.model

import java.io.Serializable

data class MyExam(
    val title: String = "",
    val description: String = "",
    val options: Int = 2,
    val questions: Int = 10,
    val examAnswers: List<Int> = emptyList(),
    val maxScore: Int = 10
) : Serializable
