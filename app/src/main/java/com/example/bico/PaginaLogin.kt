package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.RecuperacaoSenhaEmail
import com.example.bico.databinding.ActivityPaginaLoginBinding

class PaginaLogin : AppCompatActivity() {

    private lateinit var binding: ActivityPaginaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.Companion.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))
        binding = ActivityPaginaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNaoTenhoConta.setOnClickListener {
            startActivity(Intent(this, CadastroClienteNome::class.java))
            finish()
        }

        binding.txtEsqueceuSenha.setOnClickListener {
            startActivity(Intent(this, RecuperacaoSenhaEmail::class.java))
            finish()
        }

        binding.btnEntrar.setOnClickListener {
            startActivity(Intent(this, HomeCliente::class.java))
            finish()
        }
    }
}