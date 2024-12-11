package com.c242pS371.nutrigoal.ui.settings

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.work.DailyReminderService
import com.c242pS371.nutrigoal.data.work.DailyReminderWorker
import com.c242pS371.nutrigoal.data.work.InsertDailyCheckInNotificationWorker
import com.c242pS371.nutrigoal.databinding.SettingBoxContentItemBinding
import com.c242pS371.nutrigoal.ui.auth.AuthViewModel
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.profile.ProfileActivity
import com.c242pS371.nutrigoal.utils.AlertDialogUtil
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingBoxContentItemAdapter(
    private val items: List<SettingBoxContentItem>,
    private val viewModel: AuthViewModel,
    private val settingsViewModel: SettingsViewModel,
    private val historyViewModel: HistoryViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<SettingBoxContentItemAdapter.SettingsBoxContentItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingsBoxContentItemViewHolder {
        val binding =
            SettingBoxContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsBoxContentItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SettingsBoxContentItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class SettingsBoxContentItemViewHolder(private val binding: SettingBoxContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: SettingBoxContentItem) {
            with(binding) {
                tvTitle.text = item.title
                toggleButton.isChecked = false
                endIcon.setImageResource(item.endIconResId)
                val context = itemView.context

                val serviceIntent = Intent(context, DailyReminderService::class.java)
                val workManager =
                    WorkManager.getInstance(context)

                if (item.isToggleButton) {
                    toggleButton.visibility = View.VISIBLE
                    endIcon.visibility = View.GONE
                    when (item.title) {
                        getString(context, R.string.dark_mode) -> {
                            settingsViewModel.getThemeSettings()
                                .observe(lifecycleOwner) { isDarkModeActive: Boolean ->
                                    toggleButton.isChecked = isDarkModeActive
                                }

                            toggleButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                                settingsViewModel.saveThemeSetting(isChecked)
                            }

                        }

                        getString(context, R.string.notification) -> {
                            settingsViewModel.getDailyReminderSetting()
                                .observe(lifecycleOwner) { isEnabled ->
                                    toggleButton.isChecked = isEnabled
                                }
                            val currentTime = Calendar.getInstance()

                            toggleButton.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    context.startForegroundService(serviceIntent)

                                    val targetTime = Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, 9)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)

                                        if (currentTime.after(this)) {
                                            add(Calendar.DAY_OF_YEAR, 1)
                                        }
                                    }

                                    val timeUntilTarget =
                                        targetTime.timeInMillis - currentTime.timeInMillis

                                    val constraints = Constraints.Builder()
                                        .build()

                                    val dailyReminderRequest = PeriodicWorkRequest.Builder(
                                        DailyReminderWorker::class.java,
                                        1, TimeUnit.DAYS
                                    )
                                        .setInitialDelay(timeUntilTarget, TimeUnit.MILLISECONDS)
                                        .setConstraints(constraints)
                                        .build()

                                    val checkInNotificationRequest = PeriodicWorkRequest.Builder(
                                        InsertDailyCheckInNotificationWorker::class.java,
                                        1, TimeUnit.DAYS
                                    )
                                        .setInitialDelay(timeUntilTarget, TimeUnit.MILLISECONDS)
                                        .setConstraints(constraints)
                                        .build()

                                    workManager.getWorkInfosForUniqueWorkLiveData("DailyReminderWorker")
                                        .observe(lifecycleOwner) { workInfos ->
                                            val isWorkerScheduled = workInfos?.any { workInfo ->
                                                workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
                                            } ?: false

                                            if (!isWorkerScheduled) {
                                                workManager.enqueueUniquePeriodicWork(
                                                    "DailyReminderWorker",
                                                    ExistingPeriodicWorkPolicy.KEEP,
                                                    dailyReminderRequest
                                                )
                                            }
                                        }

                                    workManager.getWorkInfosForUniqueWorkLiveData("InsertDailyCheckInNotificationWorker")
                                        .observe(lifecycleOwner) { workInfos ->
                                            val isWorkerScheduled = workInfos?.any { workInfo ->
                                                workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
                                            } ?: false

                                            if (!isWorkerScheduled) {
                                                workManager.enqueueUniquePeriodicWork(
                                                    "InsertDailyCheckInNotificationWorker",
                                                    ExistingPeriodicWorkPolicy.KEEP,
                                                    checkInNotificationRequest
                                                )
                                            }
                                        }

                                    workManager.getWorkInfoByIdLiveData(dailyReminderRequest.id)
                                        .observe(lifecycleOwner) { workInfo ->
                                            if (workInfo?.state == WorkInfo.State.FAILED) {
                                                Toast.makeText(
                                                    context,
                                                    getString(context, R.string.unknown_error),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    toggleButton.isChecked = false
                                    context.stopService(serviceIntent)
                                    workManager.cancelAllWork()
                                    toggleButton.isChecked = false
                                    lifecycleOwner.lifecycleScope.launch {
                                        settingsViewModel.saveDailyReminderSetting(false)
                                    }
                                }
                                lifecycleOwner.lifecycleScope.launch {
                                    settingsViewModel.saveDailyReminderSetting(isChecked)
                                }
                            }
                        }
                    }
                } else {
                    toggleButton.visibility = View.GONE
                    endIcon.visibility = View.VISIBLE

                    when (item.title) {
                        getString(context, R.string.profile) -> {
                            itemView.setOnClickListener {
                                val intent = Intent(context, ProfileActivity::class.java)
                                historyViewModel.historyResult.observe(lifecycleOwner) { user ->
                                    intent.putExtra(EXTRA_USER, user)
                                }
                                context.startActivity(intent)

                            }
                        }

                        getString(context, R.string.logout) -> {
                            itemView.setOnClickListener {
                                AlertDialogUtil.showDialog(
                                    context,
                                    context.getString(R.string.confirm_logout_message),
                                    onAccept = {
                                        context.stopService(serviceIntent)
                                        workManager.cancelAllWork()
                                        viewModel.logout()
                                    }
                                )
                            }
                        }

                    }
                }
            }
        }
    }


    companion object {
        const val EXTRA_USER = "extra_user"
    }
}

data class SettingBoxContentItem(
    val title: String,
    var isToggleButton: Boolean = false,
    val endIconResId: Int = R.drawable.ic_arrow_forward
)