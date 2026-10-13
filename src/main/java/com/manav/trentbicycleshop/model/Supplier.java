// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Supplier")
public class Supplier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "SupplierID")
  private Integer supplierID;

  @Column(name = "SupplierName", nullable = false)
  @NotBlank
  @Size(max = 100)
  private String supplierName;

  @Column(name = "ContactRep")
  @Size(max = 100)
  private String contactRep;

  @Column(name = "Phone")
  @Size(max = 30)
  private String phone;

  @Column(name = "Email")
  @Email
  @Size(max = 100)
  private String email;

  // Getters and Setters
  public Integer getSupplierID() {
    return supplierID;
  }

  public void setSupplierID(Integer supplierID) {
    this.supplierID = supplierID;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getContactRep() {
    return contactRep;
  }

  public void setContactRep(String contactRep) {
    this.contactRep = contactRep;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
