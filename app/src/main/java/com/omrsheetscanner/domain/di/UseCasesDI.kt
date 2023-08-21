package com.omrsheetscanner.domain.di

import com.omrsheetscanner.data.database.repository.ExamRepository
import com.omrsheetscanner.domain.use_cases.SaveExamUseCase
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
}