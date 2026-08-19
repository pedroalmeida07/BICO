package com.example.bico

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.CadastroClienteNome
import com.example.bico.CadastroPrestadorNome
import com.example.bico.PaginaLogin
import com.example.bico.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique
        binding.btnEntrar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, PaginaLogin::class.java)
            startActivity(intent) // Inicia a nova tela
        }
        binding.btnCadastrar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroClienteNome::class.java)
            startActivity(intent) // Inicia a nova tela
        }
        binding.txtCadastrarPrestador.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorNome::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }
}
