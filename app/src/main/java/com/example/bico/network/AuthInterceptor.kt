package com.example.bico.network

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            return chain.proceed(originalRequest)
        }

        return try {
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
            Log.e("AuthInterceptor", "Erro ao obter token JWT do Firebase Auth: ${e.message}", e)
            chain.proceed(originalRequest)
        }
    }
}
