package com.example.bico

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.databinding.ActivityPaginaLoginBinding

class PaginaLogin : AppCompatActivity() {

    private lateinit var binding: ActivityPaginaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ))
        binding = ActivityPaginaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNaoTenhoConta.setOnClickListener {
            startActivity(Intent(this, CadastroClienteNome::class.java))
            finish()
        }
    }
}