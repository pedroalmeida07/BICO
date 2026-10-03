package com.example.bico.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bico.R
import com.example.bico.ServicoAdapter
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityVerPrestadorClienteBinding
import com.example.bico.model.Prestador
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class VerPrestadorCliente : AppCompatActivity() {

    private lateinit var binding: ActivityVerPrestadorClienteBinding
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVerPrestadorClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = UserRepository(this)

        // Tenta obter o prestador do Intent, senão busca o usuário logado
        @Suppress("DEPRECATION")
        val prestadorIntent = intent.getSerializableExtra("prestador") as? Prestador

        if (prestadorIntent != null) {
            carregarDadosPrestador(prestadorIntent)
        } else {
            lifecycleScope.launch {
                val user = repository.usuarioLogado()
                if (user is Prestador) {
                    carregarDadosPrestador(user)
                }
            }
        }

        binding.sair.setOnClickListener {
            finish()
        }

        //Barra de baixo
        binding.icHome.setOnClickListener {
            val intent = Intent(this, HomeCliente::class.java)
            startActivity(intent)
        }

        binding.icConfig.setOnClickListener {
            val intent = Intent(this, ConfiguracaoCliente::class.java)
            startActivity(intent)
        }

        binding.icUserBarra.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent)
        }
    }

    private fun carregarDadosPrestador(prestador: Prestador) {
        // UserName
        binding.txtNomePrestador.text = (prestador.usuario ?: "").ifEmpty { "UserName" }

        // Sobre
        binding.txtDesc.text = (prestador.descricao ?: "").ifEmpty { "Nenhuma descrição informada." }

        // Local de Atuação
        binding.txtCidade.text = (prestador.local ?: "").ifEmpty { "Local" }

        // Serviços oferecidos
        val servicos = prestador.servicos ?: emptyList()
        val adapter = ServicoAdapter(servicos) { _ -> 
            // Modo leitura para clientes (sem ação de exclusão no long click)
        }
        binding.rvServicos.adapter = adapter

    }
}
