package com.example.appgestioneventosuni.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appgestioneventosuni.Constantes
import com.example.appgestioneventosuni.Modelos.ModeloProyecto
import com.example.appgestioneventosuni.databinding.ItemProyectoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProyecto(
    private val context: Context,
    private val listaProyectos: ArrayList<ModeloProyecto>
) : RecyclerView.Adapter<AdaptadorProyecto.HolderProyecto>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProyecto {
        val binding = ItemProyectoBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderProyecto(binding)
    }

    override fun onBindViewHolder(holder: HolderProyecto, position: Int) {
        val modelo = listaProyectos[position]

        holder.binding.TvNombreProyecto.text = modelo.nombreProyecto
        holder.binding.TvDescripcionProyecto.text = modelo.descripcion
        holder.binding.TvResponsablesProyecto.text = "Responsable(s): ${modelo.responsables}"
        holder.binding.TvFechaProyecto.text = "Fecha de publicación: ${modelo.fechaPublicacion}"

        if (modelo.categoria.isNotEmpty()) {
            holder.binding.TvCategoriaProyecto.visibility = View.VISIBLE
            holder.binding.TvCategoriaProyecto.text = modelo.categoria
        } else {
            holder.binding.TvCategoriaProyecto.visibility = View.GONE
        }

        holder.binding.BtnInteresProyecto.setOnClickListener {
            registrarInteresProyecto(modelo)
        }
    }

    private fun registrarInteresProyecto(proyecto: ModeloProyecto) {
        val usuario = firebaseAuth.currentUser
        if (usuario == null) {
            Toast.makeText(context, "Debe iniciar sesión para registrarse", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = usuario.uid
        val email = usuario.email ?: ""
        val tiempo = Constantes.obtenerTiempoDis()
        val fechaRegistro = Constantes.obtenerFecha(tiempo)
        val idRegistro = "${proyecto.id}_$uid"

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
                    hashMap["idElemento"] = proyecto.id
                    hashMap["nombreElemento"] = proyecto.nombreProyecto
                    hashMap["tipoElemento"] = "Proyecto"
                    hashMap["fechaRegistro"] = fechaRegistro
                    hashMap["tiempo"] = tiempo

                    FirebaseDatabase.getInstance().getReference("Interesados")
                        .child(idRegistro)
                        .setValue(hashMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Interés registrado en: ${proyecto.nombreProyecto}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Error al registrar interés: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int = listaProyectos.size

    inner class HolderProyecto(val binding: ItemProyectoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
