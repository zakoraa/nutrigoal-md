package com.c242pS371.nutrigoal.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import com.c242pS371.nutrigoal.R

object AlertDialogUtil {
    fun showDialog(context: Context, message: String, onAccept: () -> Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setMessage(message)

        builder.setCancelable(false)

        builder.setPositiveButton(
            getString(
                context,
                R.string.logout
            )
        ) { _, _ ->
            onAccept()
        }

        builder.setNegativeButton(getString(context, R.string.back)) { dialog: DialogInterface, _ ->
            dialog.cancel()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        val positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.error))
        positiveButton.textSize = 14F

        val negativeButton: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.primary))
        negativeButton.textSize = 14F
    }
}