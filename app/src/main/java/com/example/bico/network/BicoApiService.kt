package com.example.bico.network

import com.example.bico.model.User
import retrofit2.Response
import retrofit2.http.*

interface BicoApiService {
    @POST("cliente")
    suspend fun cadastrarCliente(@Body user: User): Response<Unit>

    @POST("prestador")
    suspend fun cadastrarPrestador(@Body user: User): Response<Unit>

    @GET("usuario")
    suspend fun DadosUsuario(@Query("id") id: String): Response<User>

    @GET("prestadores")
    suspend fun buscarPrestadores(
        @Query("tipos") tipos: String? = null,
        @Query("inicio") inicio: Int? = null,
        @Query("fim") fim: Int? = null
    ): Response<List<User>>

    @PUT ("usuario")
    suspend fun atualizarUsuario(@Query("id") id: String, @Body user: User): Response<Unit>

    @DELETE ("usuario")
    suspend fun deletarUsuario(@Query("id") id: String): Response<Unit>
}