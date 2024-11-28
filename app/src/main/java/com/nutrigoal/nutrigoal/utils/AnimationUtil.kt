package com.nutrigoal.nutrigoal.utils

import android.animation.ObjectAnimator
import android.view.View

object AnimationUtil {
    fun createTranslationAnimator(view: View, duration: Long = 1000L): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 100f, 0f).setDuration(duration)
    }
}