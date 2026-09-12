package com.example.bico.network

import com.example.bico.model.User
import retrofit2.Response
import retrofit2.http.*

interface BicoApiService {
    @POST("Cliente")
    suspend fun cadastrarCliente(@Body user: User): Response<Unit>

    @POST("Prestador")
    suspend fun cadastrarPrestador(@Body user: User): Response<Unit>

    @GET("Usuario")
    suspend fun DadosUsuario(@Query("id") id: String): Response<User>

    @PUT ("Usuario")
    suspend fun atualizarUsuario(@Query("id") id: String, @Body user: User): Response<Unit>

    @DELETE ("Usuario")
    suspend fun deletarUsuario(@Query("id") id: String): Response<Unit>
}