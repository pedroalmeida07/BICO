package com.example.bico.model

import com.google.gson.annotations.SerializedName

interface UserBase {
    @get:SerializedName("id")
    val id: String?
    val nome: String?
    val cpf: String?
    val telefone: String?
    val email: String?
    val senha: String?
    @get:SerializedName("fotoPerfil")
    val fotoPerfil: String?

    val primeiroNome: String
        get() = (nome ?: "").trim().split(" ").firstOrNull() ?: ""
}
