package com.example.appgestioneventosuni.Fragmentos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.appgestioneventosuni.Adaptadores.AdaptadorEvento
import com.example.appgestioneventosuni.Constantes
import com.example.appgestioneventosuni.Modelos.ModeloEvento
import com.example.appgestioneventosuni.databinding.DialogAgregarEventoBinding
import com.example.appgestioneventosuni.databinding.FragmentEventosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale

class FragmentEventos : Fragment() {

    private var _binding: FragmentEventosBinding? = null
    private val binding get() = _binding!!

    private lateinit var listaEventos: ArrayList<ModeloEvento>
    private lateinit var adaptadorEvento: AdaptadorEvento
    private lateinit var firebaseAuth: FirebaseAuth
    private var semillaVerificada = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        listaEventos = ArrayList()
        adaptadorEvento = AdaptadorEvento(requireContext(), listaEventos)
        binding.RvEventos.adapter = adaptadorEvento

        cargarEventos()

        binding.FabPublicarEvento.setOnClickListener {
            mostrarDialogoPublicarEvento()
        }
    }

    private fun cargarEventos() {
        val ref = FirebaseDatabase.getInstance().getReference("Eventos")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return

                if (!snapshot.exists() && !semillaVerificada) {
                    semillaVerificada = true
                    insertarEventosInicialesUnison()
                    return
                }

                listaEventos.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloEvento::class.java)
                    if (modelo != null) {
                        listaEventos.add(modelo)
                    }
                }
                listaEventos.reverse()
                adaptadorEvento.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun insertarEventosInicialesUnison() {
        val ref = FirebaseDatabase.getInstance().getReference("Eventos")
        val tiempoBase = Constantes.obtenerTiempoDis()

        val eventosDemo = listOf(
            ModeloEvento(
                id = "ev_unison_1",
                nombreEvento = "Foro de Inteligencia Artificial y Ciencia de Datos UNISON",
                imagenUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&q=80",
                descripcion = "Conferencias magistrales y demostración de proyectos estudiantiles sobre IA generativa, visión por computadora y robótica aplicada.",
                fechaHora = "15/11/2026 - 10:00 hrs",
                lugar = "Centro de las Artes UNISON",
                uidAutor = "unison_sistema",
                tiempo = tiempoBase - 3000
            ),
            ModeloEvento(
                id = "ev_unison_2",
                nombreEvento = "Hackathon Universitario Búhos 2026",
                imagenUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&q=80",
                descripcion = "Competencia de 24 horas para desarrollar soluciones tecnológicas sostenibles para el campus universitario. Abierto a todas las licenciaturas.",
                fechaHora = "22/11/2026 - 09:00 hrs",
                lugar = "Auditorio de Ingeniería Civil y Minas",
                uidAutor = "unison_sistema",
                tiempo = tiempoBase - 2000
            ),
            ModeloEvento(
                id = "ev_unison_3",
                nombreEvento = "Feria de Proyectos de Innovación e Investigación Estudiantil",
                imagenUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=800&q=80",
                descripcion = "Exposición de carteles científicos, prototipos e iniciativas interdisciplinarias desarrolladas por alumnos de la Universidad de Sonora.",
                fechaHora = "30/11/2026 - 11:30 hrs",
                lugar = "Explanada Biblioteca Central Fernando Pesqueira",
                uidAutor = "unison_sistema",
                tiempo = tiempoBase - 1000
            )
        )

        for (ev in eventosDemo) {
            ref.child(ev.id).setValue(ev)
        }
    }

    private fun mostrarDialogoPublicarEvento() {
        val dialogBinding = DialogAgregarEventoBinding.inflate(layoutInflater)

        dialogBinding.EtFechaHoraEvento.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->
                            val fechaHoraStr = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d - %02d:%02d hrs",
                                dayOfMonth,
                                month + 1,
                                year,
                                hourOfDay,
                                minute
                            )
                            dialogBinding.EtFechaHoraEvento.setText(fechaHoraStr)
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Publicar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val btnPublicar = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnPublicar.setOnClickListener {
                val nombre = dialogBinding.EtNombreEvento.text.toString().trim()
                val lugar = dialogBinding.EtLugarEvento.text.toString().trim()
                val fechaHora = dialogBinding.EtFechaHoraEvento.text.toString().trim()
                val imagenUrl = dialogBinding.EtImagenEvento.text.toString().trim()
                val descripcion = dialogBinding.EtDescripcionEvento.text.toString().trim()

                if (nombre.isEmpty()) {
                    dialogBinding.EtNombreEvento.error = "Ingrese el nombre del evento"
                } else if (lugar.isEmpty()) {
                    dialogBinding.EtLugarEvento.error = "Ingrese el lugar del evento"
                } else if (fechaHora.isEmpty()) {
                    dialogBinding.EtFechaHoraEvento.error = "Seleccione fecha y hora"
                } else if (descripcion.isEmpty()) {
                    dialogBinding.EtDescripcionEvento.error = "Ingrese la descripción"
                } else {
                    val urlFinal = if (imagenUrl.isEmpty()) {
                        "https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=800&q=80"
                    } else {
                        imagenUrl
                    }

                    val tiempo = Constantes.obtenerTiempoDis()
                    val ref = FirebaseDatabase.getInstance().getReference("Eventos")
                    val idEvento = ref.push().key ?: "$tiempo"

                    val nuevoEvento = ModeloEvento(
                        id = idEvento,
                        nombreEvento = nombre,
                        imagenUrl = urlFinal,
                        descripcion = descripcion,
                        fechaHora = fechaHora,
                        lugar = lugar,
                        uidAutor = firebaseAuth.uid ?: "",
                        tiempo = tiempo
                    )

                    ref.child(idEvento).setValue(nuevoEvento)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Evento académico publicado",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Error al publicar: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
