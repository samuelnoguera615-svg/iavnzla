package com.veneca.app.model

enum class SellerTier(val label: String, val minSales: Int, val minVolume: Double) {
    BRONZE("Bronce", 0, 0.0),
    SILVER("Plata", 20, 700.0),
    GOLD("Oro", 50, 1500.0),
    PLATINUM("Platino", 100, 3000.0)
}

fun resolveTier(totalSales: Int, monthlyVolume: Double): SellerTier {
    return when {
        totalSales >= SellerTier.PLATINUM.minSales || monthlyVolume >= SellerTier.PLATINUM.minVolume -> SellerTier.PLATINUM
        totalSales >= SellerTier.GOLD.minSales || monthlyVolume >= SellerTier.GOLD.minVolume -> SellerTier.GOLD
        totalSales >= SellerTier.SILVER.minSales || monthlyVolume >= SellerTier.SILVER.minVolume -> SellerTier.SILVER
        else -> SellerTier.BRONZE
    }
}
