package com.example.bico

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityCadastroPrestadorUsuarioBinding

class CadastroPrestadorUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroPrestadorUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ))

        binding = ActivityCadastroPrestadorUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAvancar.setOnClickListener {
            val usuario = binding.editTextNome.text.toString()

            if (usuario.isEmpty()) {
                Toast.makeText(this, "Por favor, informe um nome de usuário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                usuario = usuario
            )

            val intent = Intent(this, CadastroPrestadorServico::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}
