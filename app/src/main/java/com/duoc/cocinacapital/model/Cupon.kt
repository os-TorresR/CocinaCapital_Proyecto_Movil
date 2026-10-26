package com.duoc.cocinacapital.model

// Cupón que se genera al canjear un ticket. Dura 1 hora y se usa una sola vez.
data class Cupon(
    val codigo: String,
    val localNombre: String,
    val beneficio: String,
    val creadoEn: Long,
    val canjeado: Boolean = false
) {
    val venceEn: Long get() = creadoEn + DURACION_MS

    // Está por canjear: no se ha usado y aún no vence
    fun vigente(ahora: Long): Boolean = !canjeado && ahora < venceEn

    companion object {
        const val DURACION_MS = 60 * 60 * 1000L // 1 hora
    }
}