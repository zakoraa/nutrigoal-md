package com.nutrigoal.nutrigoal.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseUser
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.databinding.ActivityLoginBinding
import com.nutrigoal.nutrigoal.ui.MainActivity
import com.nutrigoal.nutrigoal.utils.AnimationUtil
import com.nutrigoal.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initAction()
        playAnimation()
    }

    private fun initView() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        lifecycleScope.launch {
            viewModel.credentialState.collect { result ->
                handleCredentialState(result)
            }
        }

        lifecycleScope.launch {
            viewModel.loginWithGoogleState.collect { result ->
                handleLoginWithGoogleState(result)
            }
        }
    }

    private fun handleCredentialState(result: ResultState<GetCredentialResponse>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                showLoading(false)
                handleLogin(result.data)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, result.error)
            }

            is ResultState.Initial -> {}
        }
    }

    private fun handleLoginWithGoogleState(result: ResultState<FirebaseUser?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.login_success))
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, result.error)
            }

            is ResultState.Initial -> {}

        }
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

            btnLoginWithGoogle.setOnClickListener {
                viewModel.getCredentialResponse(this@LoginActivity)
            }

        }
    }

    private fun handleLogin(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        ToastUtil.showToast(
                            this@LoginActivity,
                            getString(R.string.error_login)
                        )
                    }
                } else {
                    ToastUtil.showToast(
                        this@LoginActivity,
                        getString(R.string.error_login)
                    )
                }
            }

            else -> {
                ToastUtil.showToast(
                    this@LoginActivity,
                    getString(R.string.error_login)
                )
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
            if (isLoading) {
                btnLogin.visibility = View.INVISIBLE
                btnLoginWithGoogle.isClickable = false
                btnLoginWithFacebook.isClickable = false
                shimmerBtnLogin.visibility = View.VISIBLE
                shimmerBtnLogin.startShimmer()
            } else {
                btnLogin.visibility = View.VISIBLE
                btnLoginWithGoogle.isClickable = true
                btnLoginWithFacebook.isClickable = true
                shimmerBtnLogin.visibility = View.INVISIBLE
                shimmerBtnLogin.stopShimmer()
            }
        }
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