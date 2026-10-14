package com.example.bico.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.Cadastro.CadCliente.CadastroClienteNome
import com.example.bico.Cliente.MainClienteActivity
import com.example.bico.Prestador.MainPrestadorActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityPaginaLoginBinding
import com.example.bico.model.Cliente
import kotlinx.coroutines.launch

class PaginaLogin : AppCompatActivity() {

    private lateinit var binding: ActivityPaginaLoginBinding

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
        binding = ActivityPaginaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(this)

        binding.btnNaoTenhoConta.setOnClickListener {
            // Reinicia o usuário temporário ao iniciar um novo cadastro
            UserRepository.resetTempUser()
            startActivity(Intent(this, CadastroClienteNome::class.java))
        }

        binding.txtEsqueceuSenha.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, RecuperacaoSenhaEmail::class.java)
            startActivity(intent) // Inicia a nova tela
        }

        binding.btnEntrar.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = repository.realizarLogin(email, senha)
                if (usuario != null) {
                    Toast.makeText(this@PaginaLogin, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    val targetClass = if (usuario is Cliente) {
                        MainClienteActivity::class.java
                    } else {
                        MainPrestadorActivity::class.java
                    }
                    val intent = Intent(this@PaginaLogin, targetClass)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@PaginaLogin, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}