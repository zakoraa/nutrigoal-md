package com.c242pS371.nutrigoal.ui.auth

import android.content.Context
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.local.entity.UserLocalEntity
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.data.remote.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _userLocalEntitySessionState =
        MutableStateFlow<ResultState<UserLocalEntity>>(ResultState.Loading)
    val userLocalEntitySessionState: StateFlow<ResultState<UserLocalEntity>> get() = _userLocalEntitySessionState
    private val _logoutState =
        MutableStateFlow<ResultState<Unit>>(ResultState.Initial)
    val logoutState: StateFlow<ResultState<Unit>> get() = _logoutState

    private val _loginWithGoogleState =
        MutableStateFlow<ResultState<FirebaseUser?>>(ResultState.Initial)
    val loginWithGoogleState: StateFlow<ResultState<FirebaseUser?>> get() = _loginWithGoogleState
    private val _credentialState =
        MutableStateFlow<ResultState<GetCredentialResponse>>(ResultState.Initial)
    val credentialState: StateFlow<ResultState<GetCredentialResponse>> get() = _credentialState

    private val _loginWithEmailAndPasswordState =
        MutableStateFlow<ResultState<FirebaseUser?>>(ResultState.Initial)
    val loginWithEmailAndPasswordState: StateFlow<ResultState<FirebaseUser?>> get() = _loginWithEmailAndPasswordState
    private val _registerWithEmailAndPasswordState =
        MutableStateFlow<ResultState<FirebaseUser?>>(ResultState.Initial)
    val registerWithEmailAndPasswordState: StateFlow<ResultState<FirebaseUser?>> get() = _registerWithEmailAndPasswordState

    private val _currentUserState =
        MutableStateFlow<ResultState<UserEntity?>>(ResultState.Initial)
    val currentUserState: StateFlow<ResultState<UserEntity?>> get() = _currentUserState
    private val _currentUser = MutableLiveData<UserEntity?>(null)
    val currentUser: LiveData<UserEntity?> get() = _currentUser

    fun setCurrentUser(user: UserEntity?) {
        _currentUser.value = user
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                _currentUserState.value = result
            }
        }
    }

    fun getCredentialResponse(context: Context) {
        viewModelScope.launch {
            authRepository.getCredentialResponse(context)
                .collect { result ->
                    _credentialState.value = result
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken)
                .collect { result ->
                    _loginWithGoogleState.value = result
                }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email, password)
                .collect { result ->
                    _loginWithEmailAndPasswordState.value = result
                }
        }
    }

    fun registerWithEmailAndPassword(username: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.registerWithEmailAndPassword(username, email, password)
                .collect { result ->
                    _registerWithEmailAndPasswordState.value = result
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                _logoutState.value = result
            }
        }
    }

    fun getSession() {
        viewModelScope.launch {
            authRepository.getSession()
                .collect { result ->
                    _userLocalEntitySessionState.value = result
                }
        }
    }

}
