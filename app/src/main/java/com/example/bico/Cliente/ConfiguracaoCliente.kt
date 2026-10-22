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
import com.example.bico.databinding.ActivityConfiguracaoClienteBinding
import com.example.bico.utils.setupBottomNavCliente

class ConfiguracaoCliente : AppCompatActivity() {
    private lateinit var binding: ActivityConfiguracaoClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )
        binding = ActivityConfiguracaoClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupBottomNavCliente(binding.layoutBarraInferior.navCliente, R.id.nav_config)

        binding.layoutPrivacidade.setOnClickListener {
            val intent = Intent(this, PrivacidadeCliente::class.java)
            startActivity(intent)
        }

        binding.layoutEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent)
        }

        binding.layoutFavoritos.setOnClickListener {
            val intent = Intent(this, FavoritosCliente::class.java)
            startActivity(intent)
        }

        binding.layoutAjuda.setOnClickListener {
            val intent = Intent(this, AjudaCliente::class.java)
            startActivity(intent)
        }
    }
}
