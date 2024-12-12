package com.c242pS371.nutrigoal.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.c242pS371.nutrigoal.data.remote.entity.PerDayItem
import com.c242pS371.nutrigoal.data.remote.repository.HistoryRepository
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _historyResult = MutableLiveData<HistoryResponse>()
    val historyResult: LiveData<HistoryResponse> = _historyResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _historyResponseState =
        MutableStateFlow<ResultState<HistoryResponse?>>(ResultState.Initial)
    val historyResponseState: StateFlow<ResultState<HistoryResponse?>> get() = _historyResponseState

    private val _addHistoryResponseState =
        MutableStateFlow<ResultState<Unit?>>(ResultState.Initial)
    val addHistoryResponseState: StateFlow<ResultState<Unit?>> get() = _addHistoryResponseState

    private val _addPerDayItemState =
        MutableStateFlow<ResultState<Unit?>>(ResultState.Initial)
    val addPerDayItemState: StateFlow<ResultState<Unit?>> get() = _addPerDayItemState

    private val _addFoodRecommendationState =
        MutableStateFlow<ResultState<Unit?>>(ResultState.Initial)
    val addFoodRecommendationState: StateFlow<ResultState<Unit?>> get() = _addFoodRecommendationState

    private val _updateSelectedFoodRecommendationState =
        MutableStateFlow<ResultState<Unit?>>(ResultState.Initial)
    val updateSelectedFoodRecommendationState: StateFlow<ResultState<Unit?>> get() = _updateSelectedFoodRecommendationState

    private val _updateUserBodyWeightAndHeightState =
        MutableStateFlow<ResultState<Unit?>>(ResultState.Initial)
    val updateUserBodyWeightAndHeightState: StateFlow<ResultState<Unit?>> get() = _updateUserBodyWeightAndHeightState

    fun addPerDayItem(userId: String, perDayItem: PerDayItem) {
        viewModelScope.launch {
            historyRepository.addPerDayItem(userId, perDayItem).collect { result ->
                _addPerDayItemState.value = result
            }
        }
    }

    fun updateUserBodyWeightAndHeight(
        userId: String,
        perDayId: String,
        height: Float,
        bodyWeight: Float
    ) {
        viewModelScope.launch {
            historyRepository.updateUserBodyWeightAndHeight(
                userId,
                perDayId,
                height,
                bodyWeight
            ).collect { result ->
                _isLoading.value = result is ResultState.Loading
                _updateUserBodyWeightAndHeightState.value = result
            }
        }
    }

    fun updateSelectedFoodRecommendation(
        userId: String,
        perDayId: String,
        foodRecommendationItem: List<FoodRecommendationItem>
    ) {
        viewModelScope.launch {
            historyRepository.updateSelectedFoodRecommendation(
                userId,
                perDayId,
                foodRecommendationItem
            ).collect { result ->
                _isLoading.value = result is ResultState.Loading
                _updateSelectedFoodRecommendationState.value = result
            }
        }
    }

    fun addFoodRecommendation(
        userId: String,
        calorieNeeds: Float,
        foodRecommendation: List<FoodRecommendationItem>
    ) {
        viewModelScope.launch {
            historyRepository.addFoodRecommendation(userId, calorieNeeds, foodRecommendation)
                .collect { result ->
                    _isLoading.value = result is ResultState.Loading
                    _addFoodRecommendationState.value = result
                }
        }
    }

    fun getHistoryResult(userId: String) {
        viewModelScope.launch {
            historyRepository.getHistoryById(userId).collect { result ->
                _historyResponseState.value = result
                _isLoading.value = result is ResultState.Loading
                if (result is ResultState.Success) {
                    _historyResult.value = result.data
                }
            }
        }
    }

    fun addHistory(historyResponse: HistoryResponse) {
        viewModelScope.launch {
            val userId = historyResponse.userId ?: return@launch
            val isAlreadyAdded = historyRepository.isHistoryAlreadyAdded(userId)

            isAlreadyAdded.collect { result ->
                when (result) {
                    is ResultState.Loading -> {}
                    is ResultState.Success -> {
                        if (!result.data) {
                            historyRepository.addHistory(historyResponse).collect { addResult ->
                                _addHistoryResponseState.value = addResult
                            }
                        }
                    }

                    is ResultState.Error -> {}
                    is ResultState.Initial -> {}
                }
            }
        }
    }
}