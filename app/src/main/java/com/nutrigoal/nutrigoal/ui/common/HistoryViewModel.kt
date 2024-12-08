package com.nutrigoal.nutrigoal.ui.common

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrigoal.nutrigoal.data.ResultState
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

    val historyResponse = HistoryResponse()

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
            Log.d("HistoryViewModel", "userId: ${userId}")

            val isAlreadyAdded = historyRepository.isHistoryAlreadyAdded(userId)

            isAlreadyAdded.collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                    }

                    is ResultState.Success -> {
                        _isLoading.value = true

                        Log.d("HistoryViewModel", "Is history already added? ${result.data}")
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