package com.example.appgestioneventosuni

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appgestioneventosuni.Fragmentos.FragmentCerrarSesion
import com.example.appgestioneventosuni.Fragmentos.FragmentConsultarProyectos
import com.example.appgestioneventosuni.Fragmentos.FragmentEventos
import com.example.appgestioneventosuni.Fragmentos.FragmentPublicarProyecto
import com.example.appgestioneventosuni.Fragmentos.FragmentRegistroInteresados
import com.example.appgestioneventosuni.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        // Mostrar primer fragmento por defecto
        verFragmentEventos()

        binding.BottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Item_Eventos -> {
                    verFragmentEventos()
                    true
                }
                R.id.Item_Publicar -> {
                    verFragmentPublicar()
                    true
                }
                R.id.Item_Proyectos -> {
                    verFragmentProyectos()
                    true
                }
                R.id.Item_Interesados -> {
                    verFragmentInteresados()
                    true
                }
                R.id.Item_CerrarSesion -> {
                    verFragmentCerrarSesion()
                    true
                }
                else -> false
            }
        }
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, OpcionesLogin::class.java))
            finishAffinity()
        }
    }

    private fun verFragmentEventos() {
        binding.TituloRL.text = getString(R.string.titulo_eventos)
        val fragment = FragmentEventos()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentEventos")
        fragmentTransition.commit()
    }

    private fun verFragmentPublicar() {
        binding.TituloRL.text = getString(R.string.titulo_publicar)
        val fragment = FragmentPublicarProyecto()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentPublicarProyecto")
        fragmentTransition.commit()
    }

    private fun verFragmentProyectos() {
        binding.TituloRL.text = getString(R.string.titulo_proyectos)
        val fragment = FragmentConsultarProyectos()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentConsultarProyectos")
        fragmentTransition.commit()
    }

    private fun verFragmentInteresados() {
        binding.TituloRL.text = getString(R.string.titulo_interesados)
        val fragment = FragmentRegistroInteresados()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentRegistroInteresados")
        fragmentTransition.commit()
    }

    private fun verFragmentCerrarSesion() {
        binding.TituloRL.text = getString(R.string.titulo_cerrar_sesion)
        val fragment = FragmentCerrarSesion()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentCerrarSesion")
        fragmentTransition.commit()
    }
}