package com.example.appgestioneventosuni.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appgestioneventosuni.Modelos.ModeloInteresado
import com.example.appgestioneventosuni.R
import com.example.appgestioneventosuni.databinding.ItemInteresadoBinding

class AdaptadorInteresado(
    private val context: Context,
    private val listaInteresados: ArrayList<ModeloInteresado>
) : RecyclerView.Adapter<AdaptadorInteresado.HolderInteresado>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderInteresado {
        val binding = ItemInteresadoBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderInteresado(binding)
    }

    override fun onBindViewHolder(holder: HolderInteresado, position: Int) {
        val modelo = listaInteresados[position]

        val tipo = if (modelo.tipoElemento.isNotEmpty()) modelo.tipoElemento else "Evento"
        holder.binding.TvTipoElementoInteresado.text = tipo

        if (tipo.equals("Proyecto", ignoreCase = true)) {
            holder.binding.TvTipoElementoInteresado.setBackgroundResource(R.color.unison_blue_light)
        } else {
            holder.binding.TvTipoElementoInteresado.setBackgroundResource(R.color.unison_gold_light)
        }

        holder.binding.TvNombreElementoInteresado.text = modelo.nombreElemento
        holder.binding.TvUsuarioInteresado.text = "Usuario interesado: ${modelo.nombreUsuario}"
        holder.binding.TvEmailInteresado.text = "Correo: ${modelo.emailUsuario}"
        holder.binding.TvFechaInteresado.text = "Fecha del registro: ${modelo.fechaRegistro}"
    }

    override fun getItemCount(): Int = listaInteresados.size

    inner class HolderInteresado(val binding: ItemInteresadoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
