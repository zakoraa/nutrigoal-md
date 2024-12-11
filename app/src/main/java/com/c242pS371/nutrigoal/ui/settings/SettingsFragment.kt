package com.c242pS371.nutrigoal.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.databinding.FragmentSettingsBinding
import com.c242pS371.nutrigoal.ui.MainActivity
import com.c242pS371.nutrigoal.ui.auth.AuthViewModel
import com.c242pS371.nutrigoal.ui.common.BoxSection
import com.c242pS371.nutrigoal.ui.common.BoxSectionAdapter
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.notifications.NotificationsViewModel
import com.c242pS371.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var boxSectionAdapter: BoxSectionAdapter<SettingBoxContentItem>
    private val viewModel: AuthViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()
    private val notificationsViewModel: NotificationsViewModel by activityViewModels()

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
        setUpSettingSections()

        lifecycleScope.launch {
            viewModel.logoutState.collect { result ->
                handleLogout(result)
            }
        }
    }

    private fun handleLogout(result: ResultState<Unit>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                ToastUtil.showToast(requireContext(), getString(R.string.logout_success))
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }

            is ResultState.Error -> {
                ToastUtil.showToast(requireContext(), getString(R.string.error_logout))
            }

            is ResultState.Initial -> {}
        }
    }

    private fun setUpSettingSections() {
        with(binding) {
            val sections = listOf(
                BoxSection(
                    getString(R.string.account), listOf(
                        SettingBoxContentItem(title = getString(R.string.profile)),
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

            boxSectionAdapter =
                BoxSectionAdapter(
                    sections,
                    viewModel,
                    settingsViewModel,
                    notificationsViewModel,
                    historyViewModel,
                    viewLifecycleOwner
                )
            recyclerView.adapter = boxSectionAdapter
            recyclerView.setHasFixedSize(true)
            recyclerView.setLayoutManager(object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            })

        }
    }
}