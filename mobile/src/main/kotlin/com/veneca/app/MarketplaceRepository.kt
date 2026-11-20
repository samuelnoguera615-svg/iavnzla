package com.veneca.app

class MarketplaceRepository {
    private val catalog = listOf(
        Product("P-1001", "Cargador USB", "Tecnología", 12.5, true, 100, "S-01"),
        Product("P-1002", "Set de cuchillos", "Hogar", 48.0, true, 35, "S-02"),
        Product("P-1003", "Botella térmica", "Hogar", 21.0, true, 70, "S-03"),
        Product("P-1004", "Perfume de autor", "Belleza", 28.0, true, 60, "S-01"),
        Product("P-1005", "Bolsas de empaque", "Operación", 8.5, false, 200, "S-04"),
        Product("P-1006", "Kit de limpieza", "Hogar", 26.0, true, 45, "S-02")
    )

    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) {
            return catalog
        }

        val normalized = query.trim().lowercase()
        return catalog.filter {
            val text = "${it.name} ${it.category}".lowercase()
            text.contains(normalized)
        }
    }
}
