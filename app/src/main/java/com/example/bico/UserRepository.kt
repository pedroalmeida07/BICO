package com.example.bico

import android.content.Context
import com.example.bico.model.User
import com.example.bico.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val api = RetrofitClient.service

    // Dados temporários para o fluxo de telas de cadastro
    companion object {
        var tempUser = User()

        fun resetTempUser() {
            tempUser = User()
        }
    }

    suspend fun salvarUsuario(user: User): Boolean {
        return try {
            val response = if (user.tipo == "PRESTADOR") {
                api.cadastrarPrestador(user)
            } else {
                api.cadastrarCliente(user)
            }
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Valida o email e senha usando Firebase Auth e busca os dados no Backend
    suspend fun realizarLogin(email: String, senha: String): User? {
        return try {
            // 1. Tenta autenticar no Firebase
            auth.signInWithEmailAndPassword(email, senha).await()
            
            // 2. Busca os dados complementares no nosso backend em Go
            val response = api.getDadosUsuario(email)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    saveLoggedEmail(email)
                }
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Retorna o usuário logado buscando no backend
    suspend fun getUsuarioLogado(): User? {
        val email = getLoggedEmail() ?: return null
        return try {
            val response = api.getDadosUsuario(email)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    // Atualiza os dados de um usuário no backend
    suspend fun atualizarUsuario(user: User): Boolean {
        return try {
            val response = api.atualizarUsuario(user)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Atualiza a senha usando Firebase
    suspend fun atualizarSenha(email: String, novaSenha: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user?.email == email) {
                user.updatePassword(novaSenha).await()
                true
            } else {
                // Caso o usuário não esteja logado, envia email de recuperação
                auth.sendPasswordResetEmail(email).await()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun saveLoggedEmail(email: String) {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("email_logado", email).apply()
    }

    private fun getLoggedEmail(): String? {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("email_logado", null)
    }

    fun deslogar() {
        auth.signOut()
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("email_logado").apply()
    }
}