package com.example.bico.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.example.bico.model.Prestador
import com.example.bico.utils.setupBottomNavPrestador
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupBottomNavPrestador(binding.layoutBarraInferior.navPrestador, R.id.nav_home_prestador)

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
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

                if (it is Prestador) {
                    val nota = it.notaMedia ?: 0.0
                    val total = it.totalAvaliacoes ?: 0

                    binding.txtNotaMedia.text = if (nota > 0) String.format("%.1f ★", nota) else "0.0 ★"
                    binding.txtTotalAvaliacoes.text = "$total"
                }
            }
        }
    }
}
