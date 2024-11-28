package com.nutrigoal.nutrigoal.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.FragmentSettingsBinding
import com.nutrigoal.nutrigoal.ui.common.BoxSection
import com.nutrigoal.nutrigoal.ui.common.BoxSectionAdapter

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var boxSectionAdapter: BoxSectionAdapter<SettingBoxContentItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpView()

        return root
    }

    private fun setUpView() {
        with(binding) {
            val sections = listOf(
                BoxSection(
                    getString(R.string.account), listOf(
                        SettingBoxContentItem(title = getString(R.string.profile)),
                        SettingBoxContentItem(title = getString(R.string.update_password))
                    )
                ),
                BoxSection(
                    getString(R.string.content_and_display), listOf(
                        SettingBoxContentItem(
                            title = getString(R.string.notification),
                            isToggleButton = true
                        ),
                        SettingBoxContentItem(
                            title = getString(R.string.dark_mode),
                            isToggleButton = true
                        )
                    )
                ),
                BoxSection(
                    getString(R.string.authentication), listOf(
                        SettingBoxContentItem(
                            title = getString(R.string.logout),
                            endIconResId = R.drawable.ic_logout
                        )
                    )
                ),
            )
            boxSectionAdapter = BoxSectionAdapter(sections)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = boxSectionAdapter
        }
    }
}