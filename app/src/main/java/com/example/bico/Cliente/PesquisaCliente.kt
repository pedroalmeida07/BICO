package com.example.bico.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.R
import com.example.bico.databinding.ActivityPesquisaClienteBinding

class PesquisaCliente : AppCompatActivity() {

    private lateinit var binding: ActivityPesquisaClienteBinding
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
        binding = ActivityPesquisaClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icHome.setOnClickListener {
            val intent = Intent(this, HomeCliente::class.java)
            startActivity(intent)
        }
    }
}