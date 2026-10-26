package com.duoc.cocinacapital.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// Convierte un texto (el código del cupón) en una imagen QR
object QrHelper {
    fun generar(texto: String, tamano: Int = 400): Bitmap {
        val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamano, tamano)
        val pixeles = IntArray(tamano * tamano)
        for (y in 0 until tamano) {
            for (x in 0 until tamano) {
                pixeles[y * tamano + x] = if (matriz[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(pixeles, tamano, tamano, Bitmap.Config.ARGB_8888)
    }
}