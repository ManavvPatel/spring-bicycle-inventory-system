package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Protects historical records even on legacy schemas with ON DELETE CASCADE. */
@Service
@Transactional(propagation = Propagation.MANDATORY)
public class DeletionGuard {
  private final ProductRepository products;
  private final CustomerRepository customers;
  private final SupplierRepository suppliers;
  private final RepairServiceRepository repairs;
  private final InventoryRepository inventory;
  private final SaleRepository sales;
  private final SalesDetailRepository saleDetails;
  private final OrderRepository orders;
  private final OrderDetailRepository orderDetails;
  private final ReturnRepository returns;
  private final ServiceTransactionRepository services;

  public DeletionGuard(
      ProductRepository products,
      CustomerRepository customers,
      SupplierRepository suppliers,
      RepairServiceRepository repairs,
      InventoryRepository inventory,
      SaleRepository sales,
      SalesDetailRepository saleDetails,
      OrderRepository orders,
      OrderDetailRepository orderDetails,
      ReturnRepository returns,
      ServiceTransactionRepository services) {
    this.products = products;
    this.customers = customers;
    this.suppliers = suppliers;
    this.repairs = repairs;
    this.inventory = inventory;
    this.sales = sales;
    this.saleDetails = saleDetails;
    this.orders = orders;
    this.orderDetails = orderDetails;
    this.returns = returns;
    this.services = services;
  }

  public void product(Integer id) {
    products.lockById(id).orElseThrow(() -> new IllegalArgumentException("Product not found."));
    reject(
        inventory.existsByProductProductID(id)
            || saleDetails.existsByProductProductID(id)
            || orderDetails.existsByProductProductID(id)
            || returns.existsByProductProductID(id));
  }

  public void customer(Integer id) {
    customers.lockById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found."));
    reject(sales.existsByCustomerCustomerID(id) || services.existsByCustomerCustomerID(id));
  }

  public void supplier(Integer id) {
    suppliers.lockById(id).orElseThrow(() -> new IllegalArgumentException("Supplier not found."));
    reject(orders.existsBySupplierSupplierID(id));
  }

  public void repair(Integer id) {
    repairs
        .lockById(id)
        .orElseThrow(() -> new IllegalArgumentException("Repair service not found."));
    reject(services.existsByRepairServiceServiceID(id));
  }

  private void reject(boolean linked) {
    if (linked)
      throw new IllegalArgumentException(
          "This record is linked to inventory or transaction history and cannot be deleted.");
  }
}
