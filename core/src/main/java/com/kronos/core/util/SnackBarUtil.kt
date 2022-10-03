package com.kronos.core.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar


class SnackBarUtil {

    companion object {
        fun show(
            view: View,
            error: String,
            textColorRes: Int,
            backgroundColorRes: Int,
            duration: Int
        ): Snackbar? {
            val snackbar: Snackbar = Snackbar.make(view, error, duration)
            snackbar.setTextColor(ContextCompat.getColor(view.context, textColorRes))
            snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, backgroundColorRes))
            val snackTextView: TextView = snackbar.view.findViewById(R.id.snackbar_text)
            snackTextView.maxLines = 4
            snackbar.show()
            return snackbar
        }

        fun show(view: View, msg: String, textColorRes: Int, backgroundColorRes: Int): Snackbar? {
            val snackbar: Snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            snackbar.setTextColor(ContextCompat.getColor(view.context, textColorRes))
            snackbar.view.setBackgroundColor(
                ContextCompat.getColor(
                    view.getContext(),
                    backgroundColorRes
                )
            )
            val snackTextView: TextView = snackbar.view.findViewById(R.id.snackbar_text)
            snackTextView.maxLines = 4
            snackbar.show()
            return snackbar
        }

    }
}