package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.RecuperacaoSenhaTrocarSenha
import com.example.bico.databinding.ActivityRecuperacaoSenhaCodigoBinding

class RecuperacaoSenhaCodigo : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperacaoSenhaCodigoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.Companion.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityRecuperacaoSenhaCodigoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique

        binding.buttonAvancar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, RecuperacaoSenhaTrocarSenha::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }
}