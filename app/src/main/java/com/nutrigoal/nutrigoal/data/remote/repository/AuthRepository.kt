package com.nutrigoal.nutrigoal.data.remote.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.utils.asResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    fun getCredentialResponse(
        context: Context,
    ): Flow<ResultState<GetCredentialResponse>> {
        return flow {
            val webClientId = com.nutrigoal.nutrigoal.BuildConfig.WEB_CLIENT_ID

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context, request)
            emit(result)
        }.asResultState()
    }

    fun loginWithGoogle(idToken: String): Flow<ResultState<FirebaseUser?>> {
        return flow {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user !== null) {
                emit(user)
            } else {
                emit(null)
            }
        }.asResultState()
    }

}
