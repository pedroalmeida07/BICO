package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.CadastroPrestadorUsuario
import com.example.bico.databinding.ActivityCadastroPrestadorNomeBinding

class CadastroPrestadorNome : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPrestadorNomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.Companion.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityCadastroPrestadorNomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique
        binding.buttonAvancar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorUsuario::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.sair.setOnClickListener {
            //Fecha a tela atual e volta para a anterior
            finish()
        }
    }
}