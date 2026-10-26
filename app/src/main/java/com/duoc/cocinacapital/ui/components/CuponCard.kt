package com.duoc.cocinacapital.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.cocinacapital.model.Cupon
import com.duoc.cocinacapital.util.QrHelper

// Cupón listo para canjear: beneficio, QR, código alfanumérico y tiempo restante
@Composable
fun CuponCard(cupon: Cupon, ahora: Long) {
    // El QR se genera una sola vez por cupón
    val qr = remember(cupon.codigo) { QrHelper.generar(cupon.codigo) }

    val segundos = ((cupon.venceEn - ahora) / 1000).coerceAtLeast(0)
    val tiempo = "%02d:%02d".format(segundos / 60, segundos % 60)
    val pocoTiempo = segundos < 5 * 60

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                cupon.localNombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "★ ${cupon.beneficio}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Image(
                bitmap = qr.asImageBitmap(),
                contentDescription = "Código QR del cupón",
                modifier = Modifier.size(200.dp)
            )
            Text(
                cupon.codigo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Text(
                "Vence en $tiempo",
                style = MaterialTheme.typography.bodyMedium,
                color = if (pocoTiempo) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}