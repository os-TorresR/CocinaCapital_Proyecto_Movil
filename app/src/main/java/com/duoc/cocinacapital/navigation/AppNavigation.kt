package com.duoc.cocinacapital.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duoc.cocinacapital.ui.screens.DetalleLocalScreen
import com.duoc.cocinacapital.ui.screens.HomeScreen
import com.duoc.cocinacapital.ui.screens.LoginScreen
import com.duoc.cocinacapital.ui.screens.RegistroScreen
import com.duoc.cocinacapital.viewmodel.AppViewModel

@Composable
fun AppNavigation() {
    // ViewModel compartido: se crea una sola vez para todas las pantallas
    val appViewModel: AppViewModel = viewModel()
    val sesionActiva by appViewModel.sesionActiva.collectAsState()

    // Carga inicial desde DataStore
    if (sesionActiva == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text("Cargando...")
        }
        return
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (sesionActiva == true) {
            Screen.Home.route
        } else {
            Screen.Login.route
        },
        // Animaciones de transición entre pantallas
        enterTransition = {
            slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
        },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = {
            slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300))
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }

        composable(Screen.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = appViewModel,
                onLocalClick = { id ->
                    navController.navigate(
                        Screen.DetalleLocal.crearRuta(id)
                    )
                },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.DetalleLocal.route,
            arguments = listOf(
                navArgument("localId") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->
            DetalleLocalScreen(
                localId = entrada.arguments?.getInt("localId") ?: 0,
                viewModel = appViewModel,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}