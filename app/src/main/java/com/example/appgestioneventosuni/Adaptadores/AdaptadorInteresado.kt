package com.example.appgestioneventosuni.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.appgestioneventosuni.Modelos.ModeloInteresado
import com.example.appgestioneventosuni.R
import com.example.appgestioneventosuni.databinding.ItemInteresadoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdaptadorInteresado(
    private val context: Context,
    private val listaInteresados: ArrayList<ModeloInteresado>
) : RecyclerView.Adapter<AdaptadorInteresado.HolderInteresado>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

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

        val uidActual = firebaseAuth.currentUser?.uid
        if (uidActual != null && modelo.uidUsuario == uidActual && modelo.idRegistro.isNotEmpty()) {
            holder.binding.BtnCancelarInteres.visibility = View.VISIBLE
            holder.binding.BtnCancelarInteres.setOnClickListener {
                confirmarCancelacion(modelo)
            }
        } else {
            holder.binding.BtnCancelarInteres.visibility = View.GONE
            holder.binding.BtnCancelarInteres.setOnClickListener(null)
        }
    }

    private fun confirmarCancelacion(modelo: ModeloInteresado) {
        AlertDialog.Builder(context)
            .setTitle("Cancelar registro")
            .setMessage("¿Deseas cancelar tu interés en \"${modelo.nombreElemento}\"?")
            .setPositiveButton("Sí, cancelar") { _, _ ->
                FirebaseDatabase.getInstance().getReference("Interesados")
                    .child(modelo.idRegistro)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Registro cancelado: ${modelo.nombreElemento}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "No se pudo cancelar: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun getItemCount(): Int = listaInteresados.size

    inner class HolderInteresado(val binding: ItemInteresadoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
