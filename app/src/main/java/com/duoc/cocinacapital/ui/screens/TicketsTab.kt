package com.duoc.cocinacapital.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.duoc.cocinacapital.ui.components.AvisoAnimado
import com.duoc.cocinacapital.ui.components.CampoFormulario
import com.duoc.cocinacapital.viewmodel.AppViewModel

@Composable
fun TicketsTab(viewModel: AppViewModel) {
    val saldo by viewModel.saldo.collectAsState()
    val compra by viewModel.compra.collectAsState()
    val aviso by viewModel.aviso.collectAsState()

    // Animaciones: el número cuenta hasta el nuevo saldo y la tarjeta cambia de color
    val saldoAnimado by animateIntAsState(
        targetValue = saldo,
        animationSpec = tween(600),
        label = "saldo"
    )

    val colorTarjeta by animateColorAsState(
        targetValue = if (saldo > 0) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(500),
        label = "colorSaldo"
    )

    // Estado derivado: total a pagar según la cantidad ingresada
    val total by remember(compra.cantidad) {
        derivedStateOf {
            (compra.cantidad.toIntOrNull() ?: 0) * AppViewModel.PRECIO_TICKET
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colorTarjeta),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Tickets disponibles",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "$saldoAnimado",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            "Comprar tickets",
            style = MaterialTheme.typography.titleMedium
        )

        CampoFormulario(
            valor = compra.cantidad,
            onValorChange = viewModel::onCantidadChange,
            etiqueta = "Cantidad (1 a 10)",
            icono = Icons.Default.ShoppingCart,
            error = compra.error,
            tipoTeclado = KeyboardType.Number
        )

        Text(
            "Total: $${"%,d".format(total).replace(',', '.')} (pago simulado)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = viewModel::comprarTickets,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Comprar")
        }

        AvisoAnimado(aviso)
    }
}