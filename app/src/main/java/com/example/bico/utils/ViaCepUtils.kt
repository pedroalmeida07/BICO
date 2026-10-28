package com.example.bico.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class ViaCepResponse(
    val cep: String? = null,
    val logradouro: String? = null,
    val complemento: String? = null,
    val bairro: String? = null,
    val localidade: String? = null,
    val uf: String? = null,
    val erro: Boolean? = null
)

interface ViaCepApiService {
    @GET("{cep}/json/")
    suspend fun getEndereco(@Path("cep") cep: String): ViaCepResponse
}

object ViaCepClient {
    private const val BASE_URL = "https://viacep.com.br/ws/"

    val apiService: ViaCepApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApiService::class.java)
    }
}
