package com.example.bico

import android.content.Context
import com.example.bico.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class UserRepository(private val context: Context) {
    private val fileName = "usuarios.json"
    private val gson = Gson()

    // Dados temporários para o fluxo de telas de cadastro
    companion object {
        var tempUser = User()
    }

    private fun getFile(): File = File(context.filesDir, fileName)

    fun salvarUsuario(user: User) {
        val lista = listarUsuarios().toMutableList()
        lista.add(user)
        getFile().writeText(gson.toJson(lista))
    }

    fun listarUsuarios(): List<User> {
        val file = getFile()
        if (!file.exists()) return emptyList()
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(file.readText(), type)
    }

    fun realizarLogin(email: String, senha: String): User? {
        return listarUsuarios().find { it.email == email && it.senha == senha }
    }
}