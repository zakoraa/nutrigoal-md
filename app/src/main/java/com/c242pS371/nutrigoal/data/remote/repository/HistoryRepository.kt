package com.c242pS371.nutrigoal.data.remote.repository

import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.extension.asResultState
import com.c242pS371.nutrigoal.data.extension.historiesCollection
import com.c242pS371.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.c242pS371.nutrigoal.data.remote.entity.PerDayItem
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import com.c242pS371.nutrigoal.utils.DateFormatter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
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
    ): Flow<ResultState<HistoryResponse?>> {
        return flow {
            val snapshot = firestore.historiesCollection()
                .document(userId)
                .get()
                .await()

            val historyResponse = snapshot.toObject(HistoryResponse::class.java)

            if (historyResponse != null) {
                emit(historyResponse)
            } else {
                emit(null)
            }
        }.asResultState()
    }

    fun updateSelectedFoodRecommendation(
        userId: String,
        perDayId: String,
        foodRecommendationItem: List<FoodRecommendationItem>
    ): Flow<ResultState<Unit?>> {
        return flow {
            val documentRef = firestore.historiesCollection()
                .document(userId)

            val snapshot = documentRef.get().await()

            if (snapshot.exists()) {
                val historyResponse = snapshot.toObject(HistoryResponse::class.java)

                val perDayItem = historyResponse?.perDay?.find { it.id == perDayId }

                if (perDayItem != null) {
                    perDayItem.selectedFoodRecommendation =
                        foodRecommendationItem
                    documentRef.set(historyResponse).await()
                    emit(Unit)
                }
            }
        }.asResultState()
    }

    fun updateUserBodyWeightAndHeight(
        userId: String,
        perDayId: String,
        height: Float,
        bodyWeight: Float
    ): Flow<ResultState<Unit?>> {
        return flow {
            val documentRef = firestore.historiesCollection()
                .document(userId)

            val snapshot = documentRef.get().await()

            if (snapshot.exists()) {
                val historyResponse = snapshot.toObject(HistoryResponse::class.java)

                val perDayItem = historyResponse?.perDay?.find { it.id == perDayId }

                if (perDayItem != null) {
                    perDayItem.height = height
                    perDayItem.bodyWeight = bodyWeight
                    documentRef.set(historyResponse).await()
                    emit(Unit)
                }
            }
        }.asResultState()
    }

    fun addFoodRecommendation(
        userId: String,
        calorieNeeds: Float,
        foodRecommendation: List<FoodRecommendationItem>
    ): Flow<ResultState<Unit?>> {
        return flow {
            val documentRef = firestore.historiesCollection()
                .document(userId)

            val snapshot = documentRef.get().await()

            if (snapshot.exists()) {
                val historyResponse = snapshot.toObject(HistoryResponse::class.java)

                val lastPerDay = historyResponse?.perDay?.lastOrNull()

                if (lastPerDay != null) {
                    lastPerDay.calorieNeeds = calorieNeeds
                    val updatedFoodRecommendations =
                        lastPerDay.foodRecommendation?.toMutableList() ?: mutableListOf()
                    updatedFoodRecommendations.addAll(foodRecommendation)
                    lastPerDay.foodRecommendation = updatedFoodRecommendations

                    documentRef.set(historyResponse).await()
                    emit(Unit)
                } else {
                    emit(null)
                }
            } else {
                emit(null)
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
                    emit(Unit)
                } else {
                    emit(null)
                }

            } else {
                emit(null)
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

            val today = DateFormatter.getTodayDate()
            val alreadyAdded = historyResponse?.perDay?.any {
                val createdAtDate = DateFormatter.parseDate(it.createdAt)
                createdAtDate == today
            } ?: false
            emit(alreadyAdded)
        }.asResultState()
    }
}