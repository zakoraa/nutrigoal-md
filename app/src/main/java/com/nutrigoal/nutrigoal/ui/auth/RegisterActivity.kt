package com.nutrigoal.nutrigoal.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivityRegisterBinding
import com.nutrigoal.nutrigoal.utils.AnimationUtil

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initAction()
        playAnimation()
    }

    private fun initView() {
    }

    private fun initAction() {
        with(binding) {
            edUsername.setInputName(getString(R.string.username))
            edEmail.setInputName(getString(R.string.email))
            edPassword.setInputName(getString(R.string.password))

            tvBackToLogin.setOnClickListener {
                finish()
            }
            btnRegister.setOnClickListener {
                register()
            }
        }
    }

    private fun register() {
        with(binding) {
            val username = edUsername.text.toString()
            val email = edEmail.text.toString()
            val password = edPassword.text.toString()

            if (username.isEmpty()) {
                showToast(getString(R.string.error_empty_field, getString(R.string.username)))
            } else if (email.isEmpty()) {
                showToast(getString(R.string.error_empty_field, getString(R.string.email)))
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(getString(R.string.error_wrong_email_format))
            } else if (password.isEmpty()) {
                showToast(getString(R.string.error_empty_field, getString(R.string.password)))
            } else if (password.length < 8) {
                showToast(
                    getString(
                        R.string.error_min_length_field,
                        getString(R.string.password),
                        8
                    )
                )
            } else {
//                doResgist()
            }
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
            ObjectAnimator.ofFloat(decoration, View.TRANSLATION_X, 100f, 0f).apply {
                duration = 2000
            }.start()

            val animators = listOf(
                AnimationUtil.createTranslationAnimator(ivAppLogo),
                AnimationUtil.createTranslationAnimator(tvTitle),
                AnimationUtil.createTranslationAnimator(tvDesc),
                AnimationUtil.createTranslationAnimator(edUsername),
                AnimationUtil.createTranslationAnimator(edEmail),
                AnimationUtil.createTranslationAnimator(edPassword),
                AnimationUtil.createTranslationAnimator(btnRegister),
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