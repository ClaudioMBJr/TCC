package com.omrsheetscanner.data.database.di

import android.content.Context
import androidx.room.Room
import com.omrsheetscanner.data.database.AppDatabase
import com.omrsheetscanner.data.database.dao.ExamDao
import com.omrsheetscanner.data.database.repository.ExamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseDI {

    @Provides
    @Singleton
    fun createDB(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun providesAnimationsDB(appDatabase: AppDatabase): ExamDao = appDatabase.examDao()

    @Provides
    @Singleton
    fun providesExamRepository(examDao: ExamDao): ExamRepository =
        ExamRepository(examDao)
}