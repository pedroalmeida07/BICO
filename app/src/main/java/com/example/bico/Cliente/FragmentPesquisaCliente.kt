package com.example.bico.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.FragmentPesquisaClienteBinding
import com.example.bico.utils.NoAccentsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentPesquisaCliente : Fragment() {

    private var _binding: FragmentPesquisaClienteBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: UserRepository
    private lateinit var adapter: PrestadorAdapter

    private var tipoServicoSelecionado: String? = null
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPesquisaClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = UserRepository(requireContext())

        setupRecyclerView()
        setupListeners()

        realizarBusca()
    }

    private fun setupRecyclerView() {
        adapter = PrestadorAdapter { prestador ->
            val intent = Intent(requireContext(), VerPrestadorCliente::class.java).apply {
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
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                realizarBusca()
            }
        }

        binding.btnFiltro.setOnClickListener {
            mostrarDropdownFiltro()
        }
    }

    private fun mostrarDropdownFiltro() {
        val view = layoutInflater.inflate(R.layout.dialog_filtrar_servico, null)
        val editBuscaServico = view.findViewById<TextInputEditText>(R.id.editBuscaServico)
        val listViewServicos = view.findViewById<ListView>(R.id.listViewServicos)

        val servicosArray = resources.getStringArray(R.array.servicos)
        val todosOpcao = "Todos os serviços (Limpar filtro)"
        val listaCompleta = listOf(todosOpcao) + servicosArray.toList()

        val adapterServicos = NoAccentsAdapter(requireContext(), android.R.layout.simple_list_item_1, listaCompleta)
        listViewServicos.adapter = adapterServicos

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Bico_MaterialAlertDialog)
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

        viewLifecycleOwner.lifecycleScope.launch {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
