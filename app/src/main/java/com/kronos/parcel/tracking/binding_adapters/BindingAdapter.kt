package com.kronos.parcel.traking.binding_adapters

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.kronos.myparceltraking.R
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
    if (days < 0) {
        textView.text =
            textView.context.resources.getString(R.string.time_spent_hours).format(hours)
    } else {
        textView.text = textView.context.resources.getString(R.string.time_spent_days).format(days)
    }
}


@BindingAdapter("handle_visibility")
fun handleVisibility(view: MaterialButton, enable: Boolean) = view.run {
    isVisible = enable
}

@BindingAdapter("handle_layout_visibility")
fun handleLayoutVisibility(view: ConstraintLayout, enable: Boolean) = view.run {
    isVisible = enable
}