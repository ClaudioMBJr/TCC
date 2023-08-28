package com.omrsheetscanner.domain.mapper

import com.omrsheetscanner.data.database.entities.Exam
import com.omrsheetscanner.domain.model.MyExam

object ExamMapper {

    fun MyExam.toExamEntity() =
        Exam(
            title = title,
            description = description,
            options = options,
            questions = questions,
            examAnswers = examAnswers,
            maxScore = maxScore
        )

    fun Exam.toMyExam() =
        MyExam(
            title = title,
            description = description,
            options = options,
            questions = questions,
            examAnswers = examAnswers,
            maxScore = maxScore
        )
}