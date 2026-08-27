package com.example.bico.Cadastro.Prestador

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

        binding = ActivityCadastroPrestadorServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAutoCompleteServicos()
        carregarCidadesIbge()
        setupListeners()
    }

    private fun setupAutoCompleteServicos() {
        val servicos = resources.getStringArray(R.array.servicos)
        val adapterServicos = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            servicos
        )

        binding.autoCompleteServico.apply {
            setAdapter(adapterServicos)
            threshold = 3 // Exibe sugestões a partir do 3º caractere
        }
    }

    private fun carregarCidadesIbge() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resposta = IbgeClient.apiService.getMunicipios()
                val nomesFormatados = resposta.map { it.nomeFormatado }

                withContext(Dispatchers.Main) {
                    listaCidadesFormatadas.clear()
                    listaCidadesFormatadas.addAll(nomesFormatados)

                    val adapterCidades = ArrayAdapter(
                        this@CadastroPrestadorServico,
                        android.R.layout.simple_dropdown_item_1line,
                        listaCidadesFormatadas
                    )

                    // Certifique-se de que editTextLocal seja um AutoCompleteTextView no layout XML
                    binding.editTextLocal.apply {
                        setAdapter(adapterCidades)
                        threshold = 3 // Exibe sugestões a partir do 3º caractere
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupListeners() {
        binding.buttonAvancar.setOnClickListener {
            val tipo = binding.autoCompleteServico.text.toString().trim()
            val local = binding.editTextLocal.text.toString().trim()

            if (tipo.isEmpty() || local.isEmpty()) {
                Toast.makeText(
                    this,
                    "Preencha o serviço e a localização",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Salva os dados no UserRepository temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                servicos = listOf(tipo),
                local = local
            )

            val intent = Intent(this, CadastroPrestadorEmail::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}