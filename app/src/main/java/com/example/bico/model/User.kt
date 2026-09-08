package com.example.bico.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class User(
    //padrao ao prestador e cliente
    @SerializedName("id")
    val id: String? = "",
    val nome: String? = "",
    val cpf: String? = "",
    val telefone: String? = "",
    val email: String? = "",
    val senha: String? = "",

    //exclusivo cliente
    val cep: String? = "",
    val numero: String? = "",
    val complemento: String? = "",

    //exclusivo prestador
    @SerializedName("username")
    val usuario: String? = "",
    @SerializedName("fotoPerfil")
    val fotoPerfil: String? = null,
    @SerializedName("fotoPaginaPerfil")
    val fotoHorizontalPrestador: String? = null,
    @SerializedName("fotosServicos")
    val fotosServico: List<String>? = emptyList(),
    @SerializedName("tiposServico")
    val servicos: List<String>? = emptyList(),
    @SerializedName("sobre")
    val descricao: String? = "",
    @SerializedName("localAtuacao")
    val local: String? = "",
) {
    val primeiroNome: String
        get() = (nome ?: "").trim().split(" ").firstOrNull() ?: ""
}