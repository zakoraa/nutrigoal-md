package com.c242pS371.nutrigoal.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AnimationUtil {
    fun createTranslationAnimator(
        view: View,
        duration: Long = 1000L,
        startAlpha: Float = 0f,
        endAlpha: Float = 1f,
        startY: Float = 100f,
        endY: Float = 0f
    ): AnimatorSet {
        val translationAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY)
        val alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, startAlpha, endAlpha)

        return AnimatorSet().apply {
            playTogether(translationAnimator, alphaAnimator)
            this.duration = duration
        }
    }
}