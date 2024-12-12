package com.c242pS371.nutrigoal.ui.survey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.remote.entity.SurveyRequest
import com.c242pS371.nutrigoal.data.remote.repository.SurveyRepository
import com.c242pS371.nutrigoal.data.remote.response.SurveyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val surveyRepository: SurveyRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _surveyResponseState =
        MutableStateFlow<ResultState<SurveyResponse?>>(ResultState.Initial)
    val surveyResponseState: StateFlow<ResultState<SurveyResponse?>> get() = _surveyResponseState

    fun getSurveyResult(surveyRequest: SurveyRequest) {
        viewModelScope.launch {
            surveyRepository.getSurveyResult(surveyRequest).collect { result ->
                _isLoading.value = result is ResultState.Loading
                _surveyResponseState.value = result
            }
        }
    }
}