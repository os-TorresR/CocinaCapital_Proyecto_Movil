package com.duoc.cocinacapital.navigation

// Rutas tipo-safe de la app
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Registro : Screen("registro")
    data object Home : Screen("home")

    // Ruta con argumento: el id del local
    data object DetalleLocal : Screen("detalle/{localId}") {
        fun crearRuta(localId: Int) = "detalle/$localId"
    }
}