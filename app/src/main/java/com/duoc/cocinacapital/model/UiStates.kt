package com.duoc.cocinacapital.model

// ---------- Formulario de registro ----------
data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val comuna: String = "",
    val esMayorDeEdad: Boolean = false,
    val cargando: Boolean = false,
    val errores: RegistroErrores = RegistroErrores()
)

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null,
    val comuna: String? = null,
    val mayorDeEdad: String? = null
) {
    fun hayErrores(): Boolean =
        listOfNotNull(nombre, correo, clave, confirmarClave, comuna, mayorDeEdad).isNotEmpty()
}

// ---------- Formulario de inicio de sesión ----------
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val errores: LoginErrores = LoginErrores()
)

data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null
)

// ---------- Formulario de compra de tickets ----------
data class CompraUiState(
    val cantidad: String = "",
    val error: String? = null
)

// Mensaje animado de éxito o error
data class Aviso(
    val texto: String,
    val esError: Boolean = false
)
