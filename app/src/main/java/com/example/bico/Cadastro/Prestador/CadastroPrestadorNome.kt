package com.example.bico.Cadastro.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorNomeBinding

class CadastroPrestadorNome : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPrestadorNomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityCadastroPrestadorNomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAvancar.setOnClickListener {
            val nome = binding.editTextNome.text.toString()
            val cpf = binding.editTextCPF.text.toString()

            if (nome.isEmpty() || cpf.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha o nome e o CPF", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                nome = nome,
                cpf = cpf,
                tipo = "PRESTADOR"
            )

            val intent = Intent(this, CadastroPrestadorUsuario::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}