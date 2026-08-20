package com.example.bico.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityRecuperacaoSenhaEmailBinding

class RecuperacaoSenhaEmail : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperacaoSenhaEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityRecuperacaoSenhaEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique

        binding.buttonAvancar.setOnClickListener {
            val email = binding.edtEmail.text.toString()

            if (email.isNotEmpty()) {
                // Criar o Intent para abrir a outra Activity
                val intent = Intent(this, RecuperacaoSenhaCodigo::class.java)
                intent.putExtra("EMAIL_RECUPERACAO", email)
                startActivity(intent) // Inicia a nova tela
            } else {
                binding.edtEmail.error = "Por favor, insira seu e-mail"
            }
        }
    }
}