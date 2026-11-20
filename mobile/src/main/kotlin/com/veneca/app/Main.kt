package com.veneca.app

fun main() {
    val repository = MarketplaceRepository()
    val products = repository.searchProducts("hogar")

    println("Productos encontrados: ${products.size}")
    products.forEach { product ->
        println("- ${product.name} | ${product.category} | ${product.price} USD | stock=${product.stock}")
    }

    val tier = resolveTier(totalSales = 62, monthlyVolume = 1950.0)
    println("Nivel del vendedor: ${tier.label}")
}
