package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.databinding.ActivityCadastroClienteEmailBinding

class CadastroClienteEmail : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.Companion.light(
            Color.TRANSPARENT,
            Color.TRANSPARENT
        ))

        binding = ActivityCadastroClienteEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de clique

        binding.buttonAvancar.setOnClickListener {
        //Vai para a Home e limpa as telas anteriores
        val intent = Intent(this, HomeCliente::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        }

        binding.sair.setOnClickListener {
            //Fecha a tela atual e volta para a anterior
            finish()
        }
    }
}