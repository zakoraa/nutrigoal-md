package com.nutrigoal.nutrigoal.ui.plan_diet

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FoodPagerAdapter(
    fragment: Fragment,
    private val dateList: List<DateItem>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = dateList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = FoodRecommendationFragment()
        fragment.arguments = Bundle().apply {
            putString("date", "${dateList[position].month} ${dateList[position].day}")
        }
        return fragment
    }
}
