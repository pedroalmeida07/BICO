package com.example.bico.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityRecuperacaoSenhaCodigoBinding

class RecuperacaoSenhaCodigo : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperacaoSenhaCodigoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityRecuperacaoSenhaCodigoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cria uma variável com o email inserido na última tela pra repassar pra próxima
        val email = intent.getStringExtra("EMAIL_RECUPERACAO")

        // Evento de clique

        binding.buttonAvancar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, RecuperacaoSenhaTrocarSenha::class.java)
            intent.putExtra("EMAIL_RECUPERACAO", email)
            startActivity(intent) // Inicia a nova tela
        }
    }
}