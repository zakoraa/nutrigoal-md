package com.nutrigoal.nutrigoal.utils

import com.nutrigoal.nutrigoal.data.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asResultState(): Flow<ResultState<T>> {
    return this
        .map<T, ResultState<T>> { ResultState.Success(it) }
        .onStart { emit(ResultState.Loading) }
        .catch { emit(ResultState.Error(it.message ?: "Unknown error")) }
}
