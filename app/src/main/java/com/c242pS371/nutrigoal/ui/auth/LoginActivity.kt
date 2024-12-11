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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import com.c242pS371.nutrigoal.databinding.ActivityLoginBinding
import com.c242pS371.nutrigoal.ui.MainActivity
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.survey.Survey1Activity
import com.c242pS371.nutrigoal.utils.AnimationUtil
import com.c242pS371.nutrigoal.utils.InputValidator
import com.c242pS371.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val inputValidator: InputValidator by lazy { InputValidator(this@LoginActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
                handleLoginState(result)
            }
        }

        lifecycleScope.launch {
            viewModel.loginWithEmailAndPasswordState.collect { result ->
                handleLoginState(result)
            }
        }

        lifecycleScope.launch {
            viewModel.currentUserState.collect { result ->
                handleGetUser(result)
            }
        }

        lifecycleScope.launch {
            historyViewModel.historyResponseState.collect { result ->
                handleGetHistoryState(result)
            }
        }
    }

    private fun setUpAction() {
        with(binding) {

            tvDirectToRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnLogin.setOnClickListener {
                handleLoginWithEmailAndPassword()
            }

            btnLoginWithGoogle.setOnClickListener {
                viewModel.getCredentialResponse(this@LoginActivity)
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
                ToastUtil.showToast(this, getString(R.string.error_login))
            }

            is ResultState.Initial -> {}
        }
    }

    private fun handleLoginState(result: ResultState<FirebaseUser?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                if (result.data !== null) historyViewModel.getHistoryResult(result.data.uid)
                showLoading(false)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_login))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleGetHistoryState(result: ResultState<HistoryResponse?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                val data = result.data
                if (data == null) {
                    viewModel.getCurrentUser()
                    val userEntity = UserEntity(
                        username = Firebase.auth.currentUser?.displayName,
                        email = Firebase.auth.currentUser?.email,
                        photoProfile = Firebase.auth.currentUser?.photoUrl.toString()
                    )
                    val intent = Intent(this, Survey1Activity::class.java)
                    intent.putExtra(EXTRA_SURVEY, userEntity)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                ToastUtil.showToast(this, getString(R.string.login_success))
                showLoading(false)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_login))
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

    private fun handleLoginWithEmailAndPassword() {
        with(binding) {
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString().trim()

            val emailError = inputValidator.validateEmail(email)
            val passwordError = inputValidator.validatePassword(password)

            inputValidator.checkValidation(tvErrorEmail, emailError)
            inputValidator.checkValidation(tvErrorPassword, passwordError)

            if (emailError == null && passwordError == null) {
                viewModel.loginWithEmailAndPassword(email, password)
            }
        }
    }

    private fun handleGetUser(result: ResultState<UserEntity?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                viewModel.setCurrentUser(result.data)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                btnLogin.visibility = View.INVISIBLE
                btnLoginWithGoogle.isClickable = false
                shimmerBtnLogin.visibility = View.VISIBLE
                shimmerBtnLogin.startShimmer()
            } else {
                btnLogin.visibility = View.VISIBLE
                btnLoginWithGoogle.isClickable = true
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

    companion object {
        const val EXTRA_SURVEY = "extra_survey"
    }

}