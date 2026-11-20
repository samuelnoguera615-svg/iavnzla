package com.veneca.app.model

import androidx.compose.runtime.Immutable

@Immutable
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val description: String = "",
    val unit: String = "Unidad",
    val price: Double,
    val wholesalePrice: Double? = null,
    val wholesaleMinimum: Int = 1,
    val stock: Int,
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String = "0412-1234567",
    val sellerPaymentMethods: List<String> = listOf("iaCuotas", "Pago Móvil", "Binance Pay"),
    val sellerPagoMovilData: String = "Banesco (0134) · CI: 24.555.888 · Telf: 0412-1234567",
    val sellerBinanceId: String = "Binance Pay ID: 84920194",
    val iaCuotasEligible: Boolean = true,
    val iaDownPaymentPercent: Int = 40, // 40% inicial
    val iaInstallments: Int = 3 // 3 cuotas cada 14 días
) {
    fun calculateDownPayment(activePrice: Double): Double {
        return activePrice * (iaDownPaymentPercent / 100.0)
    }

    fun calculateInstallmentAmount(activePrice: Double): Double {
        val remaining = activePrice * (1.0 - (iaDownPaymentPercent / 100.0))
        return if (iaInstallments > 0) remaining / iaInstallments else 0.0
    }
}
