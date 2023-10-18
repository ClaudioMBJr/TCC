package com.omrsheetscanner.data.database.repository

import com.omrsheetscanner.data.database.dao.StudentDao
import com.omrsheetscanner.data.database.entities.Student
import javax.inject.Inject

class StudentRepository @Inject constructor(private val studentDao: StudentDao) {

    suspend fun saveStudent(student: Student) =
        studentDao.saveStudent(student)

    fun getStudentsById(examId: Int) =
        studentDao.getStudentsById(examId)

    fun deleteAll() = studentDao.deleteAll()
}