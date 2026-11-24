package com.example.appgestioneventosuni.Fragmentos

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.Constantes
import com.example.appgestioneventosuni.databinding.FragmentPublicarProyectoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale

class FragmentPublicarProyecto : Fragment() {

    private var _binding: FragmentPublicarProyectoBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicarProyectoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        // Fecha actual por defecto
        val fechaHoy = Constantes.obtenerFecha(Constantes.obtenerTiempoDis())
        binding.EtFechaProyecto.setText(fechaHoy)

        // Autocompletar nombre del estudiante logueado como responsable inicial
        cargarNombreResponsable()

        binding.EtFechaProyecto.setOnClickListener {
            mostrarSelectorFecha()
        }

        binding.BtnPublicarProyecto.setOnClickListener {
            validarYPublicar()
        }
    }

    private fun cargarNombreResponsable() {
        val usuario = firebaseAuth.currentUser ?: return
        if (!usuario.displayName.isNullOrEmpty()) {
            binding.EtResponsablesProyecto.setText(usuario.displayName)
        } else {
            FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(usuario.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nombreBD = snapshot.child("nombres").value?.toString()
                        if (!nombreBD.isNullOrEmpty() && _binding != null &&
                            binding.EtResponsablesProyecto.text.isNullOrEmpty()
                        ) {
                            binding.EtResponsablesProyecto.setText(nombreBD)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun mostrarSelectorFecha() {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val fechaFormateada = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                )
                binding.EtFechaProyecto.setText(fechaFormateada)
            },
            anio,
            mes,
            dia
        ).show()
    }

    private fun validarYPublicar() {
        val nombreProyecto = binding.EtTituloProyecto.text.toString().trim()
        val responsables = binding.EtResponsablesProyecto.text.toString().trim()
        val fechaPublicacion = binding.EtFechaProyecto.text.toString().trim()
        val categoria = binding.EtCategoriaProyecto.text.toString().trim()
        val descripcion = binding.EtDescripcionProyecto.text.toString().trim()

        if (nombreProyecto.isEmpty()) {
            binding.EtTituloProyecto.error = "Ingrese el nombre del proyecto"
            binding.EtTituloProyecto.requestFocus()
        } else if (responsables.isEmpty()) {
            binding.EtResponsablesProyecto.error = "Ingrese el nombre del responsable o responsables"
            binding.EtResponsablesProyecto.requestFocus()
        } else if (fechaPublicacion.isEmpty()) {
            binding.EtFechaProyecto.error = "Seleccione la fecha de publicación"
            binding.EtFechaProyecto.requestFocus()
        } else if (descripcion.isEmpty()) {
            binding.EtDescripcionProyecto.error = "Ingrese la descripción del proyecto"
            binding.EtDescripcionProyecto.requestFocus()
        } else {
            guardarProyectoEnFirebase(
                nombreProyecto = nombreProyecto,
                responsables = responsables,
                fechaPublicacion = fechaPublicacion,
                categoria = if (categoria.isEmpty()) "General" else categoria,
                descripcion = descripcion
            )
        }
    }

    private fun guardarProyectoEnFirebase(
        nombreProyecto: String,
        responsables: String,
        fechaPublicacion: String,
        categoria: String,
        descripcion: String
    ) {
        progressDialog.setMessage("Publicando proyecto...")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoDis()
        val ref = FirebaseDatabase.getInstance().getReference("Proyectos")
        val idProyecto = ref.push().key ?: "$tiempo"
        val uidAutor = firebaseAuth.uid ?: ""

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = idProyecto
        hashMap["nombreProyecto"] = nombreProyecto
        hashMap["responsables"] = responsables
        hashMap["fechaPublicacion"] = fechaPublicacion
        hashMap["categoria"] = categoria
        hashMap["descripcion"] = descripcion
        hashMap["uidAutor"] = uidAutor
        hashMap["tiempo"] = tiempo

        ref.child(idProyecto)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                if (_binding != null) {
                    Toast.makeText(
                        requireContext(),
                        "Proyecto publicado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.EtTituloProyecto.text?.clear()
                    binding.EtCategoriaProyecto.text?.clear()
                    binding.EtDescripcionProyecto.text?.clear()
                    binding.EtFechaProyecto.setText(Constantes.obtenerFecha(Constantes.obtenerTiempoDis()))
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                if (_binding != null) {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo publicar debido a: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
