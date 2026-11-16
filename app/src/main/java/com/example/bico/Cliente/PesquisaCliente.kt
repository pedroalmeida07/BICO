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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityPesquisaClienteBinding
import com.example.bico.utils.NoAccentsAdapter
import com.example.bico.utils.setupBottomNavCliente
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
    private var modoBusca: String? = null
    private var raioMaximo: Float? = null
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
            val isTecladoVisivel = insets.isVisible(WindowInsetsCompat.Type.ime())
            binding.layoutBarraInferior.root.visibility = if (isTecladoVisivel) View.GONE else View.VISIBLE

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        repository = UserRepository(this)

        setupBottomNavCliente(binding.layoutBarraInferior.navCliente, R.id.nav_pesquisa)

        setupRecyclerView()
        setupListeners()

        atualizarEstadoChips()
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
        binding.icSearchIcon.setOnClickListener {
            realizarBusca()
        }

        binding.editTextPesquisar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                realizarBusca()
                true
            } else {
                false
            }
        }

        binding.editTextPesquisar.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(400)
                realizarBusca()
            }
        }

        binding.btnFiltro.setOnClickListener {
            val estaVisivel = binding.layoutOpcoesFiltro.visibility == View.VISIBLE
            binding.layoutOpcoesFiltro.visibility = if (estaVisivel) View.GONE else View.VISIBLE
        }

        binding.chipTipoServico.setOnClickListener {
            mostrarDropdownFiltro()
        }

        binding.chipMelhorAvaliados.setOnClickListener {
            modoBusca = null
            raioMaximo = null
            atualizarTextoFiltros()
            realizarBusca()
        }

        binding.chipDistanciaMaxima.setOnClickListener {
            mostrarDialogDistanciaMaxima()
        }
    }

    private fun atualizarTextoFiltros() {
        val listaFiltros = mutableListOf<String>()

        tipoServicoSelecionado?.let {
            listaFiltros.add("Serviço: $it")
        }

        if (modoBusca == "proximos" && raioMaximo != null) {
            val raioStr = if (raioMaximo!! % 1f == 0f) raioMaximo!!.toInt().toString() else raioMaximo.toString()
            listaFiltros.add("Distância: até ${raioStr} km")
        }

        if (listaFiltros.isNotEmpty()) {
            binding.txtFiltroAtual.text = "Filtros: " + listaFiltros.joinToString(" • ")
            binding.txtFiltroAtual.visibility = View.VISIBLE
        } else {
            binding.txtFiltroAtual.text = ""
            binding.txtFiltroAtual.visibility = View.GONE
        }

        atualizarEstadoChips()
    }

    private fun atualizarEstadoChips() {
        // 1. Chip Tipo de Serviço
        if (tipoServicoSelecionado != null) {
            binding.chipTipoServico.setChipBackgroundColorResource(R.color.azul)
            binding.chipTipoServico.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.chipTipoServico.text = "Tipo: $tipoServicoSelecionado"
        } else {
            binding.chipTipoServico.setChipBackgroundColorResource(R.color.cinzaClaro)
            binding.chipTipoServico.setTextColor(ContextCompat.getColor(this, R.color.azul))
            binding.chipTipoServico.text = "Tipo de serviço"
        }

        // 2. Chip Melhor Avaliados
        if (modoBusca == null) {
            binding.chipMelhorAvaliados.setChipBackgroundColorResource(R.color.azul)
            binding.chipMelhorAvaliados.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.chipMelhorAvaliados.setChipBackgroundColorResource(R.color.cinzaClaro)
            binding.chipMelhorAvaliados.setTextColor(ContextCompat.getColor(this, R.color.azul))
        }

        // 3. Chip Distância Máxima
        if (modoBusca == "proximos" && raioMaximo != null) {
            val raioStr = if (raioMaximo!! % 1f == 0f) raioMaximo!!.toInt().toString() else raioMaximo.toString()
            binding.chipDistanciaMaxima.setChipBackgroundColorResource(R.color.azul)
            binding.chipDistanciaMaxima.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.chipDistanciaMaxima.text = "Até ${raioStr} km"
        } else {
            binding.chipDistanciaMaxima.setChipBackgroundColorResource(R.color.cinzaClaro)
            binding.chipDistanciaMaxima.setTextColor(ContextCompat.getColor(this, R.color.azul))
            binding.chipDistanciaMaxima.text = "Distância máxima"
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
            } else {
                tipoServicoSelecionado = itemSelecionado
            }
            atualizarTextoFiltros()
            dialog.dismiss()
            realizarBusca()
        }

        dialog.show()
    }

    private fun mostrarDialogDistanciaMaxima() {
        val view = layoutInflater.inflate(R.layout.dialog_distancia_maxima, null)
        val editRaio = view.findViewById<TextInputEditText>(R.id.editRaioMaximo)

        raioMaximo?.let {
            val texto = if (it % 1f == 0f) it.toInt().toString() else it.toString()
            editRaio.setText(texto)
        }

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Distância Máxima")
            .setMessage("Digite o raio máximo em quilômetros:")
            .setView(view)
            .setPositiveButton("Aplicar") { dialog, _ ->
                val texto = editRaio.text?.toString()?.trim()
                val valor = texto?.toFloatOrNull()
                if (valor != null && valor > 0) {
                    modoBusca = "proximos"
                    raioMaximo = valor
                } else {
                    modoBusca = null
                    raioMaximo = null
                }
                atualizarTextoFiltros()
                realizarBusca()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Limpar") { dialog, _ ->
                modoBusca = null
                raioMaximo = null
                atualizarTextoFiltros()
                realizarBusca()
                dialog.dismiss()
            }
            .show()
    }

    private fun realizarBusca() {
        val usernameQuery = binding.editTextPesquisar.text.toString().trim().ifEmpty { null }

        binding.progressBar.visibility = View.VISIBLE
        binding.txtVazio.visibility = View.GONE

        lifecycleScope.launch {
            val resultado = repository.buscarPrestadores(
                username = usernameQuery,
                tipoServico = tipoServicoSelecionado,
                busca = modoBusca,
                raio = raioMaximo,
                inicio = 0,
                limite = 30
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
