package com.example.bico.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityRecuperacaoSenhaTrocarSenhaBinding

class RecuperacaoSenhaTrocarSenha : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperacaoSenhaTrocarSenhaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityRecuperacaoSenhaTrocarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EMAIL_RECUPERACAO")
        val repository = UserRepository(this)

        // Evento de clique

        binding.buttonAvancar.setOnClickListener {
            val novaSenha = binding.edtNovaSenha.text.toString()
            val confirmarSenha = binding.edtConfirmarSenha.text.toString()

            if (novaSenha.isEmpty()) {
                binding.edtNovaSenha.error = "Digite a nova senha"
                return@setOnClickListener
            }

            if (novaSenha != confirmarSenha) {
                binding.edtConfirmarSenha.error = "As senhas não coincidem"
                return@setOnClickListener
            }

            if (email != null) {
                val sucesso = repository.atualizarSenha(email, novaSenha)
                if (sucesso) {
                    Toast.makeText(this, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    // Vai para a Main e limpa as telas anteriores
                    val intent = Intent(this, PaginaLogin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Erro ao atualizar senha. E-mail não encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}