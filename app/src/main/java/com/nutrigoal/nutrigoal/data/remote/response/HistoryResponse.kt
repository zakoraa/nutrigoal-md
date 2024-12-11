package com.nutrigoal.nutrigoal.data.remote.response

import android.os.Parcelable
import com.nutrigoal.nutrigoal.data.remote.entity.PerDayItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryResponse(
	var userId: String? = null,
	var  gender: Boolean? = null,
	var perDay: List<PerDayItem>? = null
): Parcelable
