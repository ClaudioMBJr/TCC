package com.omrsheetscanner.domain.mapper

import com.omrsheetscanner.data.database.entities.Student
import com.omrsheetscanner.domain.model.StudentGrade

object StudentMapper {

    fun Student.toStudentGrade() =
        StudentGrade(
            id = id,
            name = name,
            grade = grade.toString(),
            examId = examId
        )

    fun StudentGrade.toStudent() =
        Student(
            id = id,
            name = name,
            grade = grade.toDouble(),
            examId = examId
        )
}