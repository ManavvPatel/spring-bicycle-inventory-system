// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ServiceTransactions")
public class ServiceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ServiceTransactionID")
    private Integer serviceTransactionID;

    @ManyToOne
    @JoinColumn(name = "CustomerID", referencedColumnName = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "ServiceID", referencedColumnName = "ServiceID", nullable = false)
    private RepairService repairService;

    @Column(name = "TransactionDate", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    public ServiceTransaction() {
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getServiceTransactionID() {
        return serviceTransactionID;
    }

    public void setServiceTransactionID(Integer serviceTransactionID) {
        this.serviceTransactionID = serviceTransactionID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RepairService getRepairService() {
        return repairService;
    }

    public void setRepairService(RepairService repairService) {
        this.repairService = repairService;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public BigDecimal getTotalCost() {
        return repairService != null ? repairService.getCost() : BigDecimal.ZERO;
    }
}