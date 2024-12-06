package com.nutrigoal.nutrigoal.ui.plan_diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.databinding.FragmentPlanDietBinding

class PlanDietFragment : Fragment() {
    private var _binding: FragmentPlanDietBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanDietBinding.inflate(inflater, container, false)

        setUpView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView() {
        val dateList = listOf(
            DateItem("Des", "20"),
            DateItem("Des", "21"),
            DateItem("Des", "22"),
            DateItem("Des", "23"),
            DateItem("Des", "24"),
            DateItem("Des", "25"),
            DateItem("Des", "26"),
        )

        val adapter = DateAdapter(dateList)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

    }

}