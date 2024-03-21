package com.kronos.core.adapters.diff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
class GeneralDiffCallback<T : Any>: DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return newItem.hashCode() == oldItem.hashCode()
    }
}
