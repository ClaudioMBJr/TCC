package com.omrsheetscanner.presentation.my_exam_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omrsheetscanner.domain.model.StudentGrade
import com.omrsheetscanner.domain.use_cases.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyExamInfoViewModel @Inject constructor(private val getStudentsUseCase: GetStudentsUseCase) :
    ViewModel() {

    private val _students: MutableLiveData<List<StudentGrade>> = MutableLiveData()
    val students: LiveData<List<StudentGrade>> = _students

    init {
        getStudents()
    }
    private fun getStudents() {
        viewModelScope.launch(Dispatchers.IO) {
            _students.postValue(getStudentsUseCase())
        }
    }
}