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

        fun resetTempUser() {
            tempUser = User()
        }
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

    // Em UserRepository.kt


    //valida o email e senha e salva o email do usuário logado
    fun realizarLogin(email: String, senha: String): User? {val user = listarUsuarios().find { it.email == email && it.senha == senha }
        if (user != null) {
            // Salva o e-mail do usuário logado
            val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().putString("email_logado", email).apply()
        }
        return user
    }

    //retorna o usuário logado
    fun getUsuarioLogado(): User? {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email_logado", null)
        return listarUsuarios().find { it.email == email }
    }

    // Atualiza a senha de um usuário pelo e-mail
    fun atualizarSenha(email: String, novaSenha: String): Boolean {
        val lista = listarUsuarios().toMutableList()
        val index = lista.indexOfFirst { it.email == email }

        if (index != -1) {
            val userAtualizado = lista[index].copy(senha = novaSenha)
            lista[index] = userAtualizado
            getFile().writeText(gson.toJson(lista))
            return true
        }
        return false
    }
}