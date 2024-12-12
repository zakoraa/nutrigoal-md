package com.c242pS371.nutrigoal.ui.survey

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.databinding.ActivitySurvey1Binding
import com.c242pS371.nutrigoal.ui.auth.LoginActivity.Companion.EXTRA_SURVEY
import com.c242pS371.nutrigoal.utils.AnimationUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Survey1Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySurvey1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurvey1Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        setUpAction()
    }


    private fun setUpAction() {
        val userEntity = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                EXTRA_SURVEY,
                UserEntity::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURVEY)
        }

        with(binding) {

            btnNext.setOnClickListener {
                val intent = Intent(this@Survey1Activity, Survey2Activity::class.java)
                intent.putExtra(EXTRA_SURVEY, userEntity)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        with(binding) {
            val durationDefault = 1000L

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(ivSurvey1Decoration),
                AnimationUtil.createTranslationAnimator(tvStep1, durationDefault + 1300),
                AnimationUtil.createTranslationAnimator(tvTitle, durationDefault + 1600),
                AnimationUtil.createTranslationAnimator(tvDesc, durationDefault + 1900),
            )

            val together = AnimatorSet().apply {
                playTogether(animators)
            }

            AnimatorSet().apply {
                playSequentially(together)
                start()
            }
        }
    }

}