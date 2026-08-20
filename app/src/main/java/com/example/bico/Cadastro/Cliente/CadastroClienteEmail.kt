package com.example.bico.Cadastro.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.HomeCliente
import com.example.bico.MainActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroClienteEmailBinding

class CadastroClienteEmail : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityCadastroClienteEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAvancar.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val senha = binding.editTextSenha.text.toString()
            val confirmarSenha = binding.editTextConfirmarSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                email = email,
                senha = senha
            )

            // Salva o usuário definitivamente no repositório
            val repository = UserRepository(this)
            repository.salvarUsuario(UserRepository.tempUser)

            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

            // Vai para a Home e limpa as telas anteriores
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}