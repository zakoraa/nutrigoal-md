package com.nutrigoal.nutrigoal.utils

import com.nutrigoal.nutrigoal.data.remote.entity.Gender
import com.nutrigoal.nutrigoal.data.remote.response.HistoryResponse

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
}