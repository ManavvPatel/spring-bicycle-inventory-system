package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.ServiceTransaction;
import com.manav.trentbicycleshop.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceTransactionService {
  private final ServiceTransactionRepository transactions;
  private final CustomerRepository customers;
  private final RepairServiceRepository repairs;

  public ServiceTransactionService(
      ServiceTransactionRepository transactions,
      CustomerRepository customers,
      RepairServiceRepository repairs) {
    this.transactions = transactions;
    this.customers = customers;
    this.repairs = repairs;
  }

  public List<ServiceTransaction> getAllTransactions() {
    return transactions.findAll();
  }

  public void saveTransaction(ServiceTransaction transaction) {
    if (transaction.getServiceTransactionID() != null)
      throw new IllegalArgumentException("Existing service records cannot be overwritten.");
    if (transaction.getCustomer() == null
        || transaction.getCustomer().getCustomerID() == null
        || transaction.getRepairService() == null
        || transaction.getRepairService().getServiceID() == null)
      throw new IllegalArgumentException("Select a customer and repair service.");
    transaction.setCustomer(
        customers
            .lockById(transaction.getCustomer().getCustomerID())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found.")));
    transaction.setRepairService(
        repairs
            .lockById(transaction.getRepairService().getServiceID())
            .orElseThrow(() -> new IllegalArgumentException("Repair service not found.")));
    transaction.setTransactionDate(LocalDateTime.now());
    transactions.save(transaction);
  }

  public List<ServiceTransaction> getTransactionsBetween(LocalDateTime start, LocalDateTime end) {
    return transactions.findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(start, end);
  }
}
