package com.example.bico

import android.content.Context
import android.util.Log
import com.example.bico.model.Cliente
import com.example.bico.model.Prestador
import com.example.bico.model.UserBase
import com.example.bico.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
            } else{
                Log.e("UserRepository", "Erro em atualizarCliente: ${response.code()} - ${response.errorBody()?.string()}")
                false}
        } catch (e: Exception) {
            Log.e("UserRepository", "Exceção em atualizarCliente: ${e.message}", e)
            false
        }
    }

    suspend fun atualizarEmailCliente(emailNovo: String): Boolean {
        return try {
            val response = api.atualizarEmailCliente(emailNovo)
            if (response.isSuccessful) {
                deslogar()
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

    suspend fun atualizarEmailPrestador(emailNovo: String): Boolean {
        return try {
            val response = api.atualizarEmailPrestador(emailNovo)
            if (response.isSuccessful) {
                deslogar()
                true
            } else false
        } catch (e: Exception) { false }
    }

    suspend fun deletarCliente(): Boolean {
        return try {
            val response = api.deletarCliente()
            if (response.isSuccessful) {
                deslogar()
                true
            } else {
                Log.e("UserRepository", "Erro ao deletar cliente: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exceção ao deletar cliente: ${e.message}", e)
            false
        }
    }

    suspend fun deletarPrestador(): Boolean {
        return try {
            val response = api.deletarPrestador()
            if (response.isSuccessful) {
                deslogar()
                true
            } else {
                Log.e("UserRepository", "Erro ao deletar prestador: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exceção ao deletar prestador: ${e.message}", e)
            false
        }
    }

    suspend fun deletarUsuario(id: String? = null): Boolean {
        return if (deletarCliente()) {
            true
        } else {
            deletarPrestador()
        }
    }

    suspend fun buscarPrestadores(
        username: String? = null,
        tipoServico: String? = null,
        inicio: Int? = null,
        limite: Int? = null,
        fim: Int? = null,
        tipos: String? = null
    ): List<Prestador>? {
        val filtro = tipoServico ?: tipos
        val l = limite ?: fim
        return try {
            val response = api.buscarPrestadores(username, filtro, inicio, l)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    private val firestore = FirebaseFirestore.getInstance()

    private fun getPrefFavoritos(): String {
        val uid = auth.currentUser?.uid ?: "default"
        return "bico_favoritos_cache_$uid"
    }

    suspend fun adicionarFavorito(prestador: Prestador): Boolean {
        val idPrestador = prestador.id?.takeIf { it.isNotEmpty() } 
            ?: prestador.usuario?.takeIf { it.isNotEmpty() } 
            ?: prestador.email ?: ""
        
        // 1. Salvar localmente imediatamente
        adicionarFavoritoLocal(prestador)

        // 2. Salvar no Firebase Firestore (nuvem - sobrevive a wipe data)
        val uid = auth.currentUser?.uid
        if (uid != null && idPrestador.isNotEmpty()) {
            try {
                firestore.collection("usuarios").document(uid)
                    .collection("favoritos").document(idPrestador)
                    .set(prestador).await()
            } catch (e: Exception) {
                Log.e("UserRepository", "Erro ao salvar favorito no Firestore: ${e.message}", e)
            }
        }

        // 3. Enviar para o servidor backend
        return try {
            val response = api.adicionarFavorito(idPrestador)
            Log.d("UserRepository", "adicionarFavorito resp: ${response.code()} - ${response.errorBody()?.string()}")
            response.isSuccessful || true
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao adicionar favorito (rede): ${e.message}", e)
            true
        }
    }

    suspend fun adicionarFavorito(idPrestador: String): Boolean {
        val prestador = Prestador(id = idPrestador, usuario = idPrestador, nome = idPrestador)
        return adicionarFavorito(prestador)
    }

    suspend fun removerFavorito(prestador: Prestador): Boolean {
        val idPrestador = prestador.id?.takeIf { it.isNotEmpty() } 
            ?: prestador.usuario?.takeIf { it.isNotEmpty() } 
            ?: prestador.email ?: ""

        // 1. Remover localmente imediatamente
        removerFavoritoLocal(prestador)

        // 2. Remover do Firebase Firestore
        val uid = auth.currentUser?.uid
        if (uid != null && idPrestador.isNotEmpty()) {
            try {
                firestore.collection("usuarios").document(uid)
                    .collection("favoritos").document(idPrestador)
                    .delete().await()
            } catch (e: Exception) {
                Log.e("UserRepository", "Erro ao remover favorito do Firestore: ${e.message}", e)
            }
        }

        // 3. Enviar para o servidor backend
        return try {
            val response = api.removerFavorito(idPrestador)
            Log.d("UserRepository", "removerFavorito resp: ${response.code()} - ${response.errorBody()?.string()}")
            response.isSuccessful || true
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao remover favorito (rede): ${e.message}", e)
            true
        }
    }

    suspend fun removerFavorito(idPrestador: String): Boolean {
        val prestador = Prestador(id = idPrestador, usuario = idPrestador, nome = idPrestador)
        return removerFavorito(prestador)
    }

    private fun adicionarFavoritoLocal(prestador: Prestador) {
        val prefs = context.getSharedPreferences(getPrefFavoritos(), Context.MODE_PRIVATE)
        val json = prefs.getString("json_favoritos", null)
        val lista = mutableListFromCache(json)
        
        val existe = lista.any { 
            (prestador.id != null && it.id == prestador.id) || 
            (!prestador.usuario.isNullOrEmpty() && it.usuario.equals(prestador.usuario, ignoreCase = true)) ||
            (!prestador.email.isNullOrEmpty() && it.email.equals(prestador.email, ignoreCase = true))
        }

        if (!existe) {
            lista.add(prestador)
            val novoJson = gson.toJson(lista)
            prefs.edit().putString("json_favoritos", novoJson).apply()
        }
    }

    private fun removerFavoritoLocal(prestador: Prestador) {
        val prefs = context.getSharedPreferences(getPrefFavoritos(), Context.MODE_PRIVATE)
        val json = prefs.getString("json_favoritos", null)
        val lista = mutableListFromCache(json)

        lista.removeAll { 
            (prestador.id != null && it.id == prestador.id) || 
            (!prestador.usuario.isNullOrEmpty() && it.usuario.equals(prestador.usuario, ignoreCase = true)) ||
            (!prestador.email.isNullOrEmpty() && it.email.equals(prestador.email, ignoreCase = true)) ||
            it.id == prestador.usuario || it.usuario == prestador.id
        }

        val novoJson = gson.toJson(lista)
        prefs.edit().putString("json_favoritos", novoJson).apply()
    }

    private fun mutableListFromCache(json: String?): MutableList<Prestador> {
        if (json.isNullOrEmpty()) return mutableListOf()
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<Prestador>>() {}.type
            gson.fromJson<List<Prestador>>(json, type)?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    suspend fun getFavoritos(): List<Prestador> {
        val prefs = context.getSharedPreferences(getPrefFavoritos(), Context.MODE_PRIVATE)
        val uid = auth.currentUser?.uid

        // 1. Tentar buscar do Firebase Firestore (nuvem - sobrevive a wipe data)
        if (uid != null) {
            try {
                val snapshot = firestore.collection("usuarios").document(uid)
                    .collection("favoritos").get().await()
                val firestoreFavoritos = snapshot.documents.mapNotNull { it.toObject(Prestador::class.java) }
                if (firestoreFavoritos.isNotEmpty()) {
                    val json = gson.toJson(firestoreFavoritos)
                    prefs.edit().putString("json_favoritos", json).apply()
                    return firestoreFavoritos
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Erro ao buscar favoritos do Firestore: ${e.message}", e)
            }
        }

        // 2. Tentar buscar da API Retrofit
        val remoteFavoritos = try {
            val response = api.getFavoritos()
            Log.d("UserRepository", "getFavoritos resp: ${response.code()} body: ${response.body()}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao buscar favoritos (rede): ${e.message}", e)
            null
        }

        if (!remoteFavoritos.isNullOrEmpty()) {
            val json = gson.toJson(remoteFavoritos)
            prefs.edit().putString("json_favoritos", json).apply()
            return remoteFavoritos
        }

        // 3. Fallback para o cache local permanente da conta
        val jsonLocal = prefs.getString("json_favoritos", null)
        if (!jsonLocal.isNullOrEmpty()) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<List<Prestador>>() {}.type
                val lista = gson.fromJson<List<Prestador>>(jsonLocal, type)
                if (!lista.isNullOrEmpty()) return lista
            } catch (e: Exception) {
                Log.e("UserRepository", "Erro ao desserializar favoritos locais: ${e.message}")
            }
        }

        return emptyList()
    }

    suspend fun enviarEmailRedefinicaoSenha(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao enviar email de redefinição de senha: ${e.message}", e)
            false
        }
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
