package com.omrsheetscanner.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omrsheetscanner.data.database.entities.Exam

@Dao
interface ExamDao {

    @Query("SELECT * FROM exam")
    fun getAllExams(): List<Exam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExam(exam: Exam)

    @Query("DELETE FROM exam where id =:examId")
    fun deleteExamById(examId : Int)

}