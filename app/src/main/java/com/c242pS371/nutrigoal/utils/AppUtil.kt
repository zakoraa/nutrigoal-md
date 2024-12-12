package com.c242pS371.nutrigoal.utils

import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse

object AppUtil {
    fun getTodayDataFromPerDay(historyResponse: HistoryResponse?): Int {
        val today = DateFormatter.getTodayDate()
        historyResponse?.perDay?.mapIndexed { index, perDayItem ->
            val createdAt = DateFormatter.parseDate(perDayItem.createdAt)
            return if (createdAt == today) {
                index
            } else {
                0
            }
        }
        return -1
    }

    fun getDietTimeDataFromPerDay(historyResponse: HistoryResponse?): Int {

        val dietTime = DateFormatter.getTodayDate()
        historyResponse?.perDay?.mapIndexed { index, perDayItem ->
            val dietTimeCreated = DateFormatter.parseDate(perDayItem.dietTime)
            return if (dietTimeCreated == dietTime) {
                index
            } else {
                0
            }
        }
        return -1
    }
}