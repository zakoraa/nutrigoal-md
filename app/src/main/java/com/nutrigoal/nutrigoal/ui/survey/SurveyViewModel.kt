package com.nutrigoal.nutrigoal.ui.survey

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

    private val _surveyResponseState =
        MutableStateFlow<ResultState<SurveyResponse?>>(ResultState.Initial)
    val surveyResponseState: StateFlow<ResultState<SurveyResponse?>> get() = _surveyResponseState

    fun getSurveyResult(surveyRequest: SurveyRequest) {
        viewModelScope.launch {
            surveyRepository.getSurveyResult(surveyRequest).collect { result ->
                _surveyResponseState.value = result
            }
        }
    }
}