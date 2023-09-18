package com.omrsheetscanner.data.database.repository

import com.omrsheetscanner.data.database.dao.ExamDao
import com.omrsheetscanner.data.database.entities.Exam
import javax.inject.Inject

class ExamRepository @Inject constructor(private val examDao: ExamDao) {

    fun getAllExams(): List<Exam> =
        examDao.getAllExams()

    suspend fun saveExam(exam: Exam) =
        examDao.saveExam(exam)

    fun deleteExamById(examId : Int) = examDao.deleteExamById(examId)
}