package com.omrsheetscanner.domain.use_cases

import android.util.Log
import com.omrsheetscanner.data.database.entities.Exam
import com.omrsheetscanner.data.database.repository.ExamRepository
import com.omrsheetscanner.domain.mapper.ExamMapper.toMyExam
import com.omrsheetscanner.domain.model.MyExam
import javax.inject.Inject

class GetExamsUseCase @Inject constructor(private val examRepository: ExamRepository) {

    operator fun invoke() : List<MyExam> {
        val exam = examRepository.getAllExams().map { it.toMyExam() }
        Log.d("Correct question", exam[0].examAnswers.toString())
        return exam
    }
}