package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SaleService {
  private final SaleRepository sales;
  private final SalesDetailRepository details;
  private final CustomerRepository customers;
  private final StockService stock;

  public SaleService(
      SaleRepository sales,
      SalesDetailRepository details,
      CustomerRepository customers,
      StockService stock) {
    this.sales = sales;
    this.details = details;
    this.customers = customers;
    this.stock = stock;
  }

  public List<Sale> getAllSales() {
    return sales.findAll(org.springframework.data.domain.Sort.by("saleDate").descending());
  }

  public void saveSale(Sale sale, List<SalesDetail> lines) {
    if (sale.getSalesID() != null)
      throw new IllegalArgumentException("Existing sales cannot be overwritten.");
    if (sale.getCustomer() == null || sale.getCustomer().getCustomerID() == null)
      throw new IllegalArgumentException("Select a customer.");
    sale.setCustomer(
        customers
            .lockById(sale.getCustomer().getCustomerID())
            .orElseThrow(() -> new IllegalArgumentException("Select an existing customer.")));
    if (lines == null || lines.isEmpty())
      throw new IllegalArgumentException("Select at least one product and enter a quantity.");
    Set<Integer> seen = new HashSet<>();
    for (SalesDetail line : lines) {
      if (line.getProduct() == null
          || line.getProduct().getProductID() == null
          || line.getQuantity() == null
          || line.getQuantity() <= 0)
        throw new IllegalArgumentException(
            "Every selected product needs a positive whole-number quantity.");
      if (!seen.add(line.getProduct().getProductID()))
        throw new IllegalArgumentException("Select each product only once.");
    }
    // Stable lock order avoids deadlocks between multi-product transactions.
    lines =
        lines.stream().sorted(Comparator.comparing(l -> l.getProduct().getProductID())).toList();
    BigDecimal total = BigDecimal.ZERO;
    for (SalesDetail line : lines) {
      Product product = stock.lockProduct(line.getProduct().getProductID());
      if (product.getUnitPrice() == null || product.getUnitPrice().signum() < 0)
        throw new IllegalArgumentException("Correct the product price before selling it.");
      stock.change(product, -line.getQuantity());
      line.setProduct(product);
      line.setUnitPrice(product.getUnitPrice());
      line.setSubTotal(product.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())));
      total = total.add(line.getSubTotal());
    }
    sale.setSaleDate(LocalDateTime.now());
    sale.setTotalAmount(total);
    Sale saved = sales.save(sale);
    for (SalesDetail line : lines) {
      line.setSale(saved);
      details.save(line);
    }
  }

  public List<Sale> getSalesBetween(LocalDateTime start, LocalDateTime end) {
    return sales.findBySaleDateGreaterThanEqualAndSaleDateLessThan(start, end);
  }
}
