package com.nutrigoal.nutrigoal.utils

import com.nutrigoal.nutrigoal.data.remote.entity.Gender

object AppUtil {
    fun getGenderCode(gender: String): Int {
        return if (gender == Gender.FEMALE.toString()) {
            2
        } else {
            1
        }
    }
}