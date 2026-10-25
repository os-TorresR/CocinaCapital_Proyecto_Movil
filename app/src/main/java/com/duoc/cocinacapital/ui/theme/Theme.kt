package com.duoc.cocinacapital.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de la marca (tonos de cocina: terracota y oliva)
private val Terracota = Color(0xFFB5462B)
private val TerracotaClaro = Color(0xFFFFDBD1)
private val Oliva = Color(0xFF5C6B2E)
private val OlivaClaro = Color(0xFFDFEBB0)

private val EsquemaClaro = lightColorScheme(
    primary = Terracota,
    onPrimary = Color.White,
    primaryContainer = TerracotaClaro,
    onPrimaryContainer = Color(0xFF3B0900),
    secondary = Oliva,
    onSecondary = Color.White,
    secondaryContainer = OlivaClaro,
    onSecondaryContainer = Color(0xFF1A2000),
    background = Color(0xFFFFFBFF),
    surface = Color(0xFFFFFBFF)
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFFFB4A0),
    onPrimary = Color(0xFF5F1500),
    primaryContainer = Color(0xFF8E2F17),
    onPrimaryContainer = TerracotaClaro,
    secondary = Color(0xFFC3CF96),
    onSecondary = Color(0xFF2D3505),
    secondaryContainer = Color(0xFF444F18),
    onSecondaryContainer = OlivaClaro
)

@Composable
fun CocinaCapitalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) EsquemaOscuro else EsquemaClaro,
        content = content
    )
}
