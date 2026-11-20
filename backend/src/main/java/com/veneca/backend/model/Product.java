package com.veneca.backend.model;

public class Product {
    private final String id;
    private final String name;
    private final String category;
    private final double retailPrice;
    private final double wholesalePrice;
    private final int stock;

    public Product(String id, String name, String category, double retailPrice, double wholesalePrice, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public double getWholesalePrice() {
        return wholesalePrice;
    }

    public int getStock() {
        return stock;
    }
}
