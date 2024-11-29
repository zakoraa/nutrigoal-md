package com.nutrigoal.nutrigoal.ui.survey

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivitySurvey1Binding
import com.nutrigoal.nutrigoal.utils.AnimationUtil

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
        with(binding) {
            btnBack.setOnClickListener {
                finish()
            }

            btnNext.setOnClickListener {
                val intent = Intent(this@Survey1Activity, Survey2Activity::class.java)
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