package com.example.bico.Prestador

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.Login.PaginaLogin
import com.example.bico.R
import com.example.bico.databinding.ActivityConfiguracoesPrestadorBinding

class ConfiguracoesPrestador : AppCompatActivity() {
    private lateinit var binding: ActivityConfiguracoesPrestadorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfiguracoesPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icHelp.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, AjudaPrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }
}