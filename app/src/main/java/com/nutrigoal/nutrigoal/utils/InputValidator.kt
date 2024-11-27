package com.nutrigoal.nutrigoal.utils


object InputValidator {
//    fun List<Pair<TextFieldView, String>>.areAllInputsValid(): String? {
//        for ((inputField, fieldName) in this) {
//            if (inputField.text.isNullOrEmpty()) {
//                return fieldName
//            }
//        }
//        return null
//    }

    private fun isEmailValid(email: String): Boolean =
        email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordValid(password: String): Boolean =
        password.isNotEmpty() && password.length >= 8
}