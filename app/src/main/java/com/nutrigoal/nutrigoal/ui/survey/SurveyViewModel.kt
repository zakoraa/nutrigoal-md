package com.nutrigoal.nutrigoal.ui.survey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.remote.entity.SurveyRequest
import com.nutrigoal.nutrigoal.data.remote.repository.SurveyRepository
import com.nutrigoal.nutrigoal.data.remote.response.SurveyResponse
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

    private val _surveyResult = MutableLiveData<SurveyResponse>()
    val surveyResult: LiveData<SurveyResponse> = _surveyResult

    private val _surveyResponseState =
        MutableStateFlow<ResultState<SurveyResponse?>>(ResultState.Loading)
    val surveyResponseState: StateFlow<ResultState<SurveyResponse?>> get() = _surveyResponseState

    fun getSurveyResult(surveyRequest: SurveyRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            surveyRepository.getSurveyResult(surveyRequest).collect { result ->
                _surveyResponseState.value = result
                _isLoading.value = result is ResultState.Loading
                if (result is ResultState.Success) {
                    _surveyResult.value = result.data
                }
            }
        }
    }
}