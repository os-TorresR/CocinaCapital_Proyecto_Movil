package com.duoc.cocinacapital.util

import java.security.MessageDigest
import java.security.SecureRandom

object Seguridad {

    // La contraseña nunca se guarda en texto plano, solo su hash SHA-256
    fun hashClave(clave: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(clave.toByteArray())
            .joinToString("") { "%02x".format(it) }

    // Código de canje aleatorio y no predecible (RNF-06)
    fun generarCodigoCanje(): String {
        val caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()
        return (1..6).map { caracteres[random.nextInt(caracteres.length)] }.joinToString("")
    }
}
