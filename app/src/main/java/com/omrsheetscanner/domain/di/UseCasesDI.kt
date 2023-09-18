package com.omrsheetscanner.domain.di

import com.omrsheetscanner.data.database.repository.ExamRepository
import com.omrsheetscanner.data.database.repository.StudentRepository
import com.omrsheetscanner.domain.use_cases.DeleteExamUseCase
import com.omrsheetscanner.domain.use_cases.GetExamsUseCase
import com.omrsheetscanner.domain.use_cases.GetStudentsUseCase
import com.omrsheetscanner.domain.use_cases.SaveExamUseCase
import com.omrsheetscanner.domain.use_cases.SaveStudentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCasesDI {

    @ViewModelScoped
    @Provides
    fun providesSaveExamUseCase(examRepository: ExamRepository) = SaveExamUseCase(examRepository)

    @ViewModelScoped
    @Provides
    fun providesGetExamsUseCase(examRepository: ExamRepository) = GetExamsUseCase(examRepository)

    @ViewModelScoped
    @Provides
    fun providesSaveStudentUseCase(studentRepository: StudentRepository) =
        SaveStudentUseCase(studentRepository)

    @ViewModelScoped
    @Provides
    fun providesGetStudentUseCase(studentRepository: StudentRepository) =
        GetStudentsUseCase(studentRepository)

    @ViewModelScoped
    @Provides
    fun providesDeleteExamUseCase(examRepository: ExamRepository, studentRepository: StudentRepository) =
        DeleteExamUseCase(examRepository, studentRepository)

}