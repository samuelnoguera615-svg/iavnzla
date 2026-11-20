package com.veneca.backend.model;

public class SaleRequest {
    private String vendorCode;
    private String saleCode;
    private String productId;
    private int quantity;
    private String market;

    public SaleRequest() {
    }

    public SaleRequest(String vendorCode, String saleCode, String productId, int quantity, String market) {
        this.vendorCode = vendorCode;
        this.saleCode = saleCode;
        this.productId = productId;
        this.quantity = quantity;
        this.market = market;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
