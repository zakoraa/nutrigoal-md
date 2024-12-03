package com.nutrigoal.nutrigoal.ui.profile

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.remote.entity.UserEntity
import com.nutrigoal.nutrigoal.databinding.ActivityProfileBinding
import com.nutrigoal.nutrigoal.ui.settings.SettingBoxContentItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpView()
        setUpAction()
    }

    private fun setUpView() {
        val user = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                SettingBoxContentItemAdapter.EXTRA_USER,
                UserEntity::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(SettingBoxContentItemAdapter.EXTRA_USER)
        }

        if (user != null) {
            updateProfileData(user)
        }

    }

    private fun updateProfileData(user: UserEntity) {
         Glide.with(this)
            .load(user.photoProfile)
            .placeholder(R.drawable.photo_profile)
            .into(binding.ivPhotoProfile)

        val profileItems = listOf(
            ProfileItem(getString(R.string.username), user.username),
            ProfileItem(getString(R.string.email), user.email),
            ProfileItem(getString(R.string.age), user.age.toString()),
            ProfileItem(getString(R.string.gender), user.gender.toString()),
            ProfileItem(getString(R.string.body_weight), "${user.bodyWeight} Kg"),
            ProfileItem(getString(R.string.height), "${user.height} Cm")
        )

        val adapter = ProfileItemAdapter(profileItems)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            isSmoothScrollbarEnabled = true
        }
    }

    private fun setUpAction() {
        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }
        }
    }
}
