package com.example.bico.Cliente

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.Login.PaginaLogin
import com.example.bico.R
import com.example.bico.databinding.ActivityVerPrestadorClienteBinding

class VerPrestadorCliente : AppCompatActivity() {

    private lateinit var binding: ActivityVerPrestadorClienteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivityVerPrestadorClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sair.setOnClickListener {
            finish()
        }

        //Barra de baixo
        binding.icHome.setOnClickListener {
            val intent = Intent(this, HomeCliente::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.icConfig.setOnClickListener {
            val intent = Intent(this, ConfiguracaoCliente::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.icUserBarra.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent) // Inicia a nova tela
        }

    }
}