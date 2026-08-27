package com.example.bico.utils

object CpfValidator {
    fun isValid(cpf: String?): Boolean {
        val cleanCpf = CpfMaskWatcher.unmask(cpf ?: "")
        if (cleanCpf.length != 11 || isRepeatedDigits(cleanCpf)) return false

        val digit1 = calculateDigit(cleanCpf.substring(0, 9), intArrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2))
        val digit2 = calculateDigit(cleanCpf.substring(0, 9) + digit1, intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2))

        return cleanCpf.endsWith("$digit1$digit2")
    }

    private fun isRepeatedDigits(cpf: String): Boolean {
        return (0..9).any { cpf == it.toString().repeat(11) }
    }

    private fun calculateDigit(str: String, weights: IntArray): Int {
        var sum = 0
        for (i in str.indices) {
            sum += str[i].toString().toInt() * weights[i]
        }
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
}