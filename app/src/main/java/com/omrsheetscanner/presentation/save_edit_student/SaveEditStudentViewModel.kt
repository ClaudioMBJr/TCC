package com.omrsheetscanner.presentation.save_edit_student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omrsheetscanner.domain.model.StudentGrade
import com.omrsheetscanner.domain.use_cases.SaveStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveEditStudentViewModel @Inject constructor(private val saveStudentUseCase: SaveStudentUseCase) :
    ViewModel() {

    fun saveStudent(studentGrade: StudentGrade) {
        viewModelScope.launch(Dispatchers.IO) {
            saveStudentUseCase(studentGrade)
        }
    }
}