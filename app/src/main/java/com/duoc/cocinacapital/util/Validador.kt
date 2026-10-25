package com.duoc.cocinacapital.util

// Lógica de validación centralizada y desacoplada de la UI.
// Cada función retorna el mensaje de error, o null si el valor es válido.
object Validador {

    val COMUNAS = listOf("Valparaíso", "Viña del Mar", "Concón", "Quilpué", "Villa Alemana")

    private val REGEX_CORREO = Regex("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")

    fun validarNombre(valor: String): String? = when {
        valor.isBlank() -> "Campo obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun validarCorreo(valor: String): String? = when {
        valor.isBlank() -> "Campo obligatorio"
        !REGEX_CORREO.matches(valor.trim()) -> "Correo inválido (ej: nombre@correo.cl)"
        else -> null
    }

    fun validarClave(valor: String): String? = when {
        valor.isBlank() -> "Campo obligatorio"
        valor.length < 6 -> "Debe tener al menos 6 caracteres"
        valor.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun validarConfirmacion(clave: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Campo obligatorio"
        clave != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    fun validarComuna(valor: String): String? =
        if (valor !in COMUNAS) "Selecciona una comuna" else null

    // Regla de negocio RN-08: los usuarios deben ser mayores de edad
    fun validarMayorDeEdad(valor: Boolean): String? =
        if (!valor) "Debes ser mayor de 18 años" else null

    fun validarCantidad(valor: String): String? {
        val numero = valor.toIntOrNull()
        return when {
            valor.isBlank() -> "Campo obligatorio"
            numero == null -> "Ingresa solo números"
            numero !in 1..10 -> "Puedes comprar entre 1 y 10 tickets"
            else -> null
        }
    }
}
