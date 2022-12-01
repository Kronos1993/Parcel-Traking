package com.kronos.parcel.tracking.binding_adapters

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.kronos.parcel.tracking.R
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("format_date_long")
fun formatDate(textView: TextView, date: Long) = textView.run {
    val d = Date(date)
    val sdf = SimpleDateFormat(textView.context.getString(R.string.date_format))
    try {
        textView.text = sdf.format(d)
    } catch (e: Exception) {
        textView.text = ""
    }
}

@BindingAdapter("time_transit_spent")
fun timeTransitSpent(textView: TextView, date: Long) = textView.run {
    val current = Date().time
    val spent = current - date
    val seconds: Long = spent / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24 + 1
    when {
        seconds < 60 -> {
            textView.text =
                textView.context.resources.getString(R.string.time_spent_seconds)
        }
        minutes < 60 -> {
            textView.text =
                textView.context.resources.getString(R.string.time_spent_minutes).format(minutes)
        }
        hours < 24 -> {
            textView.text =
                textView.context.resources.getString(R.string.time_spent_hours).format(hours)
        }
        else -> {
            textView.text =
                textView.context.resources.getString(R.string.time_spent_days).format(days)
        }
    }
}


@BindingAdapter("handle_visibility")
fun handleVisibility(view: View, enable: Boolean) = view.run {
    isVisible = enable
}


@BindingAdapter("error_text")
fun setErrorText(view: TextInputLayout, errorMessageRes: Int) {
    view.run {
        if (errorMessageRes == 0) {
            view.error = null
            view.isErrorEnabled = false
            return@run
        }

        val errorMessage = context.getString(errorMessageRes)
        if (errorMessage.isEmpty()) {
            view.error = null
            view.isErrorEnabled = false
        } else {
            view.error = errorMessage
            view.isErrorEnabled = true

            if (view.endIconDrawable != null) {
                view.errorIconDrawable = null
            }
        }
    }
}

@BindingAdapter("error_text")
fun setErrorText(input: TextInputLayout, value: String?) =
    input.run {
        this.isErrorEnabled = value.orEmpty().isNotEmpty()
        this.error = value

        if (this.endIconDrawable != null) {
            this.errorIconDrawable = null
        }
    }

@BindingAdapter("error_text")
fun setErrorText(view: TextView, errorMessageRes: Int) {
    view.run {
        if (errorMessageRes == 0) {
            view.text = null
            view.visibility = View.GONE
            return@run
        }

        val errorMessage = context.getString(errorMessageRes)
        if (errorMessage.isEmpty()) {
            view.text = null
            view.visibility = View.GONE
        } else {
            view.text = errorMessage
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("error_text")
fun setErrorText(input: TextView, value: String?) =
    input.run {
        this.text = value
        this.visibility = View.VISIBLE
    }