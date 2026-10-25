package com.duoc.cocinacapital.model

// Restaurante asociado a la plataforma
data class Local(
    val id: Int,
    val nombre: String,
    val comuna: String,
    val tipoCocina: String,
    val beneficio: String,
    val latitud: Double,
    val longitud: Double
)

// Local con la distancia al usuario (null si no hay ubicación)
data class LocalItem(
    val local: Local,
    val distanciaKm: Float? = null
)

// Ubicación obtenida desde el GPS del dispositivo
data class Coordenadas(
    val latitud: Double,
    val longitud: Double
)
