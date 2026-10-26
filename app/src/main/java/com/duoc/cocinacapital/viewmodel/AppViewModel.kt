package com.duoc.cocinacapital.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.cocinacapital.data.UsuarioDataStore
import com.duoc.cocinacapital.model.Aviso
import com.duoc.cocinacapital.model.CompraUiState
import com.duoc.cocinacapital.model.Cupon
import com.duoc.cocinacapital.model.Coordenadas
import com.duoc.cocinacapital.model.Local
import com.duoc.cocinacapital.model.LocalItem
import com.duoc.cocinacapital.model.Usuario
import com.duoc.cocinacapital.repository.LocalRepository
import com.duoc.cocinacapital.util.Seguridad
import com.duoc.cocinacapital.util.Validador
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel compartido por las pantallas de la sesión (usuario, tickets, recursos nativos)
class AppViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val PRECIO_TICKET = 3990
    }

    private val dataStore = UsuarioDataStore(application)

    // null = cargando; true/false = había sesión guardada
    private val _sesionActiva = MutableStateFlow<Boolean?>(null)
    val sesionActiva: StateFlow<Boolean?> = _sesionActiva.asStateFlow()

    val usuario: StateFlow<Usuario?> = dataStore.obtenerUsuario()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val saldo: StateFlow<Int> = dataStore.obtenerSaldo()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Registro de cupones del usuario (por canjear, canjeados y vencidos)
    val cupones: StateFlow<List<Cupon>> = dataStore.obtenerCupones()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _ubicacion = MutableStateFlow<Coordenadas?>(null)
    val ubicacion: StateFlow<Coordenadas?> = _ubicacion.asStateFlow()

    // Lista de locales, ordenada por cercanía cuando hay ubicación
    val locales: StateFlow<List<LocalItem>> = _ubicacion
        .map { LocalRepository.obtenerLocales(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            LocalRepository.obtenerLocales(null)
        )

    private val _foto = MutableStateFlow<Bitmap?>(null)
    val foto: StateFlow<Bitmap?> = _foto.asStateFlow()

    private val _compra = MutableStateFlow(CompraUiState())
    val compra: StateFlow<CompraUiState> = _compra.asStateFlow()

    private val _aviso = MutableStateFlow<Aviso?>(null)
    val aviso: StateFlow<Aviso?> = _aviso.asStateFlow()
    private var avisoJob: Job? = null

    init {
        // Carga inicial desde DataStore (con demora para mostrar el loader)
        viewModelScope.launch {
            delay(1200)
            _sesionActiva.value = dataStore.obtenerSesion().first()
        }
    }

    // ---------- Compra de tickets ----------
    fun onCantidadChange(valor: String) =
        _compra.update {
            it.copy(
                cantidad = valor.filter { c -> c.isDigit() }.take(2),
                error = null
            )
        }

    fun comprarTickets() {
        val error = Validador.validarCantidad(_compra.value.cantidad)
        if (error != null) {
            _compra.update { it.copy(error = error) }
            return
        }

        val cantidad = _compra.value.cantidad.toInt()
        viewModelScope.launch {
            dataStore.guardarSaldo(saldo.value + cantidad)
            _compra.value = CompraUiState()
            mostrarAviso(Aviso("¡Compraste $cantidad ticket(s)!"))
        }
    }

    // ---------- Canje (RN-01: un ticket se usa una sola vez) ----------
    // Al canjear un ticket se genera un cupón (código + QR) que dura 1 hora
    fun canjear(local: Local) {
        if (saldo.value <= 0) {
            mostrarAviso(
                Aviso(
                    "No tienes tickets. Compra en la pestaña Tickets.",
                    esError = true
                )
            )
            return
        }

        viewModelScope.launch {
            val existentes = cupones.value
            var codigo = Seguridad.generarCodigoCanje()
            while (existentes.any { it.codigo == codigo }) {
                codigo = Seguridad.generarCodigoCanje()
            }

            val cupon = Cupon(
                codigo = codigo,
                localNombre = local.nombre,
                beneficio = local.beneficio,
                creadoEn = System.currentTimeMillis()
            )
            dataStore.guardarSaldo(saldo.value - 1)
            dataStore.guardarCupones(existentes + cupon)
            mostrarAviso(
                Aviso("¡Cupón listo! Búscalo en tu Perfil, vale por 1 hora. Código: $codigo")
            )
        }
    }

    // El comercio escanea el QR (o escribe el código) para canjear el cupón
    fun canjearCupon(codigoLeido: String?) {
        val codigo = codigoLeido?.trim()?.uppercase().orEmpty()
        if (codigo.isEmpty()) {
            mostrarAviso(Aviso("Escanea o escribe un código", esError = true))
            return
        }

        val lista = cupones.value
        val cupon = lista.find { it.codigo == codigo }
        val ahora = System.currentTimeMillis()

        when {
            cupon == null ->
                mostrarAviso(Aviso("El código $codigo no existe", esError = true))

            cupon.canjeado ->
                mostrarAviso(Aviso("Este cupón ya fue canjeado", esError = true))

            !cupon.vigente(ahora) ->
                mostrarAviso(Aviso("Este cupón venció", esError = true))

            else -> viewModelScope.launch {
                // Queda marcado como canjeado: sale de la lista por canjear
                dataStore.guardarCupones(
                    lista.map { if (it.codigo == codigo) it.copy(canjeado = true) else it }
                )
                mostrarAviso(
                    Aviso("Cupón válido: ${cupon.beneficio} en ${cupon.localNombre}")
                )
            }
        }
    }

    fun mostrarError(texto: String) = mostrarAviso(Aviso(texto, esError = true))

    // ---------- Recursos nativos ----------
    fun onUbicacionObtenida(coordenadas: Coordenadas?) {
        _ubicacion.value = coordenadas
    }

    fun onFotoTomada(bitmap: Bitmap?) {
        if (bitmap != null) _foto.value = bitmap
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            dataStore.guardarSesion(false)
            _foto.value = null
            _ubicacion.value = null
            onListo()
        }
    }

    // Muestra un mensaje y lo oculta tras 3 segundos
    private fun mostrarAviso(aviso: Aviso) {
        avisoJob?.cancel()
        avisoJob = viewModelScope.launch {
            _aviso.value = aviso
            delay(3000)
            _aviso.value = null
        }
    }
}