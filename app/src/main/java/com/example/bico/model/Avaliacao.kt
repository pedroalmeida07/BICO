package com.example.bico.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Avaliacao(
    @SerializedName("prestadorID")
    val prestadorID: String? = null,
    @SerializedName("nota")
    val nota: Int = 0,
    @SerializedName("comentario")
    val comentario: String? = null,
    @SerializedName(value = "nomeAvaliador", alternate = ["nomeCliente", "avaliador", "nome", "clienteNome", "cliente"])
    val nomeAvaliador: String? = null
) : Serializable

data class AvaliacaoResponse(
    val status: String? = null
)
