package com.duoc.cocinacapital.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.duoc.cocinacapital.ui.components.CuponCard
import com.duoc.cocinacapital.ui.components.TextoError
import com.duoc.cocinacapital.viewmodel.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun PerfilTab(viewModel: AppViewModel, onCerrarSesion: () -> Unit) {
    val usuario by viewModel.usuario.collectAsState()
    val foto by viewModel.foto.collectAsState()
    val cupones by viewModel.cupones.collectAsState()
    var errorCamara by remember { mutableStateOf<String?>(null) }

    // Reloj que se actualiza cada segundo (tiempo restante y ocultar cupones vencidos)
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            ahora = System.currentTimeMillis()
        }
    }
    // Solo los cupones por canjear: ni los usados ni los vencidos
    val cuponesPorCanjear = cupones.filter { it.vigente(ahora) }

    // Recurso nativo 2: cámara (abre la app de cámara del sistema y devuelve una miniatura)
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> viewModel.onFotoTomada(bitmap) }

    val context = LocalContext.current

    // Pide el permiso y, si el usuario acepta, abre la cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            try {
                camaraLauncher.launch(null)
            } catch (e: ActivityNotFoundException) {
                errorCamara = "No se encontró una app de cámara"
            }
        } else {
            errorCamara = "Permiso de cámara denegado"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Transición suave entre el ícono y la foto tomada
        Crossfade(targetState = foto, label = "foto") { imagen ->
            if (imagen != null) {
                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Sin foto de perfil",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(128.dp)
                )
            }
        }

        Button(
            onClick = {
                errorCamara = null
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (tienePermiso) {
                    try {
                        camaraLauncher.launch(null)
                    } catch (e: ActivityNotFoundException) {
                        errorCamara = "No se encontró una app de cámara"
                    }
                } else {
                    permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Icon(Icons.Default.Face, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (foto == null) "Tomar foto de perfil" else "Cambiar foto")
        }

        TextoError(errorCamara)

        Text(
            usuario?.nombre ?: "",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            usuario?.correo ?: "",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Comuna: ${usuario?.comuna ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ---------- Mis cupones por canjear ----------
        Text(
            "Mis cupones por canjear",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        if (cuponesPorCanjear.isEmpty()) {
            Text(
                "No tienes cupones. Canjea un ticket en un local y aparecerá aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            cuponesPorCanjear.forEach { CuponCard(it, ahora) }
        }

        OutlinedButton(
            onClick = { viewModel.cerrarSesion(onCerrarSesion) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}