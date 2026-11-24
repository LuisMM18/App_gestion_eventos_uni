package com.example.appgestioneventosuni.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.OpcionesLogin
import com.example.appgestioneventosuni.R
import com.example.appgestioneventosuni.databinding.FragmentCerrarSesionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentCerrarSesion : Fragment() {

    private var _binding: FragmentCerrarSesionBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCerrarSesionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val usuarioActual = firebaseAuth.currentUser
        if (usuarioActual != null) {
            binding.TvEmailUsuario.text = usuarioActual.email ?: "Sin correo"
            val nombre = usuarioActual.displayName
            if (!nombre.isNullOrEmpty()) {
                binding.TvNombreUsuario.text = nombre
            } else {
                binding.TvNombreUsuario.text = "Estudiante Universitario"
                FirebaseDatabase.getInstance().getReference("Usuarios")
                    .child(usuarioActual.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nombreBD = snapshot.child("nombres").value?.toString()
                            if (!nombreBD.isNullOrEmpty() && _binding != null) {
                                binding.TvNombreUsuario.text = nombreBD
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        binding.BtnCerrarSesion.setOnClickListener {
            confirmarCerrarSesion()
        }
    }

    private fun confirmarCerrarSesion() {
        AlertDialog.Builder(mContext)
            .setTitle(R.string.Btn_cerrar_sesion)
            .setMessage(R.string.mensaje_cerrar_sesion_pregunta)
            .setPositiveButton(R.string.aceptar) { _, _ ->
                firebaseAuth.signOut()
                startActivity(Intent(mContext, OpcionesLogin::class.java))
                activity?.finishAffinity()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
