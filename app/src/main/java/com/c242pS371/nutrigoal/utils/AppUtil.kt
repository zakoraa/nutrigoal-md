package com.c242pS371.nutrigoal.utils

import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse

object AppUtil {
    fun getTodayDataFromPerDay(historyResponse: HistoryResponse?): Int {
        val today = DateFormatter.getTodayDate()
        historyResponse?.perDay?.mapIndexed { index, perDayItem ->
            val createdAt = DateFormatter.parseDate(perDayItem.createdAt)
            if (createdAt == today) {
                return index
            }
        }
        return -1
    }

    fun getLastWeightFromPerDay(historyResponse: HistoryResponse?): Int {
        historyResponse?.perDay?.mapIndexed { index, perDayItem ->
            if (historyResponse.perDay?.size == 1) {
                return 0
            } else if (perDayItem.bodyWeight == null) {
                return index - 1
            }
        }
        return 0
    }

    fun getDietTimeDataFromPerDay(historyResponse: HistoryResponse?): Int {
        val dietTime = DateFormatter.getTodayDate()
        historyResponse?.perDay?.mapIndexed { index, perDayItem ->
            val dietTimeCreated = DateFormatter.parseDate(perDayItem.dietTime)
            if (dietTimeCreated == dietTime) {
                return index
            }
        }
        return 0
    }
}