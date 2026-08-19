package com.example.bico.model

data class User(
    val id: String = "",
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val senha: String = "",
    val usuario: String = "",
    val servico: String = "",
    val local: String = "",
    val tipo: String = "" // "CLIENTE" ou "PRESTADOR"
)