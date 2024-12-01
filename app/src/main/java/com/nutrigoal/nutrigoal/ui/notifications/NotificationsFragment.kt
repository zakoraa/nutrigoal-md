package com.nutrigoal.nutrigoal.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.FragmentNotificationsBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel
import com.nutrigoal.nutrigoal.ui.common.BoxSection
import com.nutrigoal.nutrigoal.ui.common.BoxSectionAdapter
import com.nutrigoal.nutrigoal.ui.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var boxSectionAdapter: BoxSectionAdapter<NotificationBoxContentItem>
    private val viewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpView()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView() {
        with(binding) {
            val sections = listOf(
                BoxSection(
                    getString(R.string.today), listOf(
                        NotificationBoxContentItem(
                            imageResId = R.drawable.ic_check,
                            title = getString(R.string.daily_check_in_title),
                            time = "11 November 2024, 10 AM"
                        ),
                        NotificationBoxContentItem(
                            imageResId = R.drawable.ic_food,
                            title = getString(R.string.eat_reminder_title),
                            time = "11 November 2024, 10 AM"
                        ),
                        NotificationBoxContentItem(
                            imageResId = R.drawable.ic_dont_eat_drink,
                            title = getString(R.string.no_eat_reminder_title),
                            time = "11 November 2024, 10 AM"
                        ),
                    )
                ),
            )

            boxSectionAdapter =
                BoxSectionAdapter(sections, viewModel, settingsViewModel, viewLifecycleOwner)
            recyclerView.adapter = boxSectionAdapter
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}