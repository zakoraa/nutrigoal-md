package com.c242pS371.nutrigoal.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseUser
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.databinding.ActivityRegisterBinding
import com.c242pS371.nutrigoal.ui.MainActivity
import com.c242pS371.nutrigoal.utils.AnimationUtil
import com.c242pS371.nutrigoal.utils.InputValidator
import com.c242pS371.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private val inputValidator: InputValidator by lazy { InputValidator(this@RegisterActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView()
        setUpAction()
        playAnimation()
    }

    private fun setUpView() {
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

        lifecycleScope.launch {
            viewModel.registerWithEmailAndPasswordState.collect { result ->
                handleRegisterWithEmailAndPasswordState(result)
            }
        }
    }

    private fun setUpAction() {
        with(binding) {
            edUsername.setInputName(getString(R.string.username))
            edEmail.setInputName(getString(R.string.email))
            edPassword.setInputName(getString(R.string.password))

            tvBackToLogin.setOnClickListener {
                finish()
            }

            btnRegister.setOnClickListener {
                handleRegisterWithEmailAndPassword()
            }

            btnLoginWithGoogle.setOnClickListener {
                viewModel.getCredentialResponse(this@RegisterActivity)
            }
        }
    }

    private fun handleCredentialState(result: ResultState<GetCredentialResponse>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                showLoading(false)
                handleLoginWithGoogle(result.data)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_register))
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
                ToastUtil.showToast(this, getString(R.string.error_login))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleRegisterWithEmailAndPasswordState(result: ResultState<FirebaseUser?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.register_success))
                finish()
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_register))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleLoginWithGoogle(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        ToastUtil.showToast(
                            this@RegisterActivity,
                            getString(R.string.error_login)
                        )
                    }
                } else {
                    ToastUtil.showToast(
                        this@RegisterActivity,
                        getString(R.string.error_login)
                    )
                }
            }

            else -> {
                ToastUtil.showToast(
                    this@RegisterActivity,
                    getString(R.string.error_login)
                )
            }
        }
    }

    private fun handleRegisterWithEmailAndPassword() {
        with(binding) {
            val username = edUsername.text.toString().trim()
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString().trim()

            val usernameError = inputValidator.validateUsername(username)
            val emailError = inputValidator.validateEmail(email)
            val passwordError = inputValidator.validatePassword(password)

            inputValidator.checkValidation(tvErrorUsername, usernameError)
            inputValidator.checkValidation(tvErrorEmail, emailError)
            inputValidator.checkValidation(tvErrorPassword, passwordError)

            if (usernameError == null && emailError == null && passwordError == null) {
                viewModel.registerWithEmailAndPassword(username, email, password)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                btnRegister.visibility = View.INVISIBLE
                btnLoginWithGoogle.isClickable = false
                shimmerBtnRegister.visibility = View.VISIBLE
                shimmerBtnRegister.startShimmer()
            } else {
                btnRegister.visibility = View.VISIBLE
                btnLoginWithGoogle.isClickable = true
                shimmerBtnRegister.visibility = View.INVISIBLE
                shimmerBtnRegister.stopShimmer()
            }
        }
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