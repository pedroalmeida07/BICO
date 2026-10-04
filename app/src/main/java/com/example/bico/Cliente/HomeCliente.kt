package com.example.bico.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
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
        carregarDadosUsuario()

        binding.icPesquisa.setOnClickListener {
            val intent = Intent(this, PesquisaCliente::class.java)
            startActivity(intent)
        }

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent)
        }



        binding.pagPrestador1.setOnClickListener {
            lifecycleScope.launch {
                val usuarioBuscado = "hugoneves"
                val listaPrestadores = repository.buscarPrestadores(username = usuarioBuscado)

                // Encontra o prestador correspondente ao usuario buscado
                val prestadorDoBanco = listaPrestadores?.find { 
                    it.usuario?.equals(usuarioBuscado, ignoreCase = true) == true ||
                    it.nome?.contains(usuarioBuscado, ignoreCase = true) == true
                } ?: listaPrestadores?.firstOrNull()

                if (prestadorDoBanco != null) {
                    val intent = Intent(this@HomeCliente, VerPrestadorCliente::class.java).apply {
                        putExtra("prestador", prestadorDoBanco)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@HomeCliente, "Prestador não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.pagPrestador2.setOnClickListener {
            lifecycleScope.launch {
                val usuarioBuscado = "takamasa"
                val listaPrestadores = repository.buscarPrestadores(username = usuarioBuscado)

                // Encontra o prestador correspondente ao usuario buscado
                val prestadorDoBanco = listaPrestadores?.find { 
                    it.usuario?.equals(usuarioBuscado, ignoreCase = true) == true ||
                    it.nome?.contains(usuarioBuscado, ignoreCase = true) == true
                } ?: listaPrestadores?.firstOrNull()

                if (prestadorDoBanco != null) {
                    val intent = Intent(this@HomeCliente, VerPrestadorCliente::class.java).apply {
                        putExtra("prestador", prestadorDoBanco)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@HomeCliente, "Prestador não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
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
            }
        }
    }
}