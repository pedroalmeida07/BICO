package com.example.bico

import android.content.Context
import android.util.Log
import com.example.bico.model.Cliente
import com.example.bico.model.Prestador
import com.example.bico.model.UserBase
import com.example.bico.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val api = RetrofitClient.service
    private val gson = Gson()

    companion object {
        var tempCliente = Cliente()
        var tempPrestador = Prestador()

        fun resetTempUser() {
            tempCliente = Cliente()
            tempPrestador = Prestador()
        }
    }

    suspend fun salvarPrestador(prestador: Prestador): Boolean {
        return try {
            val response = api.cadastrarPrestador(prestador)
            if (response.isSuccessful) {
                true
            } else {
                Log.e("UserRepository", "Erro no Backend: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Falha na requisição: ${e.message}", e)
            false
        }
    }

    suspend fun salvarCliente(cliente: Cliente): Boolean {
        return try {
            val response = api.cadastrarCliente(cliente)
            if (response.isSuccessful) {
                true
            } else {
                Log.e("UserRepository", "Erro no Backend: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Falha na requisição: ${e.message}", e)
            false
        }
    }

    suspend fun realizarLogin(email: String, senha: String): UserBase? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, senha).await()
            val uid = authResult.user?.uid ?: return null

            Log.d("UserRepository", "Firebase Auth OK. Buscando dados no backend para UID: $uid")

            // Tenta buscar como Cliente primeiro
            val resCliente = api.getDadosCliente()
            if (resCliente.isSuccessful) {
                val cliente = resCliente.body()?.copy(id = uid)
                cliente?.let {
                    saveLoggedEmail(email)
                    saveLocalUser(it, "cliente")
                    return it
                }
            }

            // Se não for cliente, tenta Prestador
            val resPrestador = api.getDadosPrestador()
            if (resPrestador.isSuccessful) {
                val prestador = resPrestador.body()?.copy(id = uid)
                prestador?.let {
                    saveLoggedEmail(email)
                    saveLocalUser(it, "prestador")
                    return it
                }
            }

            null
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro no processo de login: ${e.message}", e)
            null
        }
    }

    suspend fun usuarioLogado(): UserBase? {
        val uid = auth.currentUser?.uid ?: return null

        val localUser = getLocalUser()
        if (localUser != null && localUser.id == uid) {
            return localUser
        }

        return try {
            // Tenta buscar como Cliente
            val resCliente = api.getDadosCliente()
            if (resCliente.isSuccessful) {
                val cliente = resCliente.body()?.copy(id = uid)
                cliente?.let { saveLocalUser(it, "cliente") }
                return cliente
            }

            // Tenta buscar como Prestador
            val resPrestador = api.getDadosPrestador()
            if (resPrestador.isSuccessful) {
                val prestador = resPrestador.body()?.copy(id = uid)
                prestador?.let { saveLocalUser(it, "prestador") }
                return prestador
            }
            null
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao buscar usuário logado: ${e.message}", e)
            localUser
        }
    }

    suspend fun atualizarCliente(cliente: Cliente): Boolean {
        return try {
            val response = api.atualizarCliente(cliente)
            if (response.isSuccessful) {
                saveLocalUser(cliente, "cliente")
                true
            } else false
        } catch (e: Exception) { false }
    }

    suspend fun atualizarCliente(id: String, cliente: Cliente): Boolean = atualizarCliente(cliente)

    suspend fun atualizarPrestador(prestador: Prestador): Boolean {
        return try {
            val response = api.atualizarPrestador(prestador)
            if (response.isSuccessful) {
                saveLocalUser(prestador, "prestador")
                true
            } else false
        } catch (e: Exception) { false }
    }

    suspend fun atualizarPrestador(id: String, prestador: Prestador): Boolean = atualizarPrestador(prestador)

    suspend fun deletarUsuario(id: String? = null): Boolean {
        return try {
            // Tenta deletar como cliente primeiro
            var response = api.deletarCliente()
            if (!response.isSuccessful) {
                // Se falhou, tenta como prestador
                response = api.deletarPrestador()
            }

            if (response.isSuccessful) {
                deslogar()
                true
            } else false
        } catch (e: Exception) { false }
    }

    suspend fun buscarPrestadores(
        tipoServico: String? = null,
        inicio: Int? = null,
        fim: Int? = null,
        tipos: String? = null
    ): List<Prestador>? {
        val filtro = tipoServico ?: tipos
        return try {
            val response = api.buscarPrestadores(filtro, inicio, fim)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun atualizarSenha(email: String, novaSenha: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user?.email == email) {
                user.updatePassword(novaSenha).await()
                true
            } else {
                auth.sendPasswordResetEmail(email).await()
                true
            }
        } catch (e: Exception) { false }
    }

    private fun saveLoggedEmail(email: String) {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("email_logado", email).apply()
    }

    private fun saveLocalUser(user: UserBase, type: String) {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putString("usuario_logado", gson.toJson(user))
            .putString("usuario_tipo", type)
            .apply()
    }

    private fun getLocalUser(): UserBase? {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        val userJson = sharedPref.getString("usuario_logado", null)
        val userType = sharedPref.getString("usuario_tipo", null)

        return if (userJson != null) {
            try {
                if (userType == "cliente") {
                    gson.fromJson(userJson, Cliente::class.java)
                } else {
                    gson.fromJson(userJson, Prestador::class.java)
                }
            } catch (e: Exception) { null }
        } else null
    }

    fun deslogar() {
        auth.signOut()
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("email_logado").remove("usuario_logado").remove("usuario_tipo").apply()
    }
}
