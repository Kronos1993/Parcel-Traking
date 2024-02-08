package com.kronos.core.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun vibrate(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }
}