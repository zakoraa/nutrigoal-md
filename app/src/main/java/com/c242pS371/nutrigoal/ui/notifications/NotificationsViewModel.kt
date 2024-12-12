package com.c242pS371.nutrigoal.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity
import com.c242pS371.nutrigoal.data.local.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notificationListState =
        MutableStateFlow<ResultState<List<NotificationLocalEntity>>>(ResultState.Initial)
    val notificationListState: StateFlow<ResultState<List<NotificationLocalEntity>>> get() = _notificationListState

    fun getAllNotifications() {
        viewModelScope.launch {
            notificationRepository.getAllNotifications().collect { result ->
                _notificationListState.value = result
            }
        }
    }

    fun updateNotificationAsConfirmed(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.updateIsConfirmed(id)
        }
    }
}
