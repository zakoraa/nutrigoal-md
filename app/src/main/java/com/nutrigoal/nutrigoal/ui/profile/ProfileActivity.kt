package com.nutrigoal.nutrigoal.ui.profile

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.remote.entity.Gender
import com.nutrigoal.nutrigoal.data.remote.response.HistoryResponse
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
                HistoryResponse::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(SettingBoxContentItemAdapter.EXTRA_USER)
        }

        if (user != null) {
            updateProfileData(user)
        }

    }

    private fun updateProfileData(user: HistoryResponse) {
        val firebaseAuth = Firebase.auth.currentUser
        Glide.with(this)
            .load(firebaseAuth?.photoUrl)
            .placeholder(R.drawable.photo_profile)
            .into(binding.ivPhotoProfile)

        val lastPerDay = user.perDay?.lastIndex ?: -1
        val perDay = user.perDay?.get(lastPerDay)

        val userGender = user.gender ?: false

        val gender = if (userGender) {
            Gender.MALE.toString()
        } else {
            Gender.FEMALE.toString()
        }

        val profileItems = listOf(
            ProfileItem(getString(R.string.username), firebaseAuth?.displayName ?: ""),
            ProfileItem(getString(R.string.email), firebaseAuth?.email ?: ""),
            ProfileItem(getString(R.string.age), perDay?.age.toString()),
            ProfileItem(getString(R.string.gender), gender),
            ProfileItem(getString(R.string.body_weight), "${perDay?.bodyWeight} Kg"),
            ProfileItem(getString(R.string.height), "${perDay?.height} Cm")
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
