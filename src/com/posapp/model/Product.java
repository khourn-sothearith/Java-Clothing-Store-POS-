package com.posapp.model;

import java.math.BigDecimal;
public class Product {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private String size;
    private String color;
    private BigDecimal price;
    private int quantity;
    private int lowStockThreshold;

    public Product() {
    }

    public Product(int productId, String productName, int categoryId, String categoryName,
                   String size, String color, BigDecimal price, int quantity, int lowStockThreshold) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
    public boolean isLowStock() {
        return quantity < lowStockThreshold;
    }

    @Override
    public String toString() {
        return productName + " - " + size + " (" + color + ")";
    }
}
