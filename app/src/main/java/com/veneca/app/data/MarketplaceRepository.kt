package com.veneca.app.data

import android.content.Context
import com.veneca.app.model.Product
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MarketplaceRepository(context: Context) {
    private val preferences = context.getSharedPreferences("iavnzla_marketplace_clean", Context.MODE_PRIVATE)

    // User explicitly requested: NO colocar productos precargados por defecto.
    // El catálogo inicia limpio y se alimenta exclusivamente de lo que publican los usuarios.

    fun getAllProducts(): List<Product> {
        val saved = preferences.getString(KEY_PRODUCTS_CLEAN, null) ?: return emptyList()
        val array = JSONArray(saved)
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Product(
                id = item.getString("id"),
                name = item.getString("name"),
                category = item.getString("category"),
                description = item.optString("description", ""),
                unit = item.optString("unit", "Unidad"),
                price = item.getDouble("price"),
                wholesalePrice = if (item.isNull("wholesalePrice")) null else item.getDouble("wholesalePrice"),
                wholesaleMinimum = item.optInt("wholesaleMinimum", 1),
                stock = item.getInt("stock"),
                sellerId = item.getString("sellerId"),
                sellerName = item.optString("sellerName", "Emprendedor"),
                sellerPhone = item.optString("sellerPhone", "0412-1234567"),
                sellerPaymentMethods = parseStringList(item.optJSONArray("sellerPaymentMethods")),
                sellerPagoMovilData = item.optString("sellerPagoMovilData", "Pago Móvil disponible"),
                sellerBinanceId = item.optString("sellerBinanceId", "Binance Pay disponible"),
                iaCuotasEligible = item.optBoolean("iaCuotasEligible", true),
                iaDownPaymentPercent = item.optInt("iaDownPaymentPercent", 40),
                iaInstallments = item.optInt("iaInstallments", 3)
            )
        }
    }

    private fun parseStringList(array: JSONArray?): List<String> {
        if (array == null || array.length() == 0) return listOf("iaCuotas", "Pago Móvil", "Binance Pay")
        return (0 until array.length()).map { array.getString(it) }
    }

    fun saveProducts(products: List<Product>) {
        val array = JSONArray()
        products.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("category", item.category)
                put("description", item.description)
                put("unit", item.unit)
                put("price", item.price)
                put("wholesalePrice", item.wholesalePrice)
                put("wholesaleMinimum", item.wholesaleMinimum)
                put("stock", item.stock)
                put("sellerId", item.sellerId)
                put("sellerName", item.sellerName)
                put("sellerPhone", item.sellerPhone)
                put("sellerPaymentMethods", JSONArray(item.sellerPaymentMethods))
                put("sellerPagoMovilData", item.sellerPagoMovilData)
                put("sellerBinanceId", item.sellerBinanceId)
                put("iaCuotasEligible", item.iaCuotasEligible)
                put("iaDownPaymentPercent", item.iaDownPaymentPercent)
                put("iaInstallments", item.iaInstallments)
            })
        }
        preferences.edit().putString(KEY_PRODUCTS_CLEAN, array.toString()).apply()
    }

    fun searchProducts(query: String, category: String? = null, wholesaleOnly: Boolean = false): List<Product> {
        val normalized = query.trim().lowercase()
        return getAllProducts().filter {
            val textMatches = normalized.isBlank() ||
                    it.name.lowercase().contains(normalized) ||
                    it.category.lowercase().contains(normalized) ||
                    it.description.lowercase().contains(normalized)
            val categoryMatches = category.isNullOrBlank() || category == "Todos" || it.category.equals(category, ignoreCase = true)
            val wholesaleMatches = !wholesaleOnly || (it.wholesalePrice != null)
            textMatches && categoryMatches && wholesaleMatches
        }
    }

    fun addProduct(
        name: String,
        category: String,
        description: String,
        unit: String,
        retailPrice: Double,
        wholesalePrice: Double?,
        wholesaleMinimum: Int,
        stock: Int,
        sellerId: String,
        sellerName: String,
        sellerPhone: String,
        sellerPaymentMethods: List<String>,
        sellerPagoMovilData: String,
        sellerBinanceId: String,
        iaCuotasEligible: Boolean = true
    ): Product {
        require(name.isNotBlank()) { "El nombre del artículo es obligatorio." }
        require(category.isNotBlank()) { "La categoría es obligatoria." }
        require(retailPrice > 0) { "El precio al detal debe ser mayor que cero." }
        require(wholesalePrice == null || wholesalePrice > 0) { "El precio al mayor debe ser mayor que cero." }
        require(wholesaleMinimum > 0) { "La cantidad mínima al mayor debe ser al menos 1." }
        require(stock >= 0) { "El inventario no puede ser negativo." }

        val product = Product(
            id = "prod_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            category = category.trim(),
            description = description.trim(),
            unit = unit.trim().ifBlank { "Unidad" },
            price = retailPrice,
            wholesalePrice = wholesalePrice,
            wholesaleMinimum = wholesaleMinimum,
            stock = stock,
            sellerId = sellerId,
            sellerName = sellerName.trim().ifBlank { "Emprendedor Mayorista" },
            sellerPhone = sellerPhone.trim().ifBlank { "0412-1234567" },
            sellerPaymentMethods = if (sellerPaymentMethods.isEmpty()) listOf("iaCuotas", "Pago Móvil", "Binance Pay") else sellerPaymentMethods,
            sellerPagoMovilData = sellerPagoMovilData.trim().ifBlank { "Pago Móvil disponible" },
            sellerBinanceId = sellerBinanceId.trim().ifBlank { "Binance Pay disponible" },
            iaCuotasEligible = iaCuotasEligible,
            iaDownPaymentPercent = 40,
            iaInstallments = 3
        )

        val updated = listOf(product) + getAllProducts()
        saveProducts(updated)
        return product
    }

    fun deleteProduct(productId: String): Boolean {
        val current = getAllProducts().toMutableList()
        val removed = current.removeAll { it.id == productId }
        if (removed) {
            saveProducts(current)
        }
        return removed
    }

    fun clearAllProducts() {
        saveProducts(emptyList())
    }

    companion object {
        private const val KEY_PRODUCTS_CLEAN = "iavnzla_clean_products"
    }
}
