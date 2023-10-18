package com.omrsheetscanner.domain.use_cases

import com.omrsheetscanner.data.database.repository.StudentRepository
import com.omrsheetscanner.domain.mapper.StudentMapper.toStudentGrade
import javax.inject.Inject

class GetStudentsUseCase @Inject constructor(private val studentRepository: StudentRepository) {

    operator fun invoke(myExamId: Int) =
        studentRepository.getStudentsById(myExamId).map { it.toStudentGrade() }
}