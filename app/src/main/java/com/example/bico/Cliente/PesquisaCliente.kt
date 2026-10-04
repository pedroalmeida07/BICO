package com.example.bico.Cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityPesquisaClienteBinding
import com.example.bico.utils.NoAccentsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PesquisaCliente : AppCompatActivity() {

    private lateinit var binding: ActivityPesquisaClienteBinding
    private lateinit var repository: UserRepository
    private lateinit var adapter: PrestadorAdapter

    private var tipoServicoSelecionado: String? = null
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )
        binding = ActivityPesquisaClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repository = UserRepository(this)

        setupRecyclerView()
        setupListeners()

        // Busca inicial com inicio = 0 e fim = 30
        realizarBusca()
    }

    private fun setupRecyclerView() {
        adapter = PrestadorAdapter { prestador ->
            val intent = Intent(this, VerPrestadorCliente::class.java).apply {
                putExtra("prestador", prestador)
            }
            startActivity(intent)
        }
        binding.rvPrestadores.adapter = adapter
    }

    private fun setupListeners() {
        // Clique no ícone de busca na barra
        binding.icSearchIcon.setOnClickListener {
            realizarBusca()
        }

        // Ação de busca no teclado (ImeAction)
        binding.editTextPesquisar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                realizarBusca()
                true
            } else {
                false
            }
        }

        // Debounce ao digitar na barra de pesquisa
        binding.editTextPesquisar.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(400) // Aguarda o usuário parar de digitar
                realizarBusca()
            }
        }

        // Botão de filtro por tipo de serviço
        binding.btnFiltro.setOnClickListener {
            mostrarDropdownFiltro()
        }

        // Navegação inferior
        binding.icHome.setOnClickListener {
            finish()
        }

        binding.icChat.setOnClickListener {
            val intent = Intent(this, DenunciasCliente::class.java)
            startActivity(intent)
        }

        binding.icConfig.setOnClickListener {
            val intent = Intent(this, ConfiguracaoCliente::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarDropdownFiltro() {
        val view = layoutInflater.inflate(R.layout.dialog_filtrar_servico, null)
        val editBuscaServico = view.findViewById<TextInputEditText>(R.id.editBuscaServico)
        val listViewServicos = view.findViewById<ListView>(R.id.listViewServicos)

        val servicosArray = resources.getStringArray(R.array.servicos)
        val todosOpcao = "Todos os serviços (Limpar filtro)"
        val listaCompleta = listOf(todosOpcao) + servicosArray.toList()

        val adapterServicos = NoAccentsAdapter(this, android.R.layout.simple_list_item_1, listaCompleta)
        listViewServicos.adapter = adapterServicos

        val dialog = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Filtrar por Tipo de Serviço")
            .setView(view)
            .setNegativeButton("Cancelar", null)
            .create()

        editBuscaServico.doAfterTextChanged { text ->
            adapterServicos.filter.filter(text.toString())
        }

        listViewServicos.setOnItemClickListener { _, _, position, _ ->
            val itemSelecionado = adapterServicos.getItem(position)
            if (itemSelecionado == null || itemSelecionado == todosOpcao) {
                tipoServicoSelecionado = null
                binding.txtFiltroAtual.text = ""
                binding.txtFiltroAtual.visibility = View.GONE
            } else {
                tipoServicoSelecionado = itemSelecionado
                val textoFiltro = "Filtro: $tipoServicoSelecionado"
                binding.txtFiltroAtual.text = textoFiltro
                binding.txtFiltroAtual.visibility = View.VISIBLE
            }
            dialog.dismiss()
            realizarBusca()
        }

        dialog.show()
    }

    private fun realizarBusca() {
        val usernameQuery = binding.editTextPesquisar.text.toString().trim().ifEmpty { null }

        binding.progressBar.visibility = View.VISIBLE
        binding.txtVazio.visibility = View.GONE

        lifecycleScope.launch {
            val resultado = repository.buscarPrestadores(
                username = usernameQuery,
                tipoServico = tipoServicoSelecionado,
                inicio = 0,
                fim = 30
            )

            binding.progressBar.visibility = View.GONE

            if (resultado.isNullOrEmpty()) {
                adapter.submitList(emptyList())
                binding.txtVazio.visibility = View.VISIBLE
            } else {
                binding.txtVazio.visibility = View.GONE
                adapter.submitList(resultado)
            }
        }
    }
}
