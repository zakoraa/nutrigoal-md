package com.c242pS371.nutrigoal.utils

import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse

object AppUtil {
    fun getTodayDataFromPerDay(historyResponse: HistoryResponse?): Int {
        val today = DateFormatter.getTodayDate()
        return historyResponse?.perDay?.indexOfFirst { date ->
            val createdAtDate = DateFormatter.parseDate(date.createdAt)
            createdAtDate == today
        } ?: -1
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
        return 0
    }
}