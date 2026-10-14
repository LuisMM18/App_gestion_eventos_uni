package com.example.appgestioneventosuni.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.databinding.FragmentPublicarProyectoBinding

class FragmentPublicarProyecto : Fragment() {

    private var _binding: FragmentPublicarProyectoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicarProyectoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BtnPublicarProyecto.setOnClickListener {
            val titulo = binding.EtTituloProyecto.text.toString().trim()
            val categoria = binding.EtCategoriaProyecto.text.toString().trim()
            val descripcion = binding.EtDescripcionProyecto.text.toString().trim()

            if (titulo.isEmpty()) {
                binding.EtTituloProyecto.error = "Ingrese el título del proyecto"
                binding.EtTituloProyecto.requestFocus()
            } else if (categoria.isEmpty()) {
                binding.EtCategoriaProyecto.error = "Ingrese la categoría"
                binding.EtCategoriaProyecto.requestFocus()
            } else if (descripcion.isEmpty()) {
                binding.EtDescripcionProyecto.error = "Ingrese una descripción"
                binding.EtDescripcionProyecto.requestFocus()
            } else {
                Toast.makeText(requireContext(), "Proyecto registrado: $titulo", Toast.LENGTH_SHORT).show()
                binding.EtTituloProyecto.text?.clear()
                binding.EtCategoriaProyecto.text?.clear()
                binding.EtDescripcionProyecto.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
