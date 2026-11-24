package com.example.appgestioneventosuni.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.Adaptadores.AdaptadorInteresado
import com.example.appgestioneventosuni.Modelos.ModeloInteresado
import com.example.appgestioneventosuni.databinding.FragmentRegistroInteresadosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentRegistroInteresados : Fragment() {

    private var _binding: FragmentRegistroInteresadosBinding? = null
    private val binding get() = _binding!!

    private lateinit var listaInteresados: ArrayList<ModeloInteresado>
    private lateinit var adaptadorInteresado: AdaptadorInteresado

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroInteresadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaInteresados = ArrayList()
        adaptadorInteresado = AdaptadorInteresado(requireContext(), listaInteresados)
        binding.RvInteresados.adapter = adaptadorInteresado

        cargarInteresados()
    }

    private fun cargarInteresados() {
        val ref = FirebaseDatabase.getInstance().getReference("Interesados")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                listaInteresados.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloInteresado::class.java)
                    if (modelo != null) {
                        listaInteresados.add(modelo)
                    }
                }
                listaInteresados.sortedByDescending { it.tiempo }.also { ordenados ->
                    listaInteresados.clear()
                    listaInteresados.addAll(ordenados)
                }
                adaptadorInteresado.notifyDataSetChanged()

                if (listaInteresados.isEmpty()) {
                    binding.CardVacioInteresados.visibility = View.VISIBLE
                    binding.RvInteresados.visibility = View.GONE
                } else {
                    binding.CardVacioInteresados.visibility = View.GONE
                    binding.RvInteresados.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
