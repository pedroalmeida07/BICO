package com.example.bico.network

import com.example.bico.model.User
import retrofit2.Response
import retrofit2.http.*

interface BicoApiService {
    @POST("CadastrarCliente")
    suspend fun cadastrarCliente(@Body user: User): Response<Unit>

    @POST("CadastrarPrestador")
    suspend fun cadastrarPrestador(@Body user: User): Response<Unit>

    @GET("DadosUsuario")
    suspend fun getDadosUsuario(@Query("id") id: String): Response<User>

    @POST("AtualizarUsuario")
    suspend fun atualizarUsuario(@Body user: User): Response<Unit>
}