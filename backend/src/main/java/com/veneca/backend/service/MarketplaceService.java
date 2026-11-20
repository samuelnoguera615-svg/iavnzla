package com.veneca.backend.service;

import com.veneca.backend.model.Product;
import com.veneca.backend.model.SaleRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketplaceService {
    private final List<Product> catalog = List.of(
        new Product("P-1001", "Cargador USB", "Tecnología", 12.50, 9.90, 100),
        new Product("P-1002", "Set de cuchillos", "Hogar", 48.00, 39.00, 35),
        new Product("P-1003", "Botella térmica", "Hogar", 21.00, 17.50, 70),
        new Product("P-1004", "Perfume de autor", "Belleza", 28.00, 22.00, 60),
        new Product("P-1005", "Bolsas de empaque", "Operación", 8.50, 6.40, 200),
        new Product("P-1006", "Kit de limpieza", "Hogar", 26.00, 21.00, 45)
    );

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return catalog;
        }

        String normalized = query.trim().toLowerCase();
        List<Product> result = new ArrayList<>();
        for (Product product : catalog) {
            String text = (product.getName() + " " + product.getCategory()).toLowerCase();
            if (text.contains(normalized)) {
                result.add(product);
            }
        }
        return result;
    }

    public String resolveTier(int totalSales, double monthlyVolume) {
        if (totalSales >= 100 || monthlyVolume >= 3000) {
            return "Platino";
        }
        if (totalSales >= 50 || monthlyVolume >= 1500) {
            return "Oro";
        }
        if (totalSales >= 20 || monthlyVolume >= 700) {
            return "Plata";
        }
        return "Bronce";
    }

    public boolean validateSale(SaleRequest request) {
        if (request == null) {
            return false;
        }

        if (request.getVendorCode() == null || request.getSaleCode() == null) {
            return false;
        }

        String vendorCode = request.getVendorCode().trim();
        String saleCode = request.getSaleCode().trim();

        if (vendorCode.isEmpty() || saleCode.isEmpty()) {
            return false;
        }

        if (request.getQuantity() <= 0) {
            return false;
        }

        boolean validVendor = vendorCode.matches("^[A-Z0-9-]{4,12}$");
        boolean validSale = saleCode.matches("^[A-Z0-9-]{6,20}$");

        return validVendor && validSale && isProductAvailable(request.getProductId());
    }

    public Map<String, Object> registerSale(SaleRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("valid", validateSale(request));
        payload.put("message", validateSale(request) ? "Venta registrada para sincronización" : "Venta inválida o producto no disponible");
        payload.put("tier", resolveTier(28, 1200));
        payload.put("saleCode", request != null ? request.getSaleCode() : "");
        payload.put("vendorCode", request != null ? request.getVendorCode() : "");
        return payload;
    }

    private boolean isProductAvailable(String productId) {
        for (Product product : catalog) {
            if (product.getId().equalsIgnoreCase(productId)) {
                return product.getStock() > 0;
            }
        }
        return false;
    }
}
