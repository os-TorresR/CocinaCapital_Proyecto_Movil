package com.duoc.cocinacapital.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de Cocina Capital
private val MentaClaro = Color(0xFFCFF0EA)
private val Menta = Color(0xFF88C9C4)
private val VerdeAgua = Color(0xFF3E9B94)
private val Petroleo = Color(0xFF20666B)
private val PetroleoOscuro = Color(0xFF0C3B45)

private val EsquemaClaro = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = MentaClaro,
    onPrimaryContainer = PetroleoOscuro,
    secondary = Petroleo,
    onSecondary = Color.White,
    secondaryContainer = Menta,
    onSecondaryContainer = PetroleoOscuro,
    tertiary = VerdeAgua,
    onTertiary = PetroleoOscuro,
    tertiaryContainer = MentaClaro,
    onTertiaryContainer = PetroleoOscuro,
    background = Color.White,
    onBackground = PetroleoOscuro,
    surface = Color.White,
    onSurface = PetroleoOscuro,
    surfaceVariant = MentaClaro,
    onSurfaceVariant = PetroleoOscuro,
    outline = Petroleo
)

private val EsquemaOscuro = darkColorScheme(
    primary = Menta,
    onPrimary = PetroleoOscuro,
    primaryContainer = Petroleo,
    onPrimaryContainer = MentaClaro,
    secondary = MentaClaro,
    onSecondary = PetroleoOscuro,
    secondaryContainer = Petroleo,
    onSecondaryContainer = MentaClaro,
    tertiary = VerdeAgua,
    onTertiary = PetroleoOscuro,
    tertiaryContainer = Petroleo,
    onTertiaryContainer = MentaClaro,
    background = PetroleoOscuro,
    onBackground = MentaClaro,
    surface = PetroleoOscuro,
    onSurface = MentaClaro,
    surfaceVariant = Petroleo,
    onSurfaceVariant = MentaClaro,
    outline = Menta
)

@Composable
fun CocinaCapitalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            EsquemaOscuro
        } else {
            EsquemaClaro
        },
        content = content
    )
}