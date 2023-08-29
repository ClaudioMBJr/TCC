package com.omrsheetscanner.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.omrsheetscanner.common.ListConverter
import com.omrsheetscanner.data.database.dao.ExamDao
import com.omrsheetscanner.data.database.dao.StudentDao
import com.omrsheetscanner.data.database.entities.Exam
import com.omrsheetscanner.data.database.entities.Student

@Database(entities = [Exam::class, Student::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun studentDao(): StudentDao
}