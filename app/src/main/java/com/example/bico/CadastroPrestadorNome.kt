package com.example.bico

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.databinding.ActivityCadastroPrestadorNomeBinding

class CadastroPrestadorNome : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPrestadorNomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCadastroPrestadorNomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique
        binding.buttonAvancar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorUsuario::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }
}