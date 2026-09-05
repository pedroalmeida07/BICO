package com.example.bico.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class User(
    //padrao ao prestador e cliente
    val id: String = UUID.randomUUID().toString(),
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val senha: String = "",

    //exclusivo cliente
    val cep: String = "",
    val numero: String = "",
    val complemento: String = "",

    //exclusivo prestador
    @SerializedName("username")
    val usuario: String = "",
    @SerializedName("fotoPerfil")
    val fotoPerfil: String = "",
    @SerializedName("fotoPaginaPerfil")
    val fotoHorizontalPrestador: String? = null,
    @SerializedName("fotosServicos")
    val fotosServico: List<String> = emptyList(),
    @SerializedName("tiposServico")
    val servicos: List<String> = emptyList(),
    @SerializedName("sobre")
    val descricao: String = "",
    @SerializedName("localAtuacao")
    val local: String = "",
) {
    val primeiroNome: String
        get() = nome.trim().split(" ").firstOrNull() ?: ""
}