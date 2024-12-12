package com.c242pS371.nutrigoal.ui.plan_diet.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import com.c242pS371.nutrigoal.ui.plan_diet.FoodRecommendationFragment

class FoodPagerAdapter(
    fragment: Fragment,
    private val dateList: List<DateItem>,
    private val historyResponse: HistoryResponse?,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = dateList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = FoodRecommendationFragment()
        val perDayItem = historyResponse?.perDay?.get(position)

        fragment.arguments = Bundle().apply {
            putParcelable(HISTORY_DATA, historyResponse)
            putParcelable(PER_DAY, perDayItem)
        }
        return fragment
    }

    companion object {
        const val HISTORY_DATA = "history_data"
        const val PER_DAY = "per_day"
    }
}
