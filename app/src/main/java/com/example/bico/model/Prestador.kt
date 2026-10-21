package com.example.bico.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Prestador(
    @SerializedName(value = "id", alternate = ["_id"])
    override val id: String? = null,
    override val nome: String? = null,
    override val cpf: String? = null,
    override val email: String? = null,
    override val senha: String? = null,
    @SerializedName(value = "fotoPerfil", alternate = ["foto", "avatar"])
    override val fotoPerfil: String? = null,

    // Exclusivo prestador
    @SerializedName(value = "username", alternate = ["usuario", "user"])
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
    val local: String? = null,
    @SerializedName("notaMedia")
    val notaMedia: Double? = null,
    @SerializedName("totalAvaliacoes")
    val totalAvaliacoes: Int? = null
) : UserBase, Serializable {
    val favoriteKey: String
        get() = id?.takeIf { it.isNotEmpty() }
            ?: usuario?.takeIf { it.isNotEmpty() }
            ?: email?.takeIf { it.isNotEmpty() }
            ?: nome ?: ""
}
