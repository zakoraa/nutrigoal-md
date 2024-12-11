package com.c242pS371.nutrigoal.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.ui.common.TextFieldView

class InputValidator(private val context: Context) {

    fun validateInput(editText: TextFieldView, input: String): String? {
        return when {
            editText.text.toString().trim().isEmpty() -> emptyInputMessage(input)
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> emptyInputMessage(context.getString(R.string.email))

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                context.getString(R.string.error_wrong_email_format)

            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        val lengthLimit = 6
        return when {
            password.isEmpty() -> emptyInputMessage(context.getString(R.string.password))

            password.length < lengthLimit -> inputLengthLimitMessage(
                context.getString(R.string.password),
                lengthLimit
            )

            else -> null
        }
    }

    fun validateUsername(username: String): String? {
        val lengthLimit = 3
        return when {
            username.isEmpty() -> emptyInputMessage(context.getString(R.string.username))

            username.length < lengthLimit -> inputLengthLimitMessage(
                context.getString(R.string.username),
                lengthLimit
            )

            else -> null
        }
    }

    private fun emptyInputMessage(input: String): String {
        return String.format(context.getString(R.string.error_empty_field), input)
    }

    private fun showErrorInput(textView: TextView, errorMessage: String) {
        textView.text = errorMessage
        textView.visibility = View.VISIBLE
    }

    private fun hideErrorInput(textView: TextView) {
        textView.visibility = View.GONE
    }


    fun checkValidation(tvError: TextView, errorMessage: String?) {
        if (errorMessage != null) {
            showErrorInput(
                tvError,
                errorMessage
            )
        } else {
            hideErrorInput(tvError)
        }
    }

    private fun inputLengthLimitMessage(input: String, length: Int): String {
        return String.format(context.getString(R.string.error_min_length_field), input, length)
    }
}
