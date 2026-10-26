package com.duoc.cocinacapital.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.cocinacapital.model.Aviso

// Mensaje de éxito/error que aparece y desaparece con animación
@Composable
fun AvisoAnimado(aviso: Aviso?) {
    // Recuerda el último aviso para que el texto no desaparezca durante la animación de salida
    val ultimo = remember { arrayOfNulls<Aviso>(1) }
    if (aviso != null) ultimo[0] = aviso
    val mostrado = ultimo[0]

    AnimatedVisibility(
        visible = aviso != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        if (mostrado != null) {
            val colores = if (mostrado.esError) {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            } else {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Card(
                colors = colores,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        if (mostrado.esError) Icons.Default.Warning
                        else Icons.Default.CheckCircle,
                        contentDescription = null
                    )
                    Text(
                        mostrado.texto,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
