package com.c242pS371.nutrigoal.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity
import com.c242pS371.nutrigoal.data.local.entity.NotificationType
import com.c242pS371.nutrigoal.databinding.FragmentNotificationsBinding
import com.c242pS371.nutrigoal.ui.auth.AuthViewModel
import com.c242pS371.nutrigoal.ui.common.BoxSection
import com.c242pS371.nutrigoal.ui.common.BoxSectionAdapter
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.settings.SettingsViewModel
import com.c242pS371.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var boxSectionAdapter: BoxSectionAdapter<NotificationBoxContentItem>
    private val viewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()
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
                val currentDate = Calendar.getInstance().time

                val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.ENGLISH)

                val todayList = mutableListOf<NotificationBoxContentItem>()
                val yesterdayList = mutableListOf<NotificationBoxContentItem>()
                val oldestList = mutableListOf<NotificationBoxContentItem>()

                result.forEach { entity ->
                    val entityDate = dateFormat.parse(entity.time) ?: return@forEach
                    val entityCalendar = Calendar.getInstance().apply { time = entityDate }

                    when {
                        isToday(entityCalendar, currentDate) -> {
                            val imageResId = getNotificationImage(entity.notificationType)
                            todayList.add(
                                NotificationBoxContentItem(
                                    id = entity.id,
                                    imageResId = imageResId,
                                    title = entity.title,
                                    time = entity.time,
                                    isConfirmed = entity.isConfirmed,
                                    notificationType = entity.notificationType
                                )
                            )
                        }

                        isYesterday(entityCalendar, currentDate) -> {
                            val imageResId = getNotificationImage(entity.notificationType)
                            yesterdayList.add(
                                NotificationBoxContentItem(
                                    id = entity.id,
                                    imageResId = imageResId,
                                    title = entity.title,
                                    time = entity.time,
                                    isConfirmed = entity.isConfirmed,
                                    notificationType = entity.notificationType
                                )
                            )
                        }

                        else -> {
                            val imageResId = getNotificationImage(entity.notificationType)
                            oldestList.add(
                                NotificationBoxContentItem(
                                    id = entity.id,
                                    imageResId = imageResId,
                                    title = entity.title,
                                    time = entity.time,
                                    isConfirmed = entity.isConfirmed,
                                    notificationType = entity.notificationType
                                )
                            )
                        }
                    }
                }

                val sections = mutableListOf<BoxSection<NotificationBoxContentItem>>()

                if (todayList.isNotEmpty()) {
                    sections.add(BoxSection(getString(R.string.today), todayList))
                }
                if (yesterdayList.isNotEmpty()) {
                    sections.add(BoxSection(getString(R.string.yesterday), yesterdayList))
                }
                if (oldestList.isNotEmpty()) {
                    sections.add(BoxSection(getString(R.string.oldest), oldestList))
                }

                boxSectionAdapter = BoxSectionAdapter(
                    sections,
                    viewModel,
                    settingsViewModel,
                    notificationsViewModel,
                    historyViewModel,
                    viewLifecycleOwner
                )
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

    private fun getNotificationImage(notificationType: NotificationType): Int {
        return when (notificationType) {
            NotificationType.CHECK_IN -> R.drawable.ic_check
            NotificationType.TIME_TO_EAT -> R.drawable.ic_food
        }
    }

    private fun isToday(entityCalendar: Calendar, currentDate: Date): Boolean {
        val calendar = Calendar.getInstance().apply { time = currentDate }
        return entityCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                entityCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                entityCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun isYesterday(entityCalendar: Calendar, currentDate: Date): Boolean {
        val calendar = Calendar.getInstance().apply { time = currentDate }
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return entityCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                entityCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                entityCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

}