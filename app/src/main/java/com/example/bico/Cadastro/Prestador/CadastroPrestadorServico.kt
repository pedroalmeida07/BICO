package com.example.bico.Cadastro.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorServicoBinding

class CadastroPrestadorServico : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroPrestadorServicoBinding

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

        binding = ActivityCadastroPrestadorServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAvancar.setOnClickListener {
            val tipo = binding.editTextTipo.text.toString()
            val local = binding.editTextLocal.text.toString()

            if (tipo.isEmpty() || local.isEmpty()) {
                Toast.makeText(this, "Preencha o serviço e a localização", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                servico = tipo,
                local = local
            )

            val intent = Intent(this, CadastroPrestadorEmail::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}