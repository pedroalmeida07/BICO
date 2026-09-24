package com.example.bico.Cliente

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.R
import com.example.bico.databinding.ActivityVerPrestadorClienteBinding

class VerPrestadorCliente : AppCompatActivity() {

    private lateinit var binding: ActivityVerPrestadorClienteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivityVerPrestadorClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sair.setOnClickListener {
            finish()
        }

    }
}