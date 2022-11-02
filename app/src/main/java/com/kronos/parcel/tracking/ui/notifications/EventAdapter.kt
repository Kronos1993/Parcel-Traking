package com.kronos.parcel.tracking.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.core.extensions.formatDate
import com.kronos.domain.model.event.EventModel
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.ItemEventBinding
import java.util.*

class EventAdapter() :
    ListAdapter<EventModel, EventAdapter.EventViewHolder>(GeneralDiffCallback<EventModel>()) {

    private var adapterItemClickListener: AdapterItemClickListener<EventModel>? = null

    fun setAdapterItemClick(adapterItemClickListener: AdapterItemClickListener<EventModel>?) {
        this.adapterItemClickListener = adapterItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentParcel = getItem(position)
        holder.bind(currentParcel)
        holder.binding.textViewEventAdded.visibility = View.VISIBLE
        if (position > 0) {
            if (Date(getItem(position - 1).dateAdded)
                    .formatDate(holder.itemView.context.getString(R.string.date_format))
                == Date(currentParcel.dateAdded)
                    .formatDate(holder.itemView.context.getString(R.string.date_format))
            ) {
                holder.binding.textViewEventAdded.visibility = View.GONE
            }
        }
    }

    fun getItemAt(adapterPosition: Int) = getItem(adapterPosition)

    class EventViewHolder(
        var binding: ItemEventBinding,
        var clickListener: AdapterItemClickListener<EventModel>?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventModel) {
            binding.run {
                eventModel = event
                root.setOnClickListener {
                    clickListener?.onItemClick(event, adapterPosition)
                }
            }
        }
    }
}