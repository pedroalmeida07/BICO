package com.example.bico.utils

import com.example.bico.R

object PasswordUtils {
    fun validarSenha(senha: String): Int? {
        if (senha.length < 8) {
            return R.string.msg_erro_senha_tamanho_minimo
        }
        if (!senha.any { it.isUpperCase() }) {
            return R.string.msg_erro_senha_maiuscula
        }
        if (!senha.any { it.isLowerCase() }) {
            return R.string.msg_erro_senha_minuscula
        }
        if (!senha.any { !it.isLetterOrDigit() }) {
            return R.string.msg_erro_senha_caractere_especial
        }
        return null
    }
}
