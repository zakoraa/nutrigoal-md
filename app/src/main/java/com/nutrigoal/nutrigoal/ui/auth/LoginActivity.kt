package com.nutrigoal.nutrigoal.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.ActivityLoginBinding


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

            if (email.isEmpty()) {
                showToast(getString(R.string.error_empty_field, getString(R.string.email)))
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(getString(R.string.error_wrong_email_format))
            } else if (password.isEmpty()) {
                showToast(getString(R.string.error_empty_field, getString(R.string.password)))
            } else if (password.length < 8) {
                showToast(
                    getString(
                        R.string.error_min_length_field, getString(R.string.password), 8
                    )
                )
            } else {

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

            val appLogo =
                ObjectAnimator.ofFloat(ivAppLogo, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val title =
                ObjectAnimator.ofFloat(tvTitle, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val desc =
                ObjectAnimator.ofFloat(tvDesc, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val edtEmail =
                ObjectAnimator.ofFloat(edEmail, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val edtPass =
                ObjectAnimator.ofFloat(edPassword, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val btnLogin =
                ObjectAnimator.ofFloat(btnLogin, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val llBellowBtn =
                ObjectAnimator.ofFloat(llBellowBtn, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val dividerLeft =
                ObjectAnimator.ofFloat(dividerLeft, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val dividerRight =
                ObjectAnimator.ofFloat(dividerRight, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val tvOr = ObjectAnimator.ofFloat(tvOr, View.TRANSLATION_Y, 100f, 0f).setDuration(1000)
            val btnLoginWithGoogle =
                ObjectAnimator.ofFloat(btnLoginWithGoogle, View.TRANSLATION_Y, 100f, 0f)
                    .setDuration(1000)
            val btnLoginWithFacebook =
                ObjectAnimator.ofFloat(btnLoginWithFacebook, View.TRANSLATION_Y, 100f, 0f)
                    .setDuration(1000)

            val together = AnimatorSet().apply {
                playTogether(
                    appLogo,
                    title,
                    desc,
                    edtEmail,
                    edtPass,
                    btnLogin,
                    llBellowBtn,
                    dividerRight,
                    dividerLeft,
                    tvOr,
                    btnLoginWithGoogle,
                    btnLoginWithFacebook
                )
            }

            AnimatorSet().apply {
                playSequentially(together)
                start()
            }
        }

    }

}