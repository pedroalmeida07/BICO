package com.example.bico.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityHomeClienteBinding
import com.example.bico.model.Prestador
import com.example.bico.utils.setupBottomNavCliente
import kotlinx.coroutines.launch

class HomeCliente : AppCompatActivity() {

    private lateinit var binding: ActivityHomeClienteBinding

    private lateinit var repository: UserRepository
    private lateinit var favoritosAdapter: PrestadorAdapter
    private lateinit var melhoresAdapter: PrestadorDestaqueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
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
        binding = ActivityHomeClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.parentMain) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        repository = UserRepository(this)

        favoritosAdapter = PrestadorAdapter { prestador ->
            val intent = Intent(this, VerPrestadorCliente::class.java).apply {
                putExtra("prestador", prestador)
            }
            startActivity(intent)
        }
        binding.rvFavoritos.adapter = favoritosAdapter

        melhoresAdapter = PrestadorDestaqueAdapter { prestador ->
            val intent = Intent(this, VerPrestadorCliente::class.java).apply {
                putExtra("prestador", prestador)
            }
            startActivity(intent)
        }
        binding.rvMelhoresAvaliados.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMelhoresAvaliados.adapter = melhoresAdapter

        carregarDadosUsuario()

        setupBottomNavCliente(binding.layoutBarraInferior.navCliente, R.id.nav_home)

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent)
        }

        binding.btnVerTodos.setOnClickListener {
            val intent = Intent(this, FavoritosCliente::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
        carregarFavoritos()
        carregarMelhoresAvaliados()
    }

    private fun carregarMelhoresAvaliados() {
        lifecycleScope.launch {
            val prestadores = repository.buscarPrestadores(inicio = 0, limite = 50)
                .orEmpty()
                .sortedWith(
                    compareByDescending<Prestador> { it.notaMedia ?: 0.0 }
                        .thenByDescending { it.totalAvaliacoes ?: 0 }
                )
                .take(10)

            melhoresAdapter.submitList(prestadores)
            binding.rvMelhoresAvaliados.visibility =
                if (prestadores.isEmpty()) View.GONE else View.VISIBLE
            binding.txtSemMelhores.visibility =
                if (prestadores.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun carregarFavoritos() {
        lifecycleScope.launch {
            val favoritos = repository.getFavoritos()
            if (favoritos.isNotEmpty()) {
                favoritosAdapter.submitList(favoritos)
                binding.rvFavoritos.visibility = View.VISIBLE
            } else {
                favoritosAdapter.submitList(emptyList())
                binding.rvFavoritos.visibility = View.GONE
            }
        }
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
