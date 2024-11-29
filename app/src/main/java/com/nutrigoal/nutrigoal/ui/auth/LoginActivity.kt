package com.nutrigoal.nutrigoal.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivityLoginBinding
import com.nutrigoal.nutrigoal.utils.AnimationUtil


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initAction()
        playAnimation()

        binding.btnLoginWithGoogle.setOnClickListener {}
    }

    private fun initView() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initAction() {
        with(binding) {
            edEmail.setInputName(getString(R.string.email))
            edPassword.setInputName(getString(R.string.password))

            tvDirectToRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnLogin.setOnClickListener {
                login()
            }

        }
    }

    private fun login() {
        with(binding) {
            val email = edEmail.text.toString()
            val password = edPassword.text.toString()

//            if (email.isEmpty()) {
//                showToast(getString(R.string.error_empty_field, getString(R.string.email)))
//            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                showToast(getString(R.string.error_wrong_email_format))
//            } else if (password.isEmpty()) {
//                showToast(getString(R.string.error_empty_field, getString(R.string.password)))
//            } else if (password.length < 8) {
//                showToast(
//                    getString(
//                        R.string.error_min_length_field, getString(R.string.password), 8
//                    )
//                )
//            } else {
//
//            }
            val intent = Intent(
                this@LoginActivity,
                com.nutrigoal.nutrigoal.ui.survey.Survey1Activity::class.java
            )
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {

        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        with(binding) {
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(decoration, View.TRANSLATION_X, 100f, 0f),
                    ObjectAnimator.ofFloat(decoration, View.ALPHA, 0f, 1f)
                )
                duration = 2000
                start()
            }

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(ivAppLogo),
                AnimationUtil.createTranslationAnimator(tvTitle),
                AnimationUtil.createTranslationAnimator(tvDesc),
                AnimationUtil.createTranslationAnimator(edEmail),
                AnimationUtil.createTranslationAnimator(edPassword),
                AnimationUtil.createTranslationAnimator(btnLogin),
                AnimationUtil.createTranslationAnimator(llBellowBtn),
                AnimationUtil.createTranslationAnimator(dividerLeft),
                AnimationUtil.createTranslationAnimator(dividerRight),
                AnimationUtil.createTranslationAnimator(tvOr),
                AnimationUtil.createTranslationAnimator(btnLoginWithGoogle),
                AnimationUtil.createTranslationAnimator(btnLoginWithFacebook)
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