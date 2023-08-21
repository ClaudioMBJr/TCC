package com.omrsheetscanner.presentation.select_correct_answers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omrsheetscanner.domain.model.MyExam
import com.omrsheetscanner.domain.use_cases.SaveExamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCorrectAnswersViewModel @Inject constructor(private val saveExamUseCase: SaveExamUseCase) :
    ViewModel() {

    fun saveExam(myExam: MyExam) {
        viewModelScope.launch {
            saveExamUseCase(myExam)
        }
    }
}