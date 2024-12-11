package com.c242pS371.nutrigoal.data

sealed class ResultState<out R> private constructor() {
    data object Initial : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val error: String) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
}