package com.omrsheetscanner.domain.use_cases

import com.omrsheetscanner.data.database.repository.ExamRepository
import com.omrsheetscanner.data.database.repository.StudentRepository
import javax.inject.Inject

class DeleteExamUseCase @Inject constructor(private val examRepository: ExamRepository, private val studentRepository: StudentRepository) {

    operator fun invoke(examId : Int)  {
        examRepository.deleteExamById(examId)
        studentRepository.deleteAll()
    }
}