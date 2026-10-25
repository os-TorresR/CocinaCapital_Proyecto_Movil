package com.duoc.cocinacapital.repository

import android.location.Location
import com.duoc.cocinacapital.model.Coordenadas
import com.duoc.cocinacapital.model.Local
import com.duoc.cocinacapital.model.LocalItem

// Simula el origen de datos de los locales asociados
object LocalRepository {

    private val locales = listOf(
        Local(1, "Café Turri", "Valparaíso", "Chilena", "Postre gratis", -33.0429, -71.6276),
        Local(2, "La Picá del Puerto", "Valparaíso", "Mariscos", "15% de descuento", -33.0380, -71.6290),
        Local(3, "Sushi Marga Marga", "Viña del Mar", "Japonesa", "Bebida gratis", -33.0245, -71.5518),
        Local(4, "Empanadas Concón", "Concón", "Chilena", "2x1 en empanadas", -32.9230, -71.5170),
        Local(5, "Pizzería El Sol", "Quilpué", "Italiana", "10% de descuento", -33.0475, -71.4425),
        Local(6, "Sabor Andino", "Villa Alemana", "Peruana", "Entrada gratis", -33.0425, -71.3730)
    )

    fun obtenerLocales(ubicacion: Coordenadas?): List<LocalItem> {
        if (ubicacion == null) return locales.map { LocalItem(it) }
        return locales
            .map { LocalItem(it, distanciaKm(ubicacion, it)) }
            .sortedBy { it.distanciaKm }
    }

    fun buscarPorId(id: Int): Local? = locales.find { it.id == id }

    private fun distanciaKm(desde: Coordenadas, local: Local): Float {
        val resultado = FloatArray(1)
        Location.distanceBetween(desde.latitud, desde.longitud, local.latitud, local.longitud, resultado)
        return resultado[0] / 1000f
    }
}
