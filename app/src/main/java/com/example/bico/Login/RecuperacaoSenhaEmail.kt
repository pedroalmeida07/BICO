package com.example.bico.Login

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityRecuperacaoSenhaEmailBinding
import kotlinx.coroutines.launch

class RecuperacaoSenhaEmail : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperacaoSenhaEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
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

        binding = ActivityRecuperacaoSenhaEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(this)

        // Evento de clique
        binding.buttonAvancar.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                binding.buttonAvancar.isEnabled = false
                lifecycleScope.launch {
                    val sucesso = repository.enviarEmailRedefinicaoSenha(email)
                    binding.buttonAvancar.isEnabled = true
                    if (sucesso) {
                        Toast.makeText(
                            this@RecuperacaoSenhaEmail,
                            "E-mail de redefinição de senha enviado com sucesso!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RecuperacaoSenhaEmail,
                            "Erro ao enviar e-mail. Verifique o endereço e tente novamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                binding.edtEmail.error = "Por favor, insira seu e-mail"
            }
        }
    }
}