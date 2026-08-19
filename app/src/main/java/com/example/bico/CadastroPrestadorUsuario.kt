package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityCadastroPrestadorUsuarioBinding

class CadastroPrestadorUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroPrestadorUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.Companion.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityCadastroPrestadorUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique
        binding.buttonAvancar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorServico::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.sair.setOnClickListener {
            //Fecha a tela atual e volta para a anterior
            finish()
        }
    }
}