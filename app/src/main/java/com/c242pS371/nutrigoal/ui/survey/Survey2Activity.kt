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
import com.c242pS371.nutrigoal.databinding.ActivitySurvey2Binding
import com.c242pS371.nutrigoal.ui.auth.LoginActivity.Companion.EXTRA_SURVEY
import com.c242pS371.nutrigoal.utils.AnimationUtil
import com.c242pS371.nutrigoal.utils.InputValidator

class Survey2Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySurvey2Binding
    private val inputValidator: InputValidator by lazy { InputValidator(this@Survey2Activity) }

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
        handleForm()

        with(binding) {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun handleForm() {
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
                val height = edHeight.text?.trim().toString()
                val bodyWeight = edBodyWeight.text?.trim().toString()
                val age = edAge.text?.trim().toString()

                userEntity?.age = age.toIntOrNull()
                userEntity?.height = height.toFloatOrNull()
                userEntity?.bodyWeight = bodyWeight.toFloatOrNull()

                val heightError = inputValidator.validateInput(edHeight, getString(R.string.height))
                val bodyWeightError =
                    inputValidator.validateInput(edBodyWeight, getString(R.string.body_weight))
                val ageError = inputValidator.validateInput(edAge, getString(R.string.age))

                inputValidator.checkValidation(tvErrorAge, ageError)
                inputValidator.checkValidation(tvErrorHeight, heightError)
                inputValidator.checkValidation(tvErrorBodyWeight, bodyWeightError)

                val selectedGenderId = groupRadio.checkedRadioButtonId

                userEntity?.gender = when (selectedGenderId) {
                    R.id.rb_male -> true
                    R.id.rb_female -> false
                    else -> null
                }

                if (ageError == null && heightError == null && bodyWeightError == null) {
                    val intent = Intent(
                        this@Survey2Activity,
                        Survey3Activity::class.java
                    )
                    intent.putExtra(EXTRA_SURVEY, userEntity)
                    startActivity(intent)
                }
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