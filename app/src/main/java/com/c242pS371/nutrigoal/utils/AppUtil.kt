package com.c242pS371.nutrigoal.utils

import com.c242pS371.nutrigoal.data.remote.entity.Gender
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse

object AppUtil {
    fun getGenderCode(gender: String): Int {
        return if (gender == Gender.FEMALE.toString()) {
            2
        } else {
            1
        }
    }

    fun getTodayDataFromPerDay(historyResponse: HistoryResponse?): Int {
        val today = DateFormatter.getTodayDate()
        return historyResponse?.perDay?.indexOfFirst { date ->
            val createdAtDate = DateFormatter.parseDate(date.createdAt)
            createdAtDate == today
        } ?: -1
    }

    fun getDietTimeDataFromPerDay(historyResponse: HistoryResponse?): Int {
        val dietTime = DateFormatter.getTodayDate()
        return historyResponse?.perDay?.indexOfFirst { date ->
            val dietTimeDate = DateFormatter.parseDate(date.dietTime)
            dietTimeDate == dietTime
        } ?: -1
    }
}