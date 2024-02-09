package com.kronos.parcel.tracking.binding_adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kronos.parcel.tracking.R

@BindingAdapter("app:handle_parcel_status")
fun handleParcelStatus(view: ImageView, parcelStatus: String?) = view.run {
    if (parcelStatus != null) {
        when (parcelStatus) {
            "Carga recibida en Miami" -> view.setBackgroundResource(R.mipmap.carga_recibida)
            "Carga entregada a agente Aereo" -> view.setBackgroundResource(R.mipmap.entregada_agente_aereo)
            "Carga en Proceso de Aduanas" -> view.setBackgroundResource(R.mipmap.proceso_aduanas)
            "Carga en Bodega Central" -> view.setBackgroundResource(R.mipmap.carga_transito_camion)
            "Carga disponible para entrega en sucursal" -> view.setBackgroundResource(R.mipmap.disponible_entrega)
            "not found" -> view.setBackgroundResource(R.drawable.ic_not_found)
            "" -> view.setBackgroundResource(R.drawable.ic_not_found)
            else -> view.setBackgroundResource(R.mipmap.entregado)
        }
    }else
        view.setBackgroundResource(R.drawable.ic_not_found)
}
