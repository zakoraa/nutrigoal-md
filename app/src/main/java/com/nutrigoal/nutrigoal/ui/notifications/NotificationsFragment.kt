package com.nutrigoal.nutrigoal.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.local.entity.NotificationLocalEntity
import com.nutrigoal.nutrigoal.data.local.entity.NotificationType
import com.nutrigoal.nutrigoal.databinding.FragmentNotificationsBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel
import com.nutrigoal.nutrigoal.ui.common.BoxSection
import com.nutrigoal.nutrigoal.ui.common.BoxSectionAdapter
import com.nutrigoal.nutrigoal.ui.settings.SettingsViewModel
import com.nutrigoal.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var boxSectionAdapter: BoxSectionAdapter<NotificationBoxContentItem>
    private val viewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val notificationsViewModel: NotificationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpView()
        setUpAction()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView() {
        lifecycleScope.launch {
            notificationsViewModel.notificationListState.collect { result ->
                handleGetAllNotifications(result)
            }
        }
    }

    private fun setUpAction() {
        notificationsViewModel.getAllNotifications()
    }

    private fun handleGetAllNotifications(result: ResultState<List<NotificationLocalEntity>>) {
        with(binding) {
            when (result) {
                is ResultState.Loading -> {
                    tvNoActivity.visibility = View.GONE
                }

                is ResultState.Success -> {
                    tvNoActivity.visibility = View.GONE
                    setUpAdapter(result.data)
                }

                is ResultState.Error -> {
                    tvNoActivity.visibility = View.VISIBLE
                    ToastUtil.showToast(
                        requireContext(),
                        getString(R.string.error_get_notification)
                    )
                }

                is ResultState.Initial -> {}
            }
        }

    }

    private fun setUpAdapter(result: List<NotificationLocalEntity>) {
        with(binding) {
            if (result.isNotEmpty()) {
                val sections = listOf(
                    BoxSection(
                        getString(R.string.today),
                        result.map { entity ->
                            val imageResId = when (entity.notificationType) {
                                NotificationType.CHECK_IN -> R.drawable.ic_check
                                NotificationType.TIME_TO_EAT -> R.drawable.ic_food
                            }

                            NotificationBoxContentItem(
                                id = entity.id,
                                imageResId = imageResId,
                                title = entity.title,
                                time = entity.time,
                                isConfirmed = entity.isConfirmed,
                                notificationType = entity.notificationType
                            )
                        }
                    )
                )

                boxSectionAdapter =
                    BoxSectionAdapter(sections, viewModel, settingsViewModel, notificationsViewModel, viewLifecycleOwner)
                recyclerView.adapter = boxSectionAdapter
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            } else {
                tvNoActivity.visibility = View.VISIBLE
            }

        }
    }

}