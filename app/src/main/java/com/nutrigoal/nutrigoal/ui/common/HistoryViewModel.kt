package com.nutrigoal.nutrigoal.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.nutrigoal.nutrigoal.data.remote.entity.PerDayItem
import com.nutrigoal.nutrigoal.data.remote.repository.HistoryRepository
import com.nutrigoal.nutrigoal.data.remote.response.HistoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _historyResult = MutableLiveData<HistoryResponse>()
    val historyResult: LiveData<HistoryResponse> = _historyResult

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

    fun addPerDayItem(userId: String, perDayItem: PerDayItem) {
        _isLoading.value = true
        viewModelScope.launch {
            historyRepository.addPerDayItem(userId, perDayItem).collect { result ->
                _addPerDayItemState.value = result
            }
        }
        _isLoading.value = false
    }

    fun updateSelectedFoodRecommendation(
        userId: String,
        perDayId: String,
        foodRecommendationItem: List<FoodRecommendationItem>
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            historyRepository.updateSelectedFoodRecommendation(
                userId,
                perDayId,
                foodRecommendationItem
            ).collect { result ->
                _updateSelectedFoodRecommendationState.value = result
            }
        }
        _isLoading.value = false
    }

    fun addFoodRecommendation(
        userId: String,
        calorieNeeds: Float,
        foodRecommendation: List<FoodRecommendationItem>
    ) {
        viewModelScope.launch {
            _addFoodRecommendationState.value = ResultState.Loading
            historyRepository.addFoodRecommendation(userId, calorieNeeds, foodRecommendation)
                .collect { result ->
                    _addFoodRecommendationState.value = result
                }
        }
    }


    fun getHistoryResult(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            historyRepository.getHistoryById(userId).collect { result ->
                _historyResponseState.value = result
                if (result is ResultState.Success) {
                    _historyResult.value = result.data
                }
            }
            _isLoading.value = false
        }
    }

    fun addHistory(historyResponse: HistoryResponse) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = historyResponse.userId ?: return@launch
            val isAlreadyAdded = historyRepository.isHistoryAlreadyAdded(userId)

            isAlreadyAdded.collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                    }

                    is ResultState.Success -> {
                        _isLoading.value = true

                        if (!result.data) {
                            historyRepository.addHistory(historyResponse).collect { addResult ->
                                _addHistoryResponseState.value = addResult
                            }
                        }
                        _isLoading.value = true
                    }

                    is ResultState.Error -> {
                        _isLoading.value = false
                    }

                    is ResultState.Initial -> {
                    }
                }
            }
            _isLoading.value = false
        }
    }


}