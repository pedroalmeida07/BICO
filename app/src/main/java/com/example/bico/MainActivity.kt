package com.example.bico

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique

        binding.btnEntrar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, PaginaLogin::class.java)
            startActivity(intent) // Inicia a nova tela
            finish()
        }
        binding.btnCadastrar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroClienteNome::class.java)
            startActivity(intent) // Inicia a nova tela
            finish()
        }
        binding.txtCadastrarPrestador.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorNome::class.java)
            startActivity(intent) // Inicia a nova tela
            finish()
        }
    }
}
