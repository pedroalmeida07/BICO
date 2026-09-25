package com.example.bico.network

import com.example.bico.model.Cliente
import com.example.bico.model.Prestador
import retrofit2.Response
import retrofit2.http.*

interface BicoApiService {
    // Endpoints Cliente
    @POST("clientes")
    suspend fun cadastrarCliente(@Body cliente: Cliente): Response<Unit>

    @GET("clientes")
    suspend fun getDadosCliente(): Response<Cliente>

    @PUT("clientes")
    suspend fun atualizarCliente(@Body cliente: Cliente): Response<Unit>

    @DELETE("clientes")
    suspend fun deletarCliente(): Response<Unit>

    // Endpoints Prestador
    @POST("prestadores")
    suspend fun cadastrarPrestador(@Body prestador: Prestador): Response<Unit>

    @GET("prestadores")
    suspend fun getDadosPrestador(): Response<Prestador>

    @PUT("prestadores")
    suspend fun atualizarPrestador(@Body prestador: Prestador): Response<Unit>

    @DELETE("prestadores")
    suspend fun deletarPrestador(): Response<Unit>

    @GET("prestadores/lista")
    suspend fun buscarPrestadores(
        @Query("tipoServico") tipoServico: String? = null,
        @Query("inicio") inicio: Int? = null,
        @Query("fim") fim: Int? = null
    ): Response<List<Prestador>>
}
