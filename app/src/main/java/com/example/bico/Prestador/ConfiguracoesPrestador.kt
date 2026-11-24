package com.example.bico.Prestador

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.R
import com.example.bico.databinding.ActivityConfiguracoesPrestadorBinding
import com.example.bico.utils.setupBottomNavPrestador

class ConfiguracoesPrestador : AppCompatActivity() {
    private lateinit var binding: ActivityConfiguracoesPrestadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfiguracoesPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavPrestador(binding.layoutBarraInferior.navPrestador, R.id.nav_config_prestador)

        binding.layoutEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent)
        }

        binding.layoutImpulso.setOnClickListener {
            val intent = Intent(this, PlanosPrestador::class.java)
            startActivity(intent)

        }
    }
}
