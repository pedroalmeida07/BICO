package com.example.bico.Cliente

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.R
import com.example.bico.databinding.ActivityEditarClienteBinding


class EditarCliente : AppCompatActivity() {

    private lateinit var binding: ActivityEditarClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_cliente)

        binding = ActivityEditarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sair.setOnClickListener {
            finish()
        }
    }
}