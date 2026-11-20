package com.veneca.app.model

import androidx.compose.runtime.Immutable

enum class UserRole(val label: String) {
    BUYER("Comprador"),
    SELLER_WHOLESALE("Emprendedor / Mayorista")
}

@Immutable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val cedula: String,
    val phone: String,
    val role: UserRole,
    val tier: SellerTier = SellerTier.BRONZE,
    val businessName: String? = null,
    val pagoMovilData: String? = null,
    val binanceId: String? = null,
    val rating: Double = 5.0,
    val totalOrders: Int = 0,
    val iaVnzlaLevel: Int = 3,
    val iaCreditLimit: Double = 250.0,
    val iaCreditUsed: Double = 0.0
) {
    val iaCreditAvailable: Double
        get() = (iaCreditLimit - iaCreditUsed).coerceAtLeast(0.0)
}
