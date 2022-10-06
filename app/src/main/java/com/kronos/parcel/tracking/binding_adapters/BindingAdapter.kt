package com.kronos.parcel.tracking.binding_adapters

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.kronos.parcel.tracking.R
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("format_date_long")
fun formatDate(textView: TextView, date: Long) = textView.run {
    var d = Date(date)
    var sdf = SimpleDateFormat(textView.context.getString(R.string.date_format))
    try {
        textView.text = sdf.format(d)
    } catch (e: Exception) {
        textView.text = ""
    }
}

@BindingAdapter("time_transit_spent")
fun timeTransitSpent(textView: TextView, date: Long) = textView.run {
    var current = Date().time
    var spent = current - date
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
