package com.example.bico.Prestador

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityConfiguracoesPrestadorBinding
import com.example.bico.R
import com.example.bico.utils.setupBottomNavPrestador

class ConfiguracoesPrestador : AppCompatActivity() {
    private lateinit var binding: ActivityConfiguracoesPrestadorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfiguracoesPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavPrestador(binding.layoutBarraInferior.root, R.id.nav_config_prestador)

        binding.layoutPrivacidade.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, PrivacidadePrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.layoutAjuda.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, AjudaPrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.layoutEditarPerfil.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.layoutImpulso.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, PlanosPrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }



    }
}