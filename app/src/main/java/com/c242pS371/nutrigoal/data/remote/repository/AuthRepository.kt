package com.c242pS371.nutrigoal.data.remote.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.extension.asResultState
import com.c242pS371.nutrigoal.data.local.database.AuthPreference
import com.c242pS371.nutrigoal.data.local.database.SettingPreference
import com.c242pS371.nutrigoal.data.local.entity.UserLocalEntity
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.utils.ToastUtil
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val authPreference: AuthPreference,
    private val settingPreference: SettingPreference,
) {

    fun getCredentialResponse(
        context: Context,
    ): Flow<ResultState<GetCredentialResponse>> {
        return flow {
            val webClientId = com.c242pS371.nutrigoal.BuildConfig.WEB_CLIENT_ID

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

    fun getCurrentUser(): Flow<ResultState<UserEntity>> {
        return flow {
            val currentUser = Firebase.auth.currentUser
            lateinit var user: UserEntity
            currentUser?.let {
                user = UserEntity(
                    id = it.uid,
                    photoProfile = it.photoUrl.toString(),
                    username = it.displayName ?: "",
                    email = it.email ?: "",
                    age = 20,
                    gender = false,
                    bodyWeight = 70f,
                    height = 170f
                )
            }
            emit(user)
        }.asResultState()
    }

    fun loginWithGoogle(idToken: String): Flow<ResultState<FirebaseUser?>> {
        return flow {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user !== null) {
                authPreference.saveSession(
                    UserLocalEntity(
                        id = user.uid,
                        isLogin = true
                    )
                )
                emit(user)
            } else {
                emit(null)
            }
        }.asResultState()
    }

    fun registerWithEmailAndPassword(
        username: String,
        email: String,
        password: String
    ): Flow<ResultState<FirebaseUser?>> {
        return flow {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user.updateProfile(profileUpdates).await()
                    emit(user)
                } else {
                    emit(null)
                }
            } catch (e: FirebaseAuthException) {
                if (e.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                    ToastUtil.showToast(
                        context,
                        context.getString(R.string.error_account_already_exist)
                    )
                }
                emit(null)
            }
        }.asResultState()
    }

    fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<ResultState<FirebaseUser?>> {
        return flow {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user !== null) {
                authPreference.saveSession(
                    UserLocalEntity(
                        id = user.uid,
                        isLogin = true
                    )
                )
                emit(user)
            } else {
                emit(null)
            }
        }.asResultState()
    }

    fun logout(): Flow<ResultState<Unit>> {
        return flow {
            val sharedPreferences = context.getSharedPreferences("firstDietTime", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            Firebase.auth.signOut()
            settingPreference.clearSettings()
            emit(authPreference.logout())
        }.asResultState()
    }

    fun getSession(): Flow<ResultState<UserLocalEntity>> {
        return flow {
            emit(authPreference.getUserSession())
        }.asResultState()
    }

}
