package com.example.bico.Cadastro.CadCliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.MainActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroClienteEmailBinding
import kotlinx.coroutines.launch

class CadastroClienteEmail : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )

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
            
            lifecycleScope.launch {
                val sucesso = repository.salvarCliente(UserRepository.tempUser)
                if (sucesso) {
                    Toast.makeText(this@CadastroClienteEmail, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Vai para a tela inicial e limpa as telas anteriores
                    val intent = Intent(this@CadastroClienteEmail, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CadastroClienteEmail, "Erro ao realizar cadastro", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}