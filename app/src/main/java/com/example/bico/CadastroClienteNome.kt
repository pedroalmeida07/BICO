package com.example.bico

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityCadastroClienteNomeBinding

class CadastroClienteNome : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteNomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ))

        binding = ActivityCadastroClienteNomeBinding.inflate(layoutInflater)
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
                tipo = "CLIENTE"
            )

            val intent = Intent(this, CadastroClienteLocal::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}