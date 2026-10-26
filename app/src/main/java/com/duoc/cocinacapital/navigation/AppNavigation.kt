package com.duoc.cocinacapital.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.cocinacapital.ui.screens.LoginScreen
import com.duoc.cocinacapital.ui.screens.RegistroScreen

// VERSIÓN PROVISORIA: la Persona 3 la reemplaza en su Paso 10
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }
        composable(Screen.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            // Pantalla temporal hasta que la Persona 3 cree HomeScreen
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Sesión iniciada! (Inicio en construcción)")
            }
        }
    }
}