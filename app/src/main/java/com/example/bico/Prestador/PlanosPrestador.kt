package com.example.bico.Prestador

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.R
import com.example.bico.databinding.ActivityPlanosPrestadorBinding

class PlanosPrestador : AppCompatActivity() {

    private lateinit var binding: ActivityPlanosPrestadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlanosPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.sair.setOnClickListener {
            finish()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val nav = binding.layoutBarraInferior.root
        nav.setOnItemSelectedListener(null)
        nav.selectedItemId = R.id.nav_config_prestador
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_prestador -> {
                    startActivity(Intent(this, HomePrestador::class.java))
                    finish()
                    true
                }
                R.id.nav_chat_prestador -> true
                R.id.nav_config_prestador -> {
                    startActivity(Intent(this, ConfiguracoesPrestador::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil_prestador -> {
                    startActivity(Intent(this, EditarPrestador::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.layoutBarraInferior.root.selectedItemId = R.id.nav_config_prestador
    }
}
