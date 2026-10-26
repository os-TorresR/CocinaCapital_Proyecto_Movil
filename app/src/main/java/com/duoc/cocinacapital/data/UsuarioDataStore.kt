package com.duoc.cocinacapital.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duoc.cocinacapital.model.Cupon
import com.duoc.cocinacapital.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

// DataStore con nombre 'cocina_capital' (persistencia local)
val Context.dataStore by preferencesDataStore(name = "cocina_capital")

class UsuarioDataStore(private val context: Context) {

    // Claves usadas para guardar cada dato
    private val NOMBRE = stringPreferencesKey("nombre")
    private val CORREO = stringPreferencesKey("correo")
    private val CLAVE_HASH = stringPreferencesKey("clave_hash")
    private val COMUNA = stringPreferencesKey("comuna")
    private val SESION_ACTIVA = booleanPreferencesKey("sesion_activa")
    private val SALDO_TICKETS = intPreferencesKey("saldo_tickets")
    private val CUPONES = stringPreferencesKey("cupones")

    suspend fun guardarUsuario(usuario: Usuario) {
        context.dataStore.edit { p ->
            p[NOMBRE] = usuario.nombre
            p[CORREO] = usuario.correo
            p[CLAVE_HASH] = usuario.claveHash
            p[COMUNA] = usuario.comuna
            p[SESION_ACTIVA] = false
            p[SALDO_TICKETS] = 0
            p[CUPONES] = "[]"
        }
    }

    fun obtenerUsuario(): Flow<Usuario?> = context.dataStore.data.map { p ->
        val correo = p[CORREO] ?: return@map null
        Usuario(
            nombre = p[NOMBRE] ?: "",
            correo = correo,
            claveHash = p[CLAVE_HASH] ?: "",
            comuna = p[COMUNA] ?: ""
        )
    }

    suspend fun guardarSesion(activa: Boolean) {
        context.dataStore.edit { it[SESION_ACTIVA] = activa }
    }

    fun obtenerSesion(): Flow<Boolean> = context.dataStore.data.map { it[SESION_ACTIVA] ?: false }

    suspend fun guardarSaldo(saldo: Int) {
        context.dataStore.edit { it[SALDO_TICKETS] = saldo }
    }

    fun obtenerSaldo(): Flow<Int> = context.dataStore.data.map { it[SALDO_TICKETS] ?: 0 }

    // ---------- Cupones (se guardan como texto JSON) ----------
    suspend fun guardarCupones(cupones: List<Cupon>) {
        val json = JSONArray()
        cupones.forEach { c ->
            json.put(
                JSONObject()
                    .put("codigo", c.codigo)
                    .put("local", c.localNombre)
                    .put("beneficio", c.beneficio)
                    .put("creadoEn", c.creadoEn)
                    .put("canjeado", c.canjeado)
            )
        }
        context.dataStore.edit { it[CUPONES] = json.toString() }
    }

    fun obtenerCupones(): Flow<List<Cupon>> = context.dataStore.data.map { p ->
        try {
            val json = JSONArray(p[CUPONES] ?: "[]")
            (0 until json.length()).map { i ->
                val o = json.getJSONObject(i)
                Cupon(
                    codigo = o.getString("codigo"),
                    localNombre = o.getString("local"),
                    beneficio = o.getString("beneficio"),
                    creadoEn = o.getLong("creadoEn"),
                    canjeado = o.optBoolean("canjeado", false)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}