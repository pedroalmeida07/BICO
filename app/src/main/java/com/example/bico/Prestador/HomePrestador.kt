package com.example.bico.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityHomePrestadorBinding
import kotlinx.coroutines.launch

class HomePrestador : AppCompatActivity() {

    private lateinit var binding: ActivityHomePrestadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                ContextCompat.getColor(this, R.color.azul),
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )
        binding = ActivityHomePrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarDadosUsuario()

        val temServicos = false

        if (temServicos) {
            binding.cardProximoServico.visibility = View.VISIBLE
            binding.txtSemServicos.visibility = View.GONE
        } else {
            binding.cardProximoServico.visibility = View.GONE
            binding.txtSemServicos.visibility = View.VISIBLE
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupBottomNavigation()

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        val nav = binding.layoutBarraInferior.root
        nav.setOnItemSelectedListener(null)
        nav.selectedItemId = R.id.nav_home_prestador
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_prestador -> true
                R.id.nav_chat_prestador -> true
                R.id.nav_config_prestador -> {
                    startActivity(Intent(this, ConfiguracoesPrestador::class.java))
                    true
                }
                R.id.nav_perfil_prestador -> {
                    startActivity(Intent(this, EditarPrestador::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
        binding.layoutBarraInferior.root.selectedItemId = R.id.nav_home_prestador
    }

    private fun carregarDadosUsuario() {
        val repository = UserRepository(this)
        lifecycleScope.launch {
            val usuario = repository.usuarioLogado()
            usuario?.let { 
                binding.txtNomeUsuario.text = it.primeiroNome.ifEmpty { "Usuário" }
                if (!it.fotoPerfil.isNullOrEmpty()) {
                    binding.imgUser.load(it.fotoPerfil) {
                        crossfade(true)
                        placeholder(R.drawable.user)
                        error(R.drawable.user)
                    }
                } else {
                    binding.imgUser.setImageResource(R.drawable.user)
                }
            }
        }
    }
}
