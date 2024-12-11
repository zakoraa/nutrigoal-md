package com.c242pS371.nutrigoal.ui.auth

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.c242pS371.nutrigoal.R
import kotlin.math.sqrt

class AuthDecorationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.primary)
    }

    private val sidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.primary_80)
    }

    private val centerLineLength = 450f
    private val sideLineLength = centerLineLength - 100f
    private val lineThickness = 160f
    private val cornerRadius = 100f
    private val offset = 50f
    private val angle = 45f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (centerLineLength * sqrt(0.7)).toInt()
        val desiredWidth = lineThickness.toInt() + offset.toInt() * 4

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()

        canvas.save()
        canvas.translate(width, 0f)
        canvas.rotate(angle)

        canvas.drawRoundRect(
            -lineThickness / 2 - offset * 2,
            0f,
            lineThickness / 2 - offset,
            sideLineLength - 40f,
            cornerRadius,
            cornerRadius,
            sidePaint
        )


        canvas.drawRoundRect(
            lineThickness / 2 - offset,
            0f,
            lineThickness / 2 + offset * 2,
            sideLineLength,
            cornerRadius,
            cornerRadius,
            sidePaint
        )

        canvas.drawRoundRect(
            -lineThickness / 2,
            0f,
            lineThickness / 2,
            centerLineLength,
            cornerRadius,
            cornerRadius,
            centerPaint
        )

        canvas.restore()
    }
}
