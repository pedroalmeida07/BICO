package com.example.bico

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.databinding.ActivityCadastroClienteLocalBinding

class CadastroClienteLocal : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteLocalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCadastroClienteLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique
        binding.buttonAvancar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroClienteEmail::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.sair.setOnClickListener {
            //Fecha a tela atual e volta para a anterior
            finish()
        }
    }
}