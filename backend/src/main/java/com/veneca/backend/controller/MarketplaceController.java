package com.veneca.backend.controller;

import com.veneca.backend.model.Product;
import com.veneca.backend.model.SaleRequest;
import com.veneca.backend.service.MarketplaceService;

import java.util.List;
import java.util.Map;

public class MarketplaceController {
    private final MarketplaceService service = new MarketplaceService();

    public List<Product> search(String query) {
        return service.searchProducts(query);
    }

    public Map<String, Object> sell(SaleRequest request) {
        return service.registerSale(request);
    }

    public String levelFor(int totalSales, double monthlyVolume) {
        return service.resolveTier(totalSales, monthlyVolume);
    }
}
