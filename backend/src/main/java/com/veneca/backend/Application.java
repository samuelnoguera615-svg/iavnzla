package com.veneca.backend;

import com.veneca.backend.controller.MarketplaceController;
import com.veneca.backend.model.Product;
import com.veneca.backend.model.SaleRequest;

import java.util.List;
import java.util.Map;

public class Application {
    public static void main(String[] args) {
        MarketplaceController controller = new MarketplaceController();

        List<Product> searchResult = controller.search("cargador");
        System.out.println("Resultados de búsqueda: " + searchResult.size());

        SaleRequest sale = new SaleRequest("VEND-100", "SALE-2026-XYZ", "P-1001", 2, "Mercado Libre");
        Map<String, Object> response = controller.sell(sale);
        System.out.println("Resultado de venta: " + response);

        System.out.println("Nivel: " + controller.levelFor(65, 2100));
    }
}
