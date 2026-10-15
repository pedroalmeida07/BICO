package com.example.bico.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityHomeClienteBinding
import com.example.bico.model.Prestador
import kotlinx.coroutines.launch

class HomeCliente : AppCompatActivity() {

    private lateinit var binding: ActivityHomeClienteBinding

    private lateinit var repository: UserRepository
    private lateinit var favoritosAdapter: PrestadorAdapter

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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

        carregarDadosUsuario()

        binding.navCliente.setOnItemSelectedListener { item ->
            when (item.itemId) {
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

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent)
        }

        binding.btnVerTodos.setOnClickListener {
            val intent = Intent(this, FavoritosCliente::class.java)
            startActivity(intent)
        }

        binding.pagPrestador1.setOnClickListener {
            lifecycleScope.launch {
                val usuarioBuscado = "hugoneves"
                val listaPrestadores = repository.buscarPrestadores(username = usuarioBuscado)

                val prestadorDoBanco = listaPrestadores?.find { 
                    it.usuario?.equals(usuarioBuscado, ignoreCase = true) == true ||
                    it.nome?.contains(usuarioBuscado, ignoreCase = true) == true
                } ?: listaPrestadores?.firstOrNull() ?: Prestador(
                    id = "hugoneves",
                    nome = "Hugo Neves",
                    usuario = "hugoneves",
                    telefone = "(27) 99999-9999",
                    email = "hugoneves@email.com",
                    descricao = "Pedreiro profissional com anos de experiência.",
                    local = "Serra-ES",
                    servicos = listOf("Pedreiro"),
                    notaMedia = 5.0,
                    totalAvaliacoes = 59
                )

                val intent = Intent(this@HomeCliente, VerPrestadorCliente::class.java).apply {
                    putExtra("prestador", prestadorDoBanco)
                }
                startActivity(intent)
            }
        }

        binding.pagPrestador2.setOnClickListener {
            lifecycleScope.launch {
                val usuarioBuscado = "takamasa"
                val listaPrestadores = repository.buscarPrestadores(username = usuarioBuscado)

                val prestadorDoBanco = listaPrestadores?.find { 
                    it.usuario?.equals(usuarioBuscado, ignoreCase = true) == true ||
                    it.nome?.contains(usuarioBuscado, ignoreCase = true) == true
                } ?: listaPrestadores?.firstOrNull() ?: Prestador(
                    id = "takamasa",
                    nome = "Takamasa N.",
                    usuario = "takamasa",
                    telefone = "(27) 98888-8888",
                    email = "takamasa@email.com",
                    descricao = "Ajudante geral e serviços de reforma.",
                    local = "Vitória-ES",
                    servicos = listOf("Ajudante"),
                    notaMedia = 4.9,
                    totalAvaliacoes = 112
                )

                val intent = Intent(this@HomeCliente, VerPrestadorCliente::class.java).apply {
                    putExtra("prestador", prestadorDoBanco)
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
        carregarFavoritos()
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
