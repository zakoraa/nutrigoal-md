package com.nutrigoal.nutrigoal.ui.survey

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivitySurvey3Binding
import com.nutrigoal.nutrigoal.ui.MainActivity
import com.nutrigoal.nutrigoal.utils.AnimationUtil

class Survey3Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySurvey3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurvey3Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        setUpView()
        setUpAction()
    }

    private fun setUpView() {
        val instantFoods = resources.getStringArray(R.array.instant_foods)

        val arrayAdapter = ArrayAdapter(this, R.layout.instant_food_dropdown_item, instantFoods)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun setUpAction() {
        with(binding) {
            btnBack.setOnClickListener {
                finish()
            }

            btnNext.setOnClickListener {
                val intent = Intent(this@Survey3Activity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        with(binding) {
            val durationDefault = 1000L

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(tvStep3),
                AnimationUtil.createTranslationAnimator(tvTitle, durationDefault + 100),
                AnimationUtil.createTranslationAnimator(tvDesc, durationDefault + 150),
                AnimationUtil.createTranslationAnimator(tvDietGoals, durationDefault + 200),
                AnimationUtil.createTranslationAnimator(rgGoal, durationDefault + 250),
                AnimationUtil.createTranslationAnimator(tvHistoryMag, durationDefault + 300),
                AnimationUtil.createTranslationAnimator(rgHistoryMag, durationDefault + 350),
                AnimationUtil.createTranslationAnimator(tvDietCategory, durationDefault + 400),
                AnimationUtil.createTranslationAnimator(rgDietCategory, durationDefault + 450),
                AnimationUtil.createTranslationAnimator(tvFavoriteProcessedFood, durationDefault + 500),
                AnimationUtil.createTranslationAnimator(edFavoriteProcessedFood, durationDefault + 550),
                AnimationUtil.createTranslationAnimator(tvFavoriteInstantFood, durationDefault + 600),
                AnimationUtil.createTranslationAnimator(autoCompleteTextView, durationDefault + 650),
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