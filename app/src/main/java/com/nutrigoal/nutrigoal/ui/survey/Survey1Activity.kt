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

    private fun setUpAction(){
        with(binding){
            btnBack.setOnClickListener{
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

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(tvSurveyStep),
                AnimationUtil.createTranslationAnimator(progressBaseLine),
                AnimationUtil.createTranslationAnimator(progressLine),
                AnimationUtil.createTranslationAnimator(tvTitle),
                AnimationUtil.createTranslationAnimator(tvDesc),
                AnimationUtil.createTranslationAnimator(tvStep1),
                AnimationUtil.createTranslationAnimator(ivSurvey1Decoration),
                AnimationUtil.createTranslationAnimator(btnBack),
                AnimationUtil.createTranslationAnimator(btnNext),
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