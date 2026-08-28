package com.example.bico.Cadastro.CadPrestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorServicoBinding
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.widget.Filter
import java.text.Normalizer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


// Modelos de dados para desserializar o JSON do IBGE
data class MunicipioResponse(
    val nome: String,
    val microrregiao: MicrorregiaoResponse?
) {
    data class MicrorregiaoResponse(
        val mesorregiao: MesorregiaoResponse?
    )
    data class MesorregiaoResponse(
        @SerializedName("UF") val uf: UfResponse?
    )
    data class UfResponse(
        val sigla: String
    )

    // Formata o resultado para "Cidade, UF" (ex: "Vitória, ES")
    val nomeFormatado: String
        get() {
            val siglaUf = microrregiao?.mesorregiao?.uf?.sigla
            return if (!siglaUf.isNullOrEmpty()) "$nome, $siglaUf" else nome
        }
}

// Interface Retrofit para consumo do IBGE
interface IbgeApiService {
    @GET("v1/localidades/municipios?orderBy=nome")
    suspend fun getMunicipios(): List<MunicipioResponse>
}

// Objeto singleton para instanciar a API do IBGE
object IbgeClient {
    private const val BASE_URL = "https://servicodados.ibge.gov.br/api/"

    val apiService: IbgeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IbgeApiService::class.java)
    }
}

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
            UserRepository.tempUser = UserRepository.tempUser.copy(
                servicos = listOf(tipo),
                local = local
            )
            startActivity(Intent(this, CadastroPrestadorEmail::class.java))
        }

        binding.sair.setOnClickListener { finish() }
    }
}

// Função utilitária para remover acentos e converter para minúsculas
fun String.removerAcentos(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return temp.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
}

// Adapter customizado para ignorar acentos durante o filtro
class NoAccentsAdapter(context: Context, resource: Int, private val allItems: List<String>) :
    ArrayAdapter<String>(context, resource, allItems) {

    private var itemsFiltrados: List<String> = allItems

    override fun getCount(): Int = itemsFiltrados.size
    override fun getItem(position: Int): String? = itemsFiltrados[position]

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val busca = constraint?.toString()?.removerAcentos() ?: ""
                val listaResultados = if (busca.isEmpty()) {
                    allItems
                } else {
                    allItems.filter { it.removerAcentos().contains(busca) }
                }

                return FilterResults().apply {
                    values = listaResultados
                    count = listaResultados.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltrados = results?.values as? List<String> ?: allItems
                if (results?.count ?: 0 > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
