package com.nutrigoal.nutrigoal.data.remote.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.local.database.AuthPreference
import com.nutrigoal.nutrigoal.data.local.entity.User
import com.nutrigoal.nutrigoal.data.remote.entity.Gender
import com.nutrigoal.nutrigoal.data.remote.entity.UserEntity
import com.nutrigoal.nutrigoal.utils.asResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth, private val authPreference: AuthPreference) {

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

    fun getCurrentUser(): Flow<ResultState<UserEntity>> {
        return flow {
            val currentUser = Firebase.auth.currentUser
            lateinit var user: UserEntity;
            currentUser?.let {
                user = UserEntity(
                    photoProfile = it.photoUrl.toString(),
                    username = it.displayName ?: "",
                    email = it.email ?: "",
                    age = 17,
                    gender = Gender.MALE,
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
                    User(
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
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user !== null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user.updateProfile(profileUpdates).await()
                emit(user)
            } else {
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
                    User(
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
            emit(authPreference.logout())
        }.asResultState()
    }

    fun getSession(): Flow<ResultState<User>> {
        return flow {
            emit(authPreference.getUserSession())
        }.asResultState()
    }

}
