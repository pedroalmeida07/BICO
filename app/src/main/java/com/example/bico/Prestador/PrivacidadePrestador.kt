package com.example.bico.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.R
import com.example.bico.databinding.ActivityPrivacidadePrestadorBinding

class PrivacidadePrestador : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacidadePrestadorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )

        binding = ActivityPrivacidadePrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sair.setOnClickListener {
            finish()
        }

        //Barra de Baixo
        binding.icHome.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, HomePrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }
        binding.icUserBarra.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent) // Inicia a nova tela
        }

    }
}