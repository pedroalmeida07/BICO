package com.example.bico.Cadastro.CadPrestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorServicoBinding
import com.example.bico.utils.IbgeClient
import com.example.bico.utils.NoAccentsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroPrestadorServico : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPrestadorServicoBinding
    private val listaCidadesFormatadas = mutableListOf<String>()
    private var listaServicos: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding primeiro para evitar crashes
        binding = ActivityCadastroPrestadorServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )

        setupAutoCompleteServicos()
        carregarCidadesIbge()
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

    private fun setupListeners() {
        binding.buttonAvancar.setOnClickListener {
            val tipo = binding.autoCompleteServico.text.toString().trim()
            val local = binding.editTextLocal.text.toString().trim()

            // Validação 1: Campos vazios
            if (tipo.isEmpty() || local.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação 2: Verificar se o serviço está na lista
            if (!listaServicos.contains(tipo)) {
                binding.autoCompleteServico.error = "Selecione um serviço válido da lista"
                return@setOnClickListener
            }

            // Validação 3: Verificar se a cidade está na lista carregada do IBGE
            if (listaCidadesFormatadas.isNotEmpty() && !listaCidadesFormatadas.contains(local)) {
                binding.editTextLocal.error = "Selecione uma cidade válida da lista"
                return@setOnClickListener
            }

            // Se as cidades ainda estiverem carregando e o usuário digitar algo
            if (listaCidadesFormatadas.isEmpty()) {
                Toast.makeText(this, "Aguarde o carregamento das cidades...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva e avança
            UserRepository.tempPrestador = UserRepository.tempPrestador.copy(
                servicos = listOf(tipo),
                local = local
            )
            startActivity(Intent(this, com.example.bico.Cadastro.CadPrestador.CadastroPrestadorEmail::class.java))
        }

        binding.sair.setOnClickListener { finish() }
    }
}
