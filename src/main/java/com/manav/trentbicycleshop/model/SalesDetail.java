// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SalesDetails")
public class SalesDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SalesDetailsID")
    private Integer salesDetailsID;

    @ManyToOne
    @JoinColumn(name = "SalesID", referencedColumnName = "SalesID", nullable = false)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "ProductID", referencedColumnName = "ProductID", nullable = false)
    private Product product;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "UnitPrice", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "SubTotal", nullable = false)
    private BigDecimal subTotal;

    // Getters and Setters

    public Integer getSalesDetailsID() {
        return salesDetailsID;
    }

    public void setSalesDetailsID(Integer salesDetailsID) {
        this.salesDetailsID = salesDetailsID;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}