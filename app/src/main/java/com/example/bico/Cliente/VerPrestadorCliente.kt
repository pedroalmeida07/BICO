package com.example.bico.Cliente

import android.content.Context
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


    }

    private fun carregarDadosPrestador(prestador: Prestador) {
        // Nome e UserName
        binding.txtNomePrestador.text = prestador.nome?.takeIf { it.isNotEmpty() } ?: prestador.usuario ?: "Prestador"

        // Nota e Avaliações
        binding.txtNotaPrestador.text = String.format(java.util.Locale.getDefault(), "%.1f", prestador.notaMedia ?: 5.0)
        binding.txtTotalAvaliacoes.text = String.format(java.util.Locale.getDefault(), " (%d Avaliações)", prestador.totalAvaliacoes ?: 0)

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

        var isFavorito = false

        // Verificar se é favorito
        lifecycleScope.launch {
            val favoritos = repository.getFavoritos()
            isFavorito = favoritos.any { 
                (prestador.id != null && it.id == prestador.id) || 
                (!prestador.usuario.isNullOrEmpty() && it.usuario.equals(prestador.usuario, ignoreCase = true)) ||
                (!prestador.email.isNullOrEmpty() && it.email.equals(prestador.email, ignoreCase = true))
            }

            if (isFavorito) {
                binding.btnFavorito.setImageResource(R.drawable.ic_estrela_cheia)
            } else {
                binding.btnFavorito.setImageResource(R.drawable.ic_estrela_vazia)
            }
        }

        binding.btnFavorito.setOnClickListener {
            lifecycleScope.launch {
                if (isFavorito) {
                    val sucesso = repository.removerFavorito(prestador)
                    if (sucesso) {
                        isFavorito = false
                        binding.btnFavorito.setImageResource(R.drawable.ic_estrela_vazia)
                        android.widget.Toast.makeText(this@VerPrestadorCliente, "Removido dos favoritos", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(this@VerPrestadorCliente, "Erro ao desfavoritar prestador", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val sucesso = repository.adicionarFavorito(prestador)
                    if (sucesso) {
                        isFavorito = true
                        binding.btnFavorito.setImageResource(R.drawable.ic_estrela_cheia)
                        android.widget.Toast.makeText(this@VerPrestadorCliente, "Prestador adicionado aos favoritos!", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(this@VerPrestadorCliente, "Erro ao favoritar prestador", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnAvaliar.setOnClickListener {
            showDialogAvaliacao(prestador)
        }
    }

    private fun showDialogAvaliacao(prestador: Prestador) {
        val view = layoutInflater.inflate(R.layout.dialog_adicionar_avaliacao, null)
        val ratingBar = view.findViewById<android.widget.RatingBar>(R.id.ratingBar)
        val editComentario = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editComentario)
        val btnAvaliar = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAvaliarDialog)

        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setView(view)
            .create()

        var notaAnterior = 0f
        @android.annotation.SuppressLint("ClickableViewAccessibility")
        ratingBar.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (ratingBar.rating == notaAnterior && notaAnterior > 0f) {
                    ratingBar.rating = 0f
                    notaAnterior = 0f
                    return@setOnTouchListener true
                } else {
                    notaAnterior = ratingBar.rating
                }
            }
            false
        }

        btnAvaliar.setOnClickListener {
            val prestadorId = prestador.id?.takeIf { it.isNotEmpty() }
                ?: prestador.usuario?.takeIf { it.isNotEmpty() }
                ?: ""

            if (prestadorId.isEmpty()) {
                android.widget.Toast.makeText(this, "ID do prestador inválido", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nota = ratingBar.rating.toInt()
            val comentario = editComentario.text?.toString()

            lifecycleScope.launch {
                val sucesso = repository.adicionarAvaliacao(prestadorId, nota, comentario)
                if (sucesso) {
                    android.widget.Toast.makeText(this@VerPrestadorCliente, "Avaliação enviada com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    android.widget.Toast.makeText(this@VerPrestadorCliente, "Erro ao enviar avaliação", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }
}
