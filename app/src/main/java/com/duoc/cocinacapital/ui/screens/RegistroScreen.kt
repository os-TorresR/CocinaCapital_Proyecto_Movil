package com.duoc.cocinacapital.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.cocinacapital.ui.components.CampoFormulario
import com.duoc.cocinacapital.ui.components.TextoError
import com.duoc.cocinacapital.util.Validador
import com.duoc.cocinacapital.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CampoFormulario(
                valor = estado.nombre,
                onValorChange = viewModel::onNombreChange,
                etiqueta = "Nombre",
                icono = Icons.Default.Person,
                error = estado.errores.nombre
            )
            CampoFormulario(
                valor = estado.correo,
                onValorChange = viewModel::onCorreoChange,
                etiqueta = "Correo electrónico",
                icono = Icons.Default.Email,
                error = estado.errores.correo,
                tipoTeclado = KeyboardType.Email
            )
            CampoFormulario(
                valor = estado.clave,
                onValorChange = viewModel::onClaveChange,
                etiqueta = "Contraseña",
                icono = Icons.Default.Lock,
                error = estado.errores.clave,
                esClave = true,
                tipoTeclado = KeyboardType.Password
            )
            CampoFormulario(
                valor = estado.confirmarClave,
                onValorChange = viewModel::onConfirmarClaveChange,
                etiqueta = "Confirmar contraseña",
                icono = Icons.Default.Lock,
                error = estado.errores.confirmarClave,
                esClave = true,
                tipoTeclado = KeyboardType.Password
            )

            Text("Comuna", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Validador.COMUNAS.forEach { comuna ->
                    val seleccionada = estado.comuna == comuna
                    FilterChip(
                        selected = seleccionada,
                        onClick = { viewModel.onComunaChange(comuna) },
                        label = { Text(comuna) },
                        leadingIcon = if (seleccionada) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }
            TextoError(estado.errores.comuna)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.esMayorDeEdad,
                    onCheckedChange = viewModel::onMayorDeEdadChange
                )
                Text("Declaro ser mayor de 18 años")
            }
            TextoError(estado.errores.mayorDeEdad)

            Button(
                onClick = { viewModel.registrar(onRegistroExitoso) },
                enabled = !estado.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(52.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrarme")
                }
            }
        }
    }
}