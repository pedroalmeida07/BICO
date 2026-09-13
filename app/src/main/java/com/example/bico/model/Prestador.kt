package com.example.bico.model

import com.google.gson.annotations.SerializedName

data class Prestador(
    @SerializedName("id")
    override val id: String? = null,
    override val nome: String? = null,
    override val cpf: String? = null,
    override val telefone: String? = null,
    override val email: String? = null,
    override val senha: String? = null,
    @SerializedName("fotoPerfil")
    override val fotoPerfil: String? = null,

    // Exclusivo prestador
    @SerializedName("username")
    val usuario: String? = null,
    @SerializedName("fotoPaginaPerfil")
    val fotoHorizontalPrestador: String? = null,
    @SerializedName("fotosServicos")
    val fotosServico: List<String>? = null,
    @SerializedName("tiposServico")
    val servicos: List<String>? = null,
    @SerializedName("sobre")
    val descricao: String? = null,
    @SerializedName("localAtuacao")
    val local: String? = null
) : UserBase
