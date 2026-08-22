package com.example.bico.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val senha: String = "",
    val usuario: String = "",
    val servico: String = "",
    val local: String = "",
    val cep: String = "",
    val numero: String = "",
    val complemento: String = "",
    val tipo: String = "" // "CLIENTE" ou "PRESTADOR"
) {
    val primeiroNome: String
        get() = nome.trim().split(" ").firstOrNull() ?: ""
}