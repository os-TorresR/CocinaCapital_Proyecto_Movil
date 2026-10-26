package com.duoc.cocinacapital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.cocinacapital.data.UsuarioDataStore
import com.duoc.cocinacapital.model.LoginErrores
import com.duoc.cocinacapital.model.LoginUiState
import com.duoc.cocinacapital.util.Seguridad
import com.duoc.cocinacapital.util.Validador
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = UsuarioDataStore(application)

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) = _estado.update {
        it.copy(correo = valor, errorGeneral = null, errores = it.errores.copy(correo = null))
    }

    fun onClaveChange(valor: String) = _estado.update {
        it.copy(clave = valor, errorGeneral = null, errores = it.errores.copy(clave = null))
    }

    private fun validarFormulario(): Boolean {
        val e = _estado.value
        val errores = LoginErrores(
            correo = Validador.validarCorreo(e.correo),
            clave = if (e.clave.isBlank()) "Campo obligatorio" else null
        )
        _estado.update { it.copy(errores = errores) }
        return errores.correo == null && errores.clave == null
    }

    fun iniciarSesion(onExito: () -> Unit) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            delay(800)
            val e = _estado.value
            val usuario = dataStore.obtenerUsuario().first()
            val credencialesOk = usuario != null &&
                    usuario.correo == e.correo.trim().lowercase() &&
                    usuario.claveHash == Seguridad.hashClave(e.clave)

            if (credencialesOk) {
                dataStore.guardarSesion(true)
                _estado.update { LoginUiState() }
                onExito()
            } else {
                _estado.update {
                    it.copy(
                        cargando = false,
                        errorGeneral = "Correo o contraseña incorrectos"
                    )
                }
            }
        }
    }
}