package com.example.bico.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.MainActivity
import com.example.bico.databinding.ActivityRecuperacaoSenhaTrocarSenhaBinding

class RecuperacaoSenhaTrocarSenha : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperacaoSenhaTrocarSenhaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityRecuperacaoSenhaTrocarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique

        binding.buttonAvancar.setOnClickListener {
            // Vai para a Main e limpa as telas anteriores
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}