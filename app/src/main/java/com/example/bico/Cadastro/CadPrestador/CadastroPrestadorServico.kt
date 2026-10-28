package com.example.bico.Cadastro.CadPrestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorServicoBinding
import com.example.bico.utils.IbgeClient
import com.example.bico.utils.MaskWatcher
import com.example.bico.utils.NoAccentsAdapter
import com.example.bico.utils.ViaCepClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroPrestadorServico : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPrestadorServicoBinding
    private val listaCidadesFormatadas = mutableListOf<String>()
    private var listaServicos: Array<String> = emptyArray()
    private var jobBuscaCep: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding primeiro para evitar crashes
        binding = ActivityCadastroPrestadorServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )

        binding.editTextLocal.isEnabled = false

        setupAutoCompleteServicos()
        carregarCidadesIbge()
        setupCepListener()
        setupListeners()
    }

    private fun setupAutoCompleteServicos() {
        listaServicos = resources.getStringArray(R.array.servicos)
        val adapterServicos = NoAccentsAdapter(this, android.R.layout.simple_dropdown_item_1line, listaServicos.toList())
        binding.autoCompleteServico.apply {
            setAdapter(adapterServicos)
            threshold = 1 // Mostrar opções ao digitar a primeira letra
        }
    }

    private fun carregarCidadesIbge() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resposta = IbgeClient.apiService.getMunicipios()
                val nomes = resposta.map { it.nomeFormatado }
                withContext(Dispatchers.Main) {
                    listaCidadesFormatadas.clear()
                    listaCidadesFormatadas.addAll(nomes)
                    val adapterCidades = NoAccentsAdapter(this@CadastroPrestadorServico, android.R.layout.simple_dropdown_item_1line, listaCidadesFormatadas)
                    binding.editTextLocal.apply {
                        setAdapter(adapterCidades)
                        threshold = 1
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CadastroPrestadorServico, "Erro ao carregar cidades", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCepListener() {
        binding.editTextCEP.addTextChangedListener(MaskWatcher("#####-###", binding.editTextCEP))
        binding.editTextCEP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cepLimpo = MaskWatcher.unmask(s.toString())
                if (cepLimpo.length == 8) {
                    buscarLocalPorCep(cepLimpo)
                } else if (cepLimpo.length < 8) {
                    binding.editTextLocal.setText("")
                }
            }
        })
    }

    private fun buscarLocalPorCep(cep: String) {
        jobBuscaCep?.cancel()
        jobBuscaCep = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ViaCepClient.apiService.getEndereco(cep)
                withContext(Dispatchers.Main) {
                    if (response.erro == true || response.localidade.isNullOrEmpty()) {
                        Toast.makeText(this@CadastroPrestadorServico, "CEP não encontrado", Toast.LENGTH_SHORT).show()
                        binding.editTextLocal.setText("")
                        binding.editTextCEP.error = "CEP inválido ou não encontrado"
                    } else {
                        val cidadeFormatada = if (!response.uf.isNullOrEmpty()) {
                            "${response.localidade}, ${response.uf}"
                        } else {
                            response.localidade
                        }
                        binding.editTextLocal.setText(cidadeFormatada)
                        binding.editTextCEP.error = null
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CadastroPrestadorServico, "Erro ao buscar CEP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonAvancar.setOnClickListener {
            val tipo = binding.autoCompleteServico.text.toString().trim()
            val cep = binding.editTextCEP.text.toString().trim()
            val local = binding.editTextLocal.text.toString().trim()
            val cepLimpo = MaskWatcher.unmask(cep)

            // Validação 1: Campos vazios
            if (tipo.isEmpty() || cep.isEmpty() || local.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação 2: Verificar tamanho do CEP
            if (cepLimpo.length != 8) {
                binding.editTextCEP.error = "Digite um CEP válido"
                return@setOnClickListener
            }

            // Validação 3: Verificar se o serviço está na lista
            if (!listaServicos.contains(tipo)) {
                binding.autoCompleteServico.error = "Selecione um serviço válido da lista"
                return@setOnClickListener
            }

            // Salva e avança
            UserRepository.tempPrestador = UserRepository.tempPrestador.copy(
                servicos = listOf(tipo),
                local = local,
                cep = cep
            )
            startActivity(Intent(this, CadastroPrestadorEmail::class.java))
        }

        binding.sair.setOnClickListener { finish() }
    }
}
