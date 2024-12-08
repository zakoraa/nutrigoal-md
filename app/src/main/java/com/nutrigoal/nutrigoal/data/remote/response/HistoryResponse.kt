package com.nutrigoal.nutrigoal.data.remote.response

import com.nutrigoal.nutrigoal.data.remote.entity.PerDayItem

data class HistoryResponse(
	var userId: String? = null,
	var perDay: List<PerDayItem>? = null
)
