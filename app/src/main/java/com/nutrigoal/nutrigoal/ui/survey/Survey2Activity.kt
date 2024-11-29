package com.nutrigoal.nutrigoal.ui.survey

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivitySurvey2Binding
import com.nutrigoal.nutrigoal.utils.AnimationUtil

class Survey2Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySurvey2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurvey2Binding.inflate(layoutInflater)
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
                val intent = Intent(
                    this@Survey2Activity,
                    com.nutrigoal.nutrigoal.ui.survey.Survey3Activity::class.java
                )
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        with(binding) {
            val durationDefault = 1000L

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(tvStep2),
                AnimationUtil.createTranslationAnimator(tvTitle, durationDefault + 100),
                AnimationUtil.createTranslationAnimator(tvDesc, durationDefault + 200),
                AnimationUtil.createTranslationAnimator(edBodyWeight, durationDefault + 300),
                AnimationUtil.createTranslationAnimator(edHeight, durationDefault + 400),
                AnimationUtil.createTranslationAnimator(edAge, durationDefault + 500),
                AnimationUtil.createTranslationAnimator(tvGenderTitle, durationDefault + 600),
                AnimationUtil.createTranslationAnimator(groupRadio, durationDefault + 700),
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