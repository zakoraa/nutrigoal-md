package com.nutrigoal.nutrigoal.ui.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivityProfileBinding

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

    private fun setUpAction() {
        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setUpView() {
        val profileItems = listOf(
            ProfileItem(getString(R.string.username), "John Doe"),
            ProfileItem(getString(R.string.email), "johndoe@gmail.com"),
            ProfileItem(getString(R.string.age), "17"),
            ProfileItem(getString(R.string.gender), "Male"),
            ProfileItem(getString(R.string.body_weight), "70 Kg"),
            ProfileItem(getString(R.string.height), "170 Cm")
        )

        val adapter = ProfileItemAdapter(profileItems)
        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = object : LinearLayoutManager(this@ProfileActivity) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        }
    }
}