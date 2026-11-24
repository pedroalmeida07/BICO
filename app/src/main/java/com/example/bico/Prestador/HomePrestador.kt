package com.example.bico.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bico.AvaliacaoAdapter
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityHomePrestadorBinding
import com.example.bico.model.Avaliacao
import com.example.bico.model.Prestador
import com.example.bico.utils.setupBottomNavPrestador
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class HomePrestador : AppCompatActivity() {

    private lateinit var binding: ActivityHomePrestadorBinding
    private var avaliacoesAtuais: List<Avaliacao> = emptyList()

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

        binding.cardTotalAvaliacoes.setOnClickListener { mostrarAvaliacoes() }

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
            val usuario = repository.usuarioLogado(forceRefresh = true)
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
                    val avaliacoes = it.avaliacoes.orEmpty()
                    val total = it.totalAvaliacoes ?: avaliacoes.size

                    avaliacoesAtuais = avaliacoes
                    binding.txtNotaMedia.text = if (nota > 0) String.format("%.1f ★", nota) else "0.0 ★"
                    binding.txtTotalAvaliacoes.text = total.toString()
                }
            }
        }
    }

    private fun mostrarAvaliacoes() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_avaliacoes_prestador, null)
        val recycler = view.findViewById<RecyclerView>(R.id.rvAvaliacoes)
        val txtVazio = view.findViewById<TextView>(R.id.txtSemAvaliacoes)

        if (avaliacoesAtuais.isEmpty()) {
            recycler.visibility = View.GONE
            txtVazio.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            txtVazio.visibility = View.GONE
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = AvaliacaoAdapter(avaliacoesAtuais)
            recycler.layoutParams.height = (resources.displayMetrics.heightPixels * 0.6f).toInt()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
