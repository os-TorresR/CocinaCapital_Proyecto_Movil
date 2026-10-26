package com.duoc.cocinacapital.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat

// Recurso nativo: acceso al GPS del dispositivo
object UbicacionHelper {

    val PERMISOS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun tienePermiso(context: Context): Boolean =
        PERMISOS.any {
            ContextCompat.checkSelfPermission(context, it) ==
                    PackageManager.PERMISSION_GRANTED
        }

    // Solo se llama después de comprobar el permiso
    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(context: Context, onResultado: (Location?) -> Unit) {
        if (!tienePermiso(context)) {
            onResultado(null)
            return
        }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as
                LocationManager

        val proveedor = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER

            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER

            else -> {
                onResultado(null)
                return
            }
        }

        val ultima = manager.getLastKnownLocation(proveedor)
        if (ultima != null) {
            onResultado(ultima)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            manager.getCurrentLocation(
                proveedor,
                null,
                ContextCompat.getMainExecutor(context)
            ) {
                onResultado(it)
            }
        } else {
            @Suppress("DEPRECATION")
            manager.requestSingleUpdate(
                proveedor,
                LocationListener { onResultado(it) },
                Looper.getMainLooper()
            )
        }
    }
}