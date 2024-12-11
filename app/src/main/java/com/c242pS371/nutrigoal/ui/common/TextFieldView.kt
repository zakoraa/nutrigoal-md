package com.c242pS371.nutrigoal.ui.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.c242pS371.nutrigoal.R

class TextFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    private var inputName: String = ""
    private var emptyInputMessage: String = ""
    private var minCharInputMessage: String = ""
    private var isPasswordVisible: Boolean = false

    companion object {
        const val MIN_LENGTH = 8
    }

    init {
        setupView()
        onFocusListener()
        if (inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD == InputType.TYPE_NUMBER_VARIATION_PASSWORD
        ) {
            setupEndDrawableToggle()
        }
    }

    private fun setupView() {
        background = ContextCompat.getDrawable(context, R.drawable.edt_text_background)
        val horizontalPadding =
            context.resources.getDimensionPixelSize(R.dimen.edt_padding_horizontal)
        val drawablePadding = context.resources.getDimensionPixelSize(R.dimen.edt_icon_gap)

        setPadding(horizontalPadding, paddingTop, horizontalPadding, paddingBottom)
        compoundDrawablePadding = drawablePadding



        setHintTextColor(context.getColor(R.color.grey))
    }

    private fun onFocusListener() {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setDrawableColor(R.color.primary)
            } else {
                setDrawableColor(R.color.grey)
            }
        }
    }

    private fun setDrawableColor(colorResId: Int) {
        emptyInputMessage = context.getString(R.string.error_empty_field, inputName)
        minCharInputMessage =
            context.getString(R.string.error_min_length_field, inputName, MIN_LENGTH)
        val color = ContextCompat.getColor(context, colorResId)

        val startDrawable = compoundDrawables[0]
        val endDrawable = compoundDrawables[2]

        startDrawable?.mutate()?.setTint(color)
        endDrawable?.mutate()?.setTint(color)

        setCompoundDrawablesWithIntrinsicBounds(
            startDrawable,
            compoundDrawables[1],
            endDrawable,
            compoundDrawables[3]
        )
    }

    private fun setupEndDrawableToggle() {
        val visibilityOn = ContextCompat.getDrawable(context, R.drawable.ic_visible_pass)
        val visibilityOff = ContextCompat.getDrawable(context, R.drawable.ic_invisible_pass)

        setEndDrawable(visibilityOff)

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val endDrawable = compoundDrawables[2] ?: return@setOnTouchListener false
                if (event.rawX >= (right - endDrawable.bounds.width())) {
                    togglePasswordVisibility(visibilityOn, visibilityOff)
                    performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(
        visibilityOn: Drawable?,
        visibilityOff: Drawable?
    ) {
        isPasswordVisible = !isPasswordVisible
        transformationMethod =
            if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
        setEndDrawable(if (isPasswordVisible) visibilityOn else visibilityOff)
        setSelection(text?.length ?: 0)
    }

    private fun setEndDrawable(drawable: Drawable?) {
        setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.ic_pass),
            compoundDrawables[1],
            drawable,
            compoundDrawables[3]
        )
    }

    fun setInputName(name: String) {
        inputName = name
    }
}