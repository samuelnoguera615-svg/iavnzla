package com.veneca.app

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val isWholesaleAvailable: Boolean,
    val stock: Int,
    val seller: String
)
