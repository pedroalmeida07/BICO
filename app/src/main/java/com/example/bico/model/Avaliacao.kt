package com.example.bico.model

data class Avaliacao(
    val prestadorID: String,
    val nota: Int,
    val comentario: String? = null
)

data class AvaliacaoResponse(
    val status: String? = null
)
