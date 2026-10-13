// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ProductID")
  private Integer productID;

  @Column(name = "ProductName", nullable = false)
  @NotBlank
  @Size(max = 100)
  private String productName;

  @Column(name = "CategoryID")
  private Integer categoryID;

  @Column(name = "Brand")
  @Size(max = 100)
  private String brand;

  @Column(name = "UnitPrice", nullable = false)
  @NotNull
  @DecimalMin("0.00")
  @Digits(integer = 8, fraction = 2)
  private BigDecimal unitPrice;

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

  public Integer getCategoryID() {
    return categoryID;
  }

  public void setCategoryID(Integer categoryID) {
    this.categoryID = categoryID;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }
}
