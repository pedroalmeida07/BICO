package com.example.bico.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val senha: String = "",
    
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
    
    val cep: String = "",
    val numero: String = "",
    val complemento: String = "",
    val tipo: String = "" // "CLIENTE" ou "PRESTADOR"
) {
    val primeiroNome: String
        get() = nome.trim().split(" ").firstOrNull() ?: ""
}