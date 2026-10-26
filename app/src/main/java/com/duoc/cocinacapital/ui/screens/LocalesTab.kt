package com.duoc.cocinacapital.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.cocinacapital.model.Coordenadas
import com.duoc.cocinacapital.model.LocalItem
import com.duoc.cocinacapital.ui.components.TextoError
import com.duoc.cocinacapital.util.UbicacionHelper
import com.duoc.cocinacapital.viewmodel.AppViewModel

@Composable
fun LocalesTab(viewModel: AppViewModel, onLocalClick: (Int) -> Unit) {
    val context = LocalContext.current
    val locales by viewModel.locales.collectAsState()
    val ubicacion by viewModel.ubicacion.collectAsState()
    var buscando by remember { mutableStateOf(false) }
    var errorUbicacion by remember { mutableStateOf<String?>(null) }

    // Recurso nativo 1: GPS
    fun obtenerUbicacion() {
        buscando = true
        UbicacionHelper.obtenerUbicacion(context) { location ->
            buscando = false
            if (location == null) {
                errorUbicacion =
                    "No se pudo obtener la ubicación. Revisa que el GPS esté activo."
            } else {
                viewModel.onUbicacionObtenida(
                    Coordenadas(location.latitude, location.longitude)
                )
            }
        }
    }

    // Solicitud del permiso en tiempo de ejecución
    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { it }) obtenerUbicacion()
        else errorUbicacion = "Permiso de ubicación denegado"
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedButton(
                onClick = {
                    errorUbicacion = null
                    if (UbicacionHelper.tienePermiso(context)) {
                        obtenerUbicacion()
                    } else {
                        permisoLauncher.launch(UbicacionHelper.PERMISOS)
                    }
                },
                enabled = !buscando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (buscando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    if (ubicacion == null) "Usar mi ubicación"
                    else "Actualizar ubicación"
                )
            }

            Text(
                if (ubicacion == null) {
                    "Activa tu ubicación para ver los locales más cercanos"
                } else {
                    "Locales ordenados por cercanía"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            TextoError(errorUbicacion)
        }

        // animateItem: las tarjetas se reordenan con animación al obtener la ubicación
        items(locales, key = { it.local.id }) { item ->
            TarjetaLocal(
                item,
                onClick = { onLocalClick(item.local.id) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun TarjetaLocal(
    item: LocalItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                item.local.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${item.local.tipoCocina} · ${item.local.comuna}" +
                        (item.distanciaKm?.let { " · a %.1f km".format(it) } ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    item.local.beneficio,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}