package com.omrsheetscanner.presentation.my_exams

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omrsheetscanner.domain.model.MyExam
import com.omrsheetscanner.domain.use_cases.GetExamsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyExamsViewModel @Inject constructor(private val getExamsUseCase: GetExamsUseCase) :
    ViewModel() {

    private val _myExams: MutableLiveData<List<MyExam>> = MutableLiveData()
    val myExams: LiveData<List<MyExam>> = _myExams

    init {
        getAllExams()
    }

    private fun getAllExams() {
        viewModelScope.launch(Dispatchers.IO) {
            _myExams.postValue(getExamsUseCase())
        }
    }
}