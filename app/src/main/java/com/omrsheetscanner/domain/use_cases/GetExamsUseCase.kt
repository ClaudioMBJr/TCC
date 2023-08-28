package com.omrsheetscanner.domain.use_cases

import com.omrsheetscanner.data.database.repository.ExamRepository
import com.omrsheetscanner.domain.mapper.ExamMapper.toMyExam
import javax.inject.Inject

class GetExamsUseCase @Inject constructor(private val examRepository: ExamRepository) {

    operator fun invoke() =
        examRepository.getAllExams().map { it.toMyExam() }
}