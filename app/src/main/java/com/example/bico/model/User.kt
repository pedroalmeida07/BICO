package com.example.bico.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class User(
    //padrao ao prestador e cliente
    @SerializedName("id")
    val id: String? = null,
    val nome: String? = null,
    val cpf: String? = null,
    val telefone: String? = null,
    val email: String? = null,
    val senha: String? = null,

    //exclusivo cliente
    val cep: String? = null,
    val numero: String? = null,
    val complemento: String? = null,

    //exclusivo prestador
    @SerializedName("username")
    val usuario: String? = null,
    @SerializedName("fotoPerfil")
    val fotoPerfil: String? = null,
    @SerializedName("fotoPaginaPerfil")
    val fotoHorizontalPrestador: String? = null,
    @SerializedName("fotosServicos")
    val fotosServico: List<String>? = null,
    @SerializedName("tiposServico")
    val servicos: List<String>? = null,
    @SerializedName("sobre")
    val descricao: String? = null,
    @SerializedName("localAtuacao")
    val local: String? = null,
) {
    val primeiroNome: String
        get() = (nome ?: "").trim().split(" ").firstOrNull() ?: ""
}