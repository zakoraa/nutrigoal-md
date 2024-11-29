package com.nutrigoal.nutrigoal.ui.auth

import android.content.Context
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.remote.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginWithGoogleState =
        MutableStateFlow<ResultState<FirebaseUser?>>(ResultState.Initial)
    val loginWithGoogleState: StateFlow<ResultState<FirebaseUser?>> get() = _loginWithGoogleState
    private val _credentialState =
        MutableStateFlow<ResultState<GetCredentialResponse>>(ResultState.Initial)
    val credentialState: StateFlow<ResultState<GetCredentialResponse>> get() = _credentialState

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
}
