package com.example.bico.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.Normalizer

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
