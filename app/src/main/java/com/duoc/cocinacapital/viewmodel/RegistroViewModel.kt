package com.duoc.cocinacapital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.cocinacapital.data.UsuarioDataStore
import com.duoc.cocinacapital.model.RegistroErrores
import com.duoc.cocinacapital.model.RegistroUiState
import com.duoc.cocinacapital.model.Usuario
import com.duoc.cocinacapital.util.Seguridad
import com.duoc.cocinacapital.util.Validador
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = UsuarioDataStore(application)

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) =
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }

    fun onCorreoChange(valor: String) =
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }

    fun onClaveChange(valor: String) =
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }

    fun onConfirmarClaveChange(valor: String) =
        _estado.update { it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null)) }

    fun onComunaChange(valor: String) =
        _estado.update { it.copy(comuna = valor, errores = it.errores.copy(comuna = null)) }

    fun onMayorDeEdadChange(valor: Boolean) =
        _estado.update { it.copy(esMayorDeEdad = valor, errores = it.errores.copy(mayorDeEdad = null)) }

    private fun validarFormulario(): Boolean {
        val e = _estado.value
        val errores = RegistroErrores(
            nombre = Validador.validarNombre(e.nombre),
            correo = Validador.validarCorreo(e.correo),
            clave = Validador.validarClave(e.clave),
            confirmarClave = Validador.validarConfirmacion(e.clave, e.confirmarClave),
            comuna = Validador.validarComuna(e.comuna),
            mayorDeEdad = Validador.validarMayorDeEdad(e.esMayorDeEdad)
        )
        _estado.update { it.copy(errores = errores) }
        return !errores.hayErrores()
    }

    fun registrar(onExito: () -> Unit) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            delay(1000)
            val e = _estado.value
            dataStore.guardarUsuario(
                Usuario(
                    nombre = e.nombre.trim(),
                    correo = e.correo.trim().lowercase(),
                    claveHash = Seguridad.hashClave(e.clave),
                    comuna = e.comuna
                )
            )
            _estado.update { it.copy(cargando = false) }
            onExito()
        }
    }
}