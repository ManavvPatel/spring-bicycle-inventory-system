// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.dto;

import java.math.BigDecimal;

public class InventoryValueDTO {

    private Integer productID;
    private String productName;
    private Integer currentStock;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;

    public InventoryValueDTO(Integer productID, String productName, Integer currentStock, BigDecimal unitPrice, BigDecimal totalValue) {
        this.productID = productID;
        this.productName = productName;
        this.currentStock = currentStock;
        this.unitPrice = unitPrice;
        this.totalValue = totalValue;
    }

    // Getters and Setters

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}