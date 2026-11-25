package com.example.appgestioneventosuni.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.Adaptadores.AdaptadorProyecto
import com.example.appgestioneventosuni.Modelos.ModeloProyecto
import com.example.appgestioneventosuni.databinding.FragmentConsultarProyectosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentConsultarProyectos : Fragment() {

    private var _binding: FragmentConsultarProyectosBinding? = null
    private val binding get() = _binding!!

    private lateinit var listaProyectos: ArrayList<ModeloProyecto>
    private lateinit var adaptadorProyecto: AdaptadorProyecto
    private lateinit var firebaseAuth: FirebaseAuth
    private var interesesListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultarProyectosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        listaProyectos = ArrayList()
        adaptadorProyecto = AdaptadorProyecto(requireContext(), listaProyectos)
        binding.RvProyectos.adapter = adaptadorProyecto

        cargarProyectos()
        escucharInteresesUsuario()
    }

    private fun escucharInteresesUsuario() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Interesados")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val ids = HashSet<String>()
                for (ds in snapshot.children) {
                    val uidRegistro = ds.child("uidUsuario").value?.toString()
                    val tipo = ds.child("tipoElemento").value?.toString()
                    val idElemento = ds.child("idElemento").value?.toString()
                    if (uidRegistro == uid &&
                        tipo.equals("Proyecto", ignoreCase = true) &&
                        !idElemento.isNullOrEmpty()
                    ) {
                        ids.add(idElemento)
                    }
                }
                adaptadorProyecto.actualizarIntereses(ids)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        interesesListener = listener
        ref.addValueEventListener(listener)
    }

    private fun cargarProyectos() {
        val ref = FirebaseDatabase.getInstance().getReference("Proyectos")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                listaProyectos.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloProyecto::class.java)
                    if (modelo != null) {
                        listaProyectos.add(modelo)
                    }
                }
                listaProyectos.sortByDescending { it.tiempo }
                adaptadorProyecto.notifyDataSetChanged()

                if (listaProyectos.isEmpty()) {
                    binding.CardVacioProyectos.visibility = View.VISIBLE
                    binding.RvProyectos.visibility = View.GONE
                } else {
                    binding.CardVacioProyectos.visibility = View.GONE
                    binding.RvProyectos.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interesesListener?.let {
            FirebaseDatabase.getInstance().getReference("Interesados").removeEventListener(it)
        }
        interesesListener = null
        _binding = null
    }
}
