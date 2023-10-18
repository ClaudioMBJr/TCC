package com.omrsheetscanner.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omrsheetscanner.data.database.entities.Student

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStudent(student: Student)

    @Query(
        "SELECT * FROM student where examId = :myExamId"
    )
    fun getStudentsById(myExamId : Int): List<Student>

    @Query("DELETE FROM student")
    fun deleteAll()
}