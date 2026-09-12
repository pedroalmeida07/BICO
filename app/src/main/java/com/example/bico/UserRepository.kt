package com.example.bico

import android.content.Context
import android.util.Log
import com.example.bico.model.User
import com.example.bico.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val api = RetrofitClient.service
    private val gson = Gson()

    // Dados temporários para o fluxo de telas de cadastro
    companion object {
        var tempUser = User()

        fun resetTempUser() {
            tempUser = User()
        }
    }

    suspend fun salvarPrestador(user: User): Boolean {
        return try {
            val response = api.cadastrarPrestador(user)

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

    suspend fun salvarCliente(user: User): Boolean {
        return try {
            val response = api.cadastrarCliente(user)

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

    // Valida o email e senha usando Firebase Auth e busca os dados no Backend
    suspend fun realizarLogin(email: String, senha: String): User? {
        return try {
            // 1. Tenta autenticar no Firebase
            val authResult = auth.signInWithEmailAndPassword(email, senha).await()
            val uid = authResult.user?.uid
            
            if (uid == null) {
                Log.e("UserRepository", "Firebase Auth: UID nulo após login")
                return null
            }

            // 2. Busca os dados complementares no nosso backend em Go
            Log.d("UserRepository", "Firebase Auth OK. Buscando dados no backend para UID: $uid")
            val response = api.DadosUsuario(uid)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Log.d("UserRepository", "Backend OK. Usuário retornado: ${user.nome}, tipo: ${if(user.usuario.isNullOrEmpty()) "Cliente" else "Prestador"}")
                    val userComId = user.copy(id = uid)
                    saveLoggedEmail(email)
                    saveLocalUser(userComId)
                    userComId
                } else {
                    Log.e("UserRepository", "Backend retornou corpo vazio para UID: $uid")
                    null
                }
            } else {
                Log.e("UserRepository", "Erro no Backend: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro no processo de login para o email $email: ${e.message}", e)
            null
        }
    }

    // Retorna o usuário logado buscando no backend (com cache local)
    suspend fun usuarioLogado(): User? {
        val uid = auth.currentUser?.uid ?: return null
        
        // Tenta retornar o cache local primeiro para rapidez
        val localUser = getLocalUser()
        if (localUser != null && localUser.id == uid) {
            return localUser
        }

        return try {
            val response = api.DadosUsuario(uid)
            if (response.isSuccessful) {
                val user = response.body()?.copy(id = uid)
                user?.let { saveLocalUser(it) }
                user
            } else {
                localUser // Retorna o local mesmo se o servidor falhar
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao buscar usuário logado: ${e.message}", e)
            localUser
        }
    }

    // Atualiza os dados de um usuário no backend
    suspend fun atualizarUsuario(id: String, user: User): Boolean {
        Log.d("UserRepository", "Enviando atualização para o ID (Query e Body): $id")
        return try {
            val response = api.atualizarUsuario(id, user)
            if (response.isSuccessful) {
                saveLocalUser(user) // Atualiza o cache local após sucesso
                true
            } else {
                Log.e("UserRepository", "Erro ao atualizar: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Falha na atualização: ${e.message}", e)
            false
        }
    }

    suspend fun deletarUsuario(id: String): Boolean {
        try {
            val response = api.deletarUsuario(id)
            if (response.isSuccessful) {
                deslogar()
                return true
            } else {
                Log.e("UserRepository", "Erro ao deletar usuario de ID: $id")
                return false
            }
        }
        catch (e: Exception) {
            Log.e("UserRepository", "Falha na requisição de deleção: ${e.message}")
            return false
        }
    }

    suspend fun buscarPrestadores(tipos: String? = null, inicio: Int? = null, fim: Int? = null): List<User>? {
        return try {
            val response = api.buscarPrestadores(tipos, inicio, fim)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepository", "Erro ao buscar prestadores: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Falha na requisição de prestadores: ${e.message}")
            null
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

    private fun saveLocalUser(user: User) {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("usuario_logado", gson.toJson(user)).apply()
    }

    private fun getLocalUser(): User? {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        val userJson = sharedPref.getString("usuario_logado", null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    private fun getLoggedEmail(): String? {
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("email_logado", null)
    }

    fun deslogar() {
        auth.signOut()
        val sharedPref = context.getSharedPreferences("bico_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("email_logado").remove("usuario_logado").apply()
    }
}