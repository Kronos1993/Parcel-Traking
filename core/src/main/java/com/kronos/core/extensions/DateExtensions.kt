package com.kronos.core.extensions

import java.text.SimpleDateFormat
import java.util.Date

fun Date.formatDate(format: String): String {
    var dateFormatted = ""
    var sdf = SimpleDateFormat(format)
    try {
        dateFormatted = sdf.format(this)
    } catch (e: Exception) {
    }
    return dateFormatted
}