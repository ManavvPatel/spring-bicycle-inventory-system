// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Customer")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CustomerID")
  private Integer customerID;

  @Column(name = "FirstName")
  @NotBlank
  @Size(max = 50)
  private String firstName;

  @Column(name = "LastName")
  @NotBlank
  @Size(max = 50)
  private String lastName;

  @Column(name = "Phone")
  @Size(max = 30)
  private String phone;

  @Column(name = "Email")
  @Email
  @Size(max = 100)
  private String email;

  // Getters and Setters

  public Integer getCustomerID() {
    return customerID;
  }

  public void setCustomerID(Integer customerID) {
    this.customerID = customerID;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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
