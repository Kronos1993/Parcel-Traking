package com.kronos.parcel.tracking.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.core.util.copyText
import com.kronos.data.remote.retrofit.UrlProvider
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.databinding.ItemParcelBinding

class ParcelAdapter : ListAdapter<ParcelModel, ParcelAdapter.ParcelViewHolder>(GeneralDiffCallback<ParcelModel>()) {

    private var adapterItemClickListener:AdapterItemClickListener<ParcelModel>?=null
    private lateinit var urlProvider: UrlProvider

    fun setAdapterItemClick(adapterItemClickListener:AdapterItemClickListener<ParcelModel>?){
        this.adapterItemClickListener = adapterItemClickListener
    }

    fun setUrlProvider(urlProvider: UrlProvider){
        this.urlProvider = urlProvider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelViewHolder {
        val binding = ItemParcelBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ParcelViewHolder(binding,adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: ParcelViewHolder, position: Int) {
        val currentParcel = getItem(position)
        holder.bind(currentParcel)
    }

    fun getItemAt(adapterPosition: Int): ParcelModel = getItem(adapterPosition)

    class ParcelViewHolder(var binding:ItemParcelBinding, private var clickListener:AdapterItemClickListener<ParcelModel>?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parcel: ParcelModel){
            binding.run {
                parcelModel = parcel
                root.setOnClickListener {
                    clickListener?.onItemClick(parcel,adapterPosition)
                }
                root.setOnLongClickListener {
                    copyText(binding.textViewParcelTrackingNumber.context,binding.textViewParcelTrackingNumber.text.toString())
                    true
                }
            }
        }
    }
}

