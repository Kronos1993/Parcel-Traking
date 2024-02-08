package com.kronos.parcel.tracking.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.diff.GeneralDiffCallback
import com.kronos.core.util.copyText
import com.kronos.data.remote.retrofit.UrlProvider
import com.kronos.parcel.tracking.databinding.ItemParcelBinding
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.R

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
        holder.binding.textViewParcelTrackingNumber.setOnLongClickListener {
            copyText(holder.binding.textViewParcelTrackingNumber.context,holder.binding.textViewParcelTrackingNumber.text.toString())
            true
        }
        if(currentParcel.imageUrl != null){
            if (currentParcel.imageUrl == "not found")
                Glide.with(holder.itemView).load(R.drawable.ic_not_found).into(holder.binding.imageStatus)
            else
                Glide.with(holder.itemView).load(
                    when (currentParcel.status) {
                        "Carga recibida en Miami" -> R.mipmap.carga_recibida
                        "Carga entregada a agente Aereo" -> R.mipmap.entregada_agente_aereo
                        "Carga en Proceso de Aduanas" -> R.mipmap.proceso_aduanas
                        "Carga en Bodega Central" -> R.mipmap.carga_transito_camion
                        "Carga disponible para entrega en sucursal" -> R.mipmap.disponible_entrega
                        "not found" -> R.drawable.ic_not_found
                        else -> R.mipmap.entregado
                    }
                ).placeholder(R.drawable.ic_not_found).into(holder.binding.imageStatus)

        }
        else
            Glide.with(holder.itemView).load(R.drawable.ic_not_found).into(holder.binding.imageStatus)
    }

    fun getItemAt(adapterPosition: Int): ParcelModel = getItem(adapterPosition)

    class ParcelViewHolder(var binding:ItemParcelBinding, var clickListener:AdapterItemClickListener<ParcelModel>?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parcel: ParcelModel){
            binding.run {
                parcelModel = parcel
                root.setOnClickListener {
                    clickListener?.onItemClick(parcel,adapterPosition)
                }
            }
        }
    }
}

