package com.duoc.cocinacapital.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.duoc.cocinacapital.viewmodel.AppViewModel

private data class Pestana(val titulo: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana("Locales", Icons.Default.Home),
    Pestana("Tickets", Icons.Default.ShoppingCart),
    Pestana("Perfil", Icons.Default.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onLocalClick: (Int) -> Unit,
    onCerrarSesion: () -> Unit
) {
    // rememberSaveable: la pestaña se mantiene al rotar la pantalla
    var seleccionada by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pestanas[seleccionada].titulo) }
            )
        },
        bottomBar = {
            NavigationBar {
                pestanas.forEachIndexed { indice, pestana ->
                    NavigationBarItem(
                        selected = seleccionada == indice,
                        onClick = { seleccionada = indice },
                        icon = {
                            Icon(
                                pestana.icono,
                                contentDescription = pestana.titulo
                            )
                        },
                        label = { Text(pestana.titulo) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Transición animada al cambiar de pestaña
        AnimatedContent(
            targetState = seleccionada,
            label = "pestanas",
            modifier = Modifier.padding(innerPadding)
        ) { pestana ->
            Box(Modifier.fillMaxSize()) {
                when (pestana) {
                    0 -> LocalesTab(viewModel, onLocalClick)
                    1 -> TicketsTab(viewModel)
                    else -> PerfilTab(viewModel, onCerrarSesion)
                }
            }
        }
    }
}