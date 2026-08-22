package com.example.bico.Cadastro.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroClienteLocalBinding

class CadastroClienteLocal : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteLocalBinding

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

        binding = ActivityCadastroClienteLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAvancar.setOnClickListener {
            val cep = binding.editTextCEP.text.toString()
            val numero = binding.editTextNumero.text.toString()
            val complemento = binding.editTextComplemento.text.toString()

            if (cep.isEmpty() || numero.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha o CEP e o número", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                local = "$cep, $numero - $complemento",
                cep = cep,
                numero = numero,
                complemento = complemento
            )

            val intent = Intent(this, CadastroClienteEmail::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}