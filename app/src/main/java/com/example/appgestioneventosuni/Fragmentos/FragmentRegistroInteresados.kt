package com.example.appgestioneventosuni.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.Adaptadores.AdaptadorInteresado
import com.example.appgestioneventosuni.Modelos.ModeloInteresado
import com.example.appgestioneventosuni.R
import com.example.appgestioneventosuni.databinding.FragmentRegistroInteresadosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentRegistroInteresados : Fragment() {

    private var _binding: FragmentRegistroInteresadosBinding? = null
    private val binding get() = _binding!!

    private lateinit var listaCompleta: ArrayList<ModeloInteresado>
    private lateinit var listaInteresados: ArrayList<ModeloInteresado>
    private lateinit var adaptadorInteresado: AdaptadorInteresado
    private lateinit var firebaseAuth: FirebaseAuth
    private var interesadosListener: ValueEventListener? = null
    private var filtroActualId: Int = R.id.ChipTodos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroInteresadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        listaCompleta = ArrayList()
        listaInteresados = ArrayList()
        adaptadorInteresado = AdaptadorInteresado(requireContext(), listaInteresados)
        binding.RvInteresados.adapter = adaptadorInteresado

        binding.ChipGroupFiltros.setOnCheckedStateChangeListener { _, checkedIds ->
            filtroActualId = checkedIds.firstOrNull() ?: R.id.ChipTodos
            aplicarFiltro()
        }

        cargarInteresados()
    }

    private fun cargarInteresados() {
        val ref = FirebaseDatabase.getInstance().getReference("Interesados")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                listaCompleta.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloInteresado::class.java)
                    if (modelo != null) {
                        listaCompleta.add(modelo)
                    }
                }
                listaCompleta.sortByDescending { it.tiempo }
                aplicarFiltro()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        interesadosListener = listener
        ref.addValueEventListener(listener)
    }

    private fun aplicarFiltro() {
        if (_binding == null) return

        val uidActual = firebaseAuth.currentUser?.uid
        val filtrados = when (filtroActualId) {
            R.id.ChipMisRegistros -> listaCompleta.filter {
                uidActual != null && it.uidUsuario == uidActual
            }
            R.id.ChipEventos -> listaCompleta.filter {
                it.tipoElemento.isEmpty() || it.tipoElemento.equals("Evento", ignoreCase = true)
            }
            R.id.ChipProyectos -> listaCompleta.filter {
                it.tipoElemento.equals("Proyecto", ignoreCase = true)
            }
            else -> listaCompleta
        }

        listaInteresados.clear()
        listaInteresados.addAll(filtrados)
        adaptadorInteresado.notifyDataSetChanged()

        if (listaInteresados.isEmpty()) {
            binding.TvEstadoInteresados.text = when (filtroActualId) {
                R.id.ChipMisRegistros -> "Aún no tienes registros de interés"
                R.id.ChipEventos -> "No hay personas interesadas en eventos todavía"
                R.id.ChipProyectos -> "No hay personas interesadas en proyectos todavía"
                else -> "No se registran personas interesadas aún"
            }
            binding.CardVacioInteresados.visibility = View.VISIBLE
            binding.RvInteresados.visibility = View.GONE
        } else {
            binding.CardVacioInteresados.visibility = View.GONE
            binding.RvInteresados.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interesadosListener?.let {
            FirebaseDatabase.getInstance().getReference("Interesados").removeEventListener(it)
        }
        interesadosListener = null
        _binding = null
    }
}
