package com.example.bico.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityFavoritosClienteBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FavoritosCliente : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritosClienteBinding
    private lateinit var repository: UserRepository
    private lateinit var adapter: PrestadorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoritosClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        repository = UserRepository(this)

        binding.layoutBarraInferior.navCliente.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeCliente::class.java))
                    true
                }
                R.id.nav_pesquisa -> {
                    startActivity(Intent(this, PesquisaCliente::class.java))
                    true
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, ConfiguracaoCliente::class.java))
                    true
                }
                else -> false
            }
        }

        adapter = PrestadorAdapter { prestador ->
            val intent = Intent(this, VerPrestadorCliente::class.java).apply {
                putExtra("prestador", prestador)
            }
            startActivity(intent)
        }
        binding.rvFavoritos.adapter = adapter

        binding.sair.setOnClickListener {
            finish()
        }

        carregarFavoritos()
    }

    override fun onResume() {
        super.onResume()
        carregarFavoritos()
    }

    private fun carregarFavoritos() {
        lifecycleScope.launch {
            val favoritos = repository.getFavoritos()
            if (favoritos.isNullOrEmpty()) {
                adapter.submitList(emptyList())
                binding.txtVazio.visibility = View.VISIBLE
            } else {
                binding.txtVazio.visibility = View.GONE
                adapter.submitList(favoritos)
            }
        }
    }
}
