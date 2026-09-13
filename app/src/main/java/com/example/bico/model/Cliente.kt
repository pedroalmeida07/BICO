package com.example.bico.model

import com.google.gson.annotations.SerializedName

data class Cliente(
    @SerializedName("id")
    override val id: String? = null,
    override val nome: String? = null,
    override val cpf: String? = null,
    override val telefone: String? = null,
    override val email: String? = null,
    override val senha: String? = null,
    @SerializedName("fotoPerfil")
    override val fotoPerfil: String? = null,

    // Exclusivo cliente
    val cep: String? = null,
    val numero: String? = null,
    val complemento: String? = null,
    
    // Campo formatado usado em algumas telas
    val local: String? = null
) : UserBase
