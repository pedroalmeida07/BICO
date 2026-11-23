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
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroClienteEmailBinding
import com.example.bico.utils.PasswordUtils
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
            binding.tilSenha.error = null
            binding.tilConfirmarSenha.error = null

            val email = binding.editTextEmail.text.toString().trim()
            val senha = binding.editTextSenha.text.toString()
            val confirmarSenha = binding.editTextConfirmarSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_preencha_todos_campos), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val erroSenhaResId = PasswordUtils.validarSenha(senha)
            if (erroSenhaResId != null) {
                binding.tilSenha.error = getString(erroSenhaResId)
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                binding.tilConfirmarSenha.error = getString(R.string.msg_senhas_nao_coincidem)
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempCliente = UserRepository.tempCliente.copy(
                email = email,
                senha = senha
            )

            // Salva o usuário definitivamente no repositório
            val repository = UserRepository(this)
            
            lifecycleScope.launch {
                val erro = repository.salvarCliente(UserRepository.tempCliente)
                if (erro == null) {
                    Toast.makeText(this@CadastroClienteEmail, getString(R.string.msg_cadastro_sucesso), Toast.LENGTH_SHORT).show()

                    // Vai para a tela inicial e limpa as telas anteriores
                    val intent = Intent(this@CadastroClienteEmail, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CadastroClienteEmail, erro, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}