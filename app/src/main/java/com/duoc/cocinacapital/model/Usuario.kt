package com.duoc.cocinacapital.model

// Usuario guardado en el almacenamiento local (la clave se guarda como hash)
data class Usuario(
    val nombre: String,
    val correo: String,
    val claveHash: String,
    val comuna: String
)
