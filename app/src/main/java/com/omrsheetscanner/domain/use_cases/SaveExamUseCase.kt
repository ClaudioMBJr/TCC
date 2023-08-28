package com.omrsheetscanner.domain.use_cases

import com.omrsheetscanner.data.database.repository.ExamRepository
import com.omrsheetscanner.domain.mapper.ExamMapper.toExamEntity
import com.omrsheetscanner.domain.model.MyExam
import javax.inject.Inject

class SaveExamUseCase @Inject constructor(private val examRepository: ExamRepository) {

    suspend operator fun invoke(myExam: MyExam) {
        examRepository.saveExam(myExam.toExamEntity())
    }
}