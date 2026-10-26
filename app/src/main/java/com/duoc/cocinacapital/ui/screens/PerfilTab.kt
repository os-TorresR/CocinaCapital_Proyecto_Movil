package com.duoc.cocinacapital.ui.screens

import android.content.ActivityNotFoundException
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.cocinacapital.ui.components.TextoError
import com.duoc.cocinacapital.viewmodel.AppViewModel

@Composable
fun PerfilTab(viewModel: AppViewModel, onCerrarSesion: () -> Unit) {
    val usuario by viewModel.usuario.collectAsState()
    val foto by viewModel.foto.collectAsState()
    var errorCamara by remember { mutableStateOf<String?>(null) }

    // Recurso nativo 2: cámara (abre la app de cámara del sistema y devuelve una miniatura)
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> viewModel.onFotoTomada(bitmap) }

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
                try {
                    camaraLauncher.launch(null)
                } catch (e: ActivityNotFoundException) {
                    errorCamara = "No se encontró una app de cámara"
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