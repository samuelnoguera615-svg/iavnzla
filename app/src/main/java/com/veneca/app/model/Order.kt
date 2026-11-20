package com.veneca.app.model

import androidx.compose.runtime.Immutable

enum class OrderStatus(val label: String, val badgeColorHex: Long) {
    PENDING_PAYMENT("Esperando Pago del Comprador", 0xFFF59E0B),
    PAID_PENDING_SELLER("Pagado · Esperando Confirmación del Vendedor", 0xFF6366F1),
    COMPLETED("Completado · Fondos Liberados", 0xFF10B981),
    CANCELLED("Cancelado", 0xFFEF4444)
}

@Immutable
data class P2POrder(
    val id: String,
    val productId: String,
    val productName: String,
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val sellerName: String,
    val quantity: Int,
    val unitPriceUsd: Double,
    val totalUsd: Double,
    val totalVes: Double,
    val isWholesale: Boolean,
    val paymentMethod: String,
    val sellerPaymentDetails: String,
    val buyerPaymentRef: String? = null,
    val sellerPaymentRef: String? = null,
    val status: OrderStatus = OrderStatus.PENDING_PAYMENT,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isIaCuotasOrder: Boolean = false,
    val iaDownPaymentUsd: Double = 0.0,
    val iaInstallmentAmountUsd: Double = 0.0,
    val iaTotalInstallments: Int = 3,
    val iaInstallmentsPaid: Int = 1
)
