package com.omrsheetscanner.domain.use_cases

import com.omrsheetscanner.data.database.repository.StudentRepository
import com.omrsheetscanner.domain.mapper.StudentMapper.toStudent
import com.omrsheetscanner.domain.model.StudentGrade
import javax.inject.Inject

class SaveStudentUseCase @Inject constructor(private val studentRepository: StudentRepository) {

    suspend operator fun invoke(studentGrade: StudentGrade) {
        studentRepository.saveStudent(studentGrade.toStudent())
    }
}