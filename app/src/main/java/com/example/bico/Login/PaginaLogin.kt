package com.example.bico.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.Cadastro.Cliente.CadastroClienteNome
import com.example.bico.HomeCliente
import com.example.bico.HomePrestador
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityPaginaLoginBinding
import com.example.bico.model.User

class PaginaLogin : AppCompatActivity() {

    private lateinit var binding: ActivityPaginaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))
        binding = ActivityPaginaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(this)

        binding.btnNaoTenhoConta.setOnClickListener {
            // Reinicia o usuário temporário ao iniciar um novo cadastro
            UserRepository.tempUser = User()
            startActivity(Intent(this, CadastroClienteNome::class.java))
        }

        binding.btnEntrar.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = repository.realizarLogin(email, senha)
            if (usuario != null) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = if (usuario.tipo == "PRESTADOR") {
                    Intent(this, HomePrestador::class.java)
                } else {
                    Intent(this, HomeCliente::class.java)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}