package com.kronos.parcel.tracking.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.parcel.tracking.databinding.ItemParcelBinding
import com.kronos.data.remote.retrofit.UrlConstants
import com.kronos.domain.model.parcel.ParcelModel

class ParcelAdapter : ListAdapter<ParcelModel, ParcelAdapter.ParcelViewHolder>(GeneralDiffCallback<ParcelModel>()) {

    private lateinit var adapterItemClickListener:AdapterItemClickListener<ParcelModel>

    fun setAdapterItemClick(adapterItemClickListener:AdapterItemClickListener<ParcelModel>){
        this.adapterItemClickListener = adapterItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelViewHolder {
        val binding = ItemParcelBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ParcelViewHolder(binding,adapterItemClickListener)
    }

    override fun onBindViewHolder(holder: ParcelViewHolder, position: Int) {
        val currentParcel = getItem(position)
        holder.bind(currentParcel)
        Glide.with(holder.itemView).load(UrlConstants.IMAGE_URL +  currentParcel.imageUrl).into(holder.binding.imageStatus)
    }

    fun getItemAt(adapterPosition: Int) = getItem(adapterPosition)

    class ParcelViewHolder(var binding:ItemParcelBinding, var clickListener:AdapterItemClickListener<ParcelModel>) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parcel: ParcelModel){
            binding.run {
                parcelModel = parcel
                root.setOnClickListener {
                    clickListener.onItemClick(parcel,adapterPosition)
                }
            }
        }
    }
}