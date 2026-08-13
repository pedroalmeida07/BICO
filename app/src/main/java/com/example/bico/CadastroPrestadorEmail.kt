package com.example.bico

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.databinding.ActivityCadastroPrestadorEmailBinding

class CadastroPrestadorEmail : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroPrestadorEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCadastroPrestadorEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique

        binding.sair.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorServico::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }
}