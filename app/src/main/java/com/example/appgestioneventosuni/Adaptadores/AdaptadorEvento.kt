package com.example.appgestioneventosuni.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appgestioneventosuni.Constantes
import com.example.appgestioneventosuni.Modelos.ModeloEvento
import com.example.appgestioneventosuni.R
import com.example.appgestioneventosuni.databinding.ItemEventoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorEvento(
    private val context: Context,
    private val listaEventos: ArrayList<ModeloEvento>
) : RecyclerView.Adapter<AdaptadorEvento.HolderEvento>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderEvento {
        val binding = ItemEventoBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderEvento(binding)
    }

    override fun onBindViewHolder(holder: HolderEvento, position: Int) {
        val modelo = listaEventos[position]

        holder.binding.TvNombreEvento.text = modelo.nombreEvento
        holder.binding.TvLugarEvento.text = "Lugar: ${modelo.lugar}"
        holder.binding.TvFechaHoraEvento.text = "Fecha y hora: ${modelo.fechaHora}"
        holder.binding.TvDescripcionEvento.text = modelo.descripcion

        try {
            Glide.with(context)
                .load(modelo.imagenUrl)
                .placeholder(R.drawable.ic_eventos)
                .error(R.drawable.ic_eventos)
                .centerCrop()
                .into(holder.binding.IvImagenEvento)
        } catch (e: Exception) {
            holder.binding.IvImagenEvento.setImageResource(R.drawable.ic_eventos)
        }

        holder.binding.BtnInteresEvento.setOnClickListener {
            registrarInteresEvento(modelo)
        }
    }

    private fun registrarInteresEvento(evento: ModeloEvento) {
        val usuario = firebaseAuth.currentUser
        if (usuario == null) {
            Toast.makeText(context, "Debe iniciar sesión para registrarse", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = usuario.uid
        val email = usuario.email ?: ""
        val tiempo = Constantes.obtenerTiempoDis()
        val fechaRegistro = Constantes.obtenerFecha(tiempo)
        val idRegistro = "${evento.id}_$uid"

        FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombreBD = snapshot.child("nombres").value?.toString()
                    val nombreFinal = if (!nombreBD.isNullOrEmpty()) {
                        nombreBD
                    } else if (!usuario.displayName.isNullOrEmpty()) {
                        usuario.displayName!!
                    } else {
                        email
                    }

                    val hashMap = HashMap<String, Any>()
                    hashMap["idRegistro"] = idRegistro
                    hashMap["uidUsuario"] = uid
                    hashMap["nombreUsuario"] = nombreFinal
                    hashMap["emailUsuario"] = email
                    hashMap["idElemento"] = evento.id
                    hashMap["nombreElemento"] = evento.nombreEvento
                    hashMap["tipoElemento"] = "Evento"
                    hashMap["fechaRegistro"] = fechaRegistro
                    hashMap["tiempo"] = tiempo

                    FirebaseDatabase.getInstance().getReference("Interesados")
                        .child(idRegistro)
                        .setValue(hashMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Te has registrado como interesado en: ${evento.nombreEvento}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "No se pudo guardar el registro: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int = listaEventos.size

    inner class HolderEvento(val binding: ItemEventoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
