package com.omrsheetscanner.domain.mapper

import com.omrsheetscanner.data.database.entities.Student
import com.omrsheetscanner.domain.model.StudentGrade

object StudentMapper {

    fun Student.toStudentGrade() =
        StudentGrade(
            name = name,
            grade = grade.toString()
        )

    fun StudentGrade.toStudent() =
        Student(
            name = name,
            grade = grade.toDouble()
        )
}