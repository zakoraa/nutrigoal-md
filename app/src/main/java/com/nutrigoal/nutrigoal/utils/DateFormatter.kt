package com.nutrigoal.nutrigoal.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatToString(time: Date): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, h a", Locale.getDefault())
        return dateFormat.format(time)
    }
}