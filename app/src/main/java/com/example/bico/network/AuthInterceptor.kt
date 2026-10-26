package com.example.bico.network

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.ExecutionException

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return chain.proceed(originalRequest)

        return try {
            // Usa forceRefresh = false para utilizar o token em cache se for válido.
            // O Firebase Auth renovará o token automaticamente em background quando expirado.
            val tokenTask = user.getIdToken(false)
            val tokenResult = Tasks.await(tokenTask)
            val token = tokenResult.token

            if (!token.isNullOrEmpty()) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        } catch (e: Exception) {
            val rootCause = if ((e is ExecutionException) && (e.cause != null)) e.cause else e

            if (rootCause is FirebaseAuthInvalidUserException) {
                Log.w("AuthInterceptor", "Credenciais do usuário inválidas no Firebase Auth. Realizando logout: ${rootCause.message}")
                auth.signOut()
            } else {
                Log.e("AuthInterceptor", "Erro ao obter token JWT do Firebase Auth: ${rootCause?.message}", rootCause)
            }

            chain.proceed(originalRequest)
        }
    }
}
