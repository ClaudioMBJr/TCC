package com.omrsheetscanner.presentation.my_exam_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omrsheetscanner.domain.model.StudentGrade
import com.omrsheetscanner.domain.use_cases.DeleteExamUseCase
import com.omrsheetscanner.domain.use_cases.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyExamInfoViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val deleteExamUseCase: DeleteExamUseCase
) :
    ViewModel() {

    private val _students: MutableLiveData<List<StudentGrade>> = MutableLiveData()
    val students: LiveData<List<StudentGrade>> = _students

    fun getStudents(myExamId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _students.postValue(getStudentsUseCase(myExamId))
        }
    }

    fun deleteExam(examId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteExamUseCase.invoke(examId)
        }
    }
}