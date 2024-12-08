package com.nutrigoal.nutrigoal.data.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.extension.historiesCollection
import com.nutrigoal.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.nutrigoal.nutrigoal.data.remote.entity.PerDayItem
import com.nutrigoal.nutrigoal.data.remote.response.HistoryResponse
import com.nutrigoal.nutrigoal.utils.asResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class HistoryRepository(private val firestore: FirebaseFirestore) {

    fun addHistory(
        historyResponse: HistoryResponse
    ): Flow<ResultState<Unit?>> {
        return flow {
            firestore.historiesCollection()
                .document(historyResponse.userId ?: UUID.randomUUID().toString())
                .set(historyResponse)
                .await()

            emit(null)
        }.asResultState()
    }

    fun getHistoryById(
        userId: String
    ): Flow<ResultState<HistoryResponse>> {
        return flow {
            val snapshot = firestore.historiesCollection()
                .document(userId)
                .get()
                .await()

            val historyResponse = snapshot.toObject(HistoryResponse::class.java)

            if (historyResponse != null) {
                emit(historyResponse)
            }
        }.asResultState()
    }

    fun updateFoodRecommendationById(
        userId: String,
        perDayId: String,
        foodRecommendationItem: FoodRecommendationItem
    ): Flow<ResultState<Unit?>> {
        return flow {
            val documentRef = firestore.historiesCollection()
                .document(userId)

            val snapshot = documentRef.get().await()

            if (snapshot.exists()) {
                val historyResponse = snapshot.toObject(HistoryResponse::class.java)

                val perDayItem = historyResponse?.perDay?.find { it.id == perDayId }

                if (perDayItem != null) {
                    val updatedFoodRecommendations =
                        perDayItem.foodRecommendation?.toMutableList() ?: mutableListOf()
                    updatedFoodRecommendations.add(foodRecommendationItem)
                    perDayItem.foodRecommendation = updatedFoodRecommendations
                    documentRef.set(historyResponse).await()

                    emit(Unit)
                }
            }
        }.asResultState()
    }


    fun addPerDayItem(
        userId: String,
        perDayItem: PerDayItem
    ): Flow<ResultState<Unit?>> {
        return flow {
            val documentRef = firestore.historiesCollection()
                .document(userId)

            val snapshot = documentRef.get().await()

            if (snapshot.exists()) {
                val historyResponse = snapshot.toObject(HistoryResponse::class.java)

                val updatedPerDayList = historyResponse?.perDay?.toMutableList() ?: mutableListOf()
                updatedPerDayList.add(perDayItem)

                historyResponse?.perDay = updatedPerDayList

                if (historyResponse != null) {
                    documentRef.set(historyResponse).await()
                }

                emit(Unit)
            }
        }.asResultState()
    }

    fun isHistoryAlreadyAdded(userId: String): Flow<ResultState<Boolean>> {
        return flow {
            val snapshot = firestore.historiesCollection()
                .document(userId)
                .get()
                .await()

            val historyResponse = snapshot.toObject(HistoryResponse::class.java)

            val today = getTodayDate()
            val alreadyAdded = historyResponse?.perDay?.any {
                val createdAtDate = parseDate(it.createdAt)
                createdAtDate == today
            } ?: false
            emit(alreadyAdded)
        }.asResultState()
    }

    private fun parseDate(dateString: String?): String? {
        if (dateString == null) return null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        return try {
            val date = format.parse(dateString)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

            String.format(
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format("%04d-%02d-%02d", year, month, day)
    }
}