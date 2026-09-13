package com.example.bico.network

import com.example.bico.model.Cliente
import com.example.bico.model.Prestador
import retrofit2.Response
import retrofit2.http.*

interface BicoApiService {
    // Endpoints Cliente
    @POST("cliente")
    suspend fun cadastrarCliente(@Body cliente: Cliente): Response<Unit>

    @GET("cliente")
    suspend fun getDadosCliente(@Query("id") id: String): Response<Cliente>

    @PUT("cliente")
    suspend fun atualizarCliente(@Query("id") id: String, @Body cliente: Cliente): Response<Unit>

    @DELETE("cliente")
    suspend fun deletarCliente(@Query("id") id: String): Response<Unit>

    // Endpoints Prestador
    @POST("prestador")
    suspend fun cadastrarPrestador(@Body prestador: Prestador): Response<Unit>

    @GET("prestador")
    suspend fun getDadosPrestador(@Query("id") id: String): Response<Prestador>

    @PUT("prestador")
    suspend fun atualizarPrestador(@Query("id") id: String, @Body prestador: Prestador): Response<Unit>

    @DELETE("prestador")
    suspend fun deletarPrestador(@Query("id") id: String): Response<Unit>

    @GET("prestadores")
    suspend fun buscarPrestadores(
        @Query("tipos") tipos: String? = null,
        @Query("inicio") inicio: Int? = null,
        @Query("fim") fim: Int? = null
    ): Response<List<Prestador>>
}
