package com.nutrigoal.nutrigoal.ui.settings

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.SettingBoxContentItemBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel
import com.nutrigoal.nutrigoal.ui.profile.ProfileActivity
import com.nutrigoal.nutrigoal.utils.AlertDialogUtil
import com.nutrigoal.nutrigoal.utils.DailyReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingBoxContentItemAdapter(
    private val items: List<SettingBoxContentItem>,
    private val viewModel: AuthViewModel,
    private val settingsViewModel: SettingsViewModel,
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

    override fun onBindViewHolder(holder: SettingsBoxContentItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class SettingsBoxContentItemViewHolder(private val binding: SettingBoxContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingBoxContentItem) {
            with(binding) {
                tvTitle.text = item.title
                toggleButton.isChecked = false
                endIcon.setImageResource(item.endIconResId)
                val context = itemView.context

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
                            settingsViewModel.getDailyReminderSetting().observe(lifecycleOwner) { isEnabled ->
                                toggleButton.isChecked = isEnabled
                            }
                            val workManager = WorkManager.getInstance(context)

                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                            val periodicWorkRequest = PeriodicWorkRequest.Builder(
                                DailyReminderWorker::class.java,
                                1,
                                TimeUnit.HOURS
                            ).setConstraints(constraints)
                                .build()

                            toggleButton.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    workManager.enqueueUniquePeriodicWork(
                                        "DailyReminderWork",
                                        ExistingPeriodicWorkPolicy.KEEP,
                                        periodicWorkRequest
                                    )

                                    workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
                                        .observe(lifecycleOwner) { workInfo ->

                                            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                                                toggleButton.isChecked = true
                                                lifecycleOwner.lifecycleScope.launch {
                                                    settingsViewModel.saveDailyReminderSetting(true)
                                                }
                                            }

                                            if (workInfo?.state == WorkInfo.State.FAILED) {
                                                Toast.makeText(
                                                    context,
                                                    getString(context, R.string.unknown_error),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    workManager.cancelWorkById(periodicWorkRequest.id)
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
                                viewModel.currentUser.observe(lifecycleOwner) { user ->
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
                                    onAccept = { viewModel.logout() }
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