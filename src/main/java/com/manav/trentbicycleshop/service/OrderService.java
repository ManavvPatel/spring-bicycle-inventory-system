package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
  private final OrderRepository orders;
  private final OrderDetailRepository details;
  private final StatusRepository statuses;
  private final SupplierRepository suppliers;
  private final StockService stock;

  public OrderService(
      OrderRepository orders,
      OrderDetailRepository details,
      StatusRepository statuses,
      SupplierRepository suppliers,
      StockService stock) {
    this.orders = orders;
    this.details = details;
    this.statuses = statuses;
    this.suppliers = suppliers;
    this.stock = stock;
  }

  public List<Order> getAllOrders() {
    return orders.findAll(org.springframework.data.domain.Sort.by("orderDate").descending());
  }

  public Order getOrder(Integer id) {
    return orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found."));
  }

  public void saveOrder(Order order, List<OrderDetail> lines) {
    if (order.getOrderID() != null)
      throw new IllegalArgumentException("Existing orders cannot be overwritten.");
    if (order.getSupplier() == null || order.getSupplier().getSupplierID() == null)
      throw new IllegalArgumentException("Select a supplier.");
    order.setSupplier(
        suppliers
            .lockById(order.getSupplier().getSupplierID())
            .orElseThrow(() -> new IllegalArgumentException("Select an existing supplier.")));
    if (lines == null || lines.isEmpty())
      throw new IllegalArgumentException("Select at least one product and enter a quantity.");
    Set<Integer> seen = new HashSet<>();
    for (OrderDetail line : lines) {
      if (line.getProduct() == null
          || line.getProduct().getProductID() == null
          || line.getQuantity() == null
          || line.getQuantity() <= 0)
        throw new IllegalArgumentException(
            "Every selected product needs a positive whole-number quantity.");
      if (!seen.add(line.getProduct().getProductID()))
        throw new IllegalArgumentException("Select each product only once.");
    }
    lines =
        lines.stream().sorted(Comparator.comparing(l -> l.getProduct().getProductID())).toList();
    for (OrderDetail line : lines)
      line.setProduct(stock.lockProduct(line.getProduct().getProductID()));
    Status shipped = statuses.findByStatusName("Shipped");
    if (shipped == null)
      throw new IllegalArgumentException("Order statuses have not been configured.");
    order.setOrderDate(LocalDateTime.now());
    order.setStatus(shipped);
    Order saved = orders.save(order);
    for (OrderDetail line : lines) {
      line.setOrder(saved);
      details.save(line);
    }
  }

  public void updateOrderStatus(Integer orderID, Integer statusID) {
    Order order =
        orders
            .lockById(orderID)
            .orElseThrow(() -> new IllegalArgumentException("Order not found."));
    Status next =
        statuses
            .findById(statusID)
            .orElseThrow(() -> new IllegalArgumentException("Status not found."));
    String current = order.getStatus() == null ? "" : order.getStatus().getStatusName();
    if (current.equals(next.getStatusName())) return;
    if (!"Shipped".equals(current)
        || !Set.of("Received", "Cancelled").contains(next.getStatusName()))
      throw new IllegalArgumentException(
          "Only shipped orders can be received or cancelled. Completed orders cannot be reopened.");
    if ("Received".equals(next.getStatusName())) {
      var lines =
          details.findByOrderOrderID(orderID).stream()
              .sorted(Comparator.comparing(l -> l.getProduct().getProductID()))
              .toList();
      if (lines.isEmpty())
        throw new IllegalArgumentException("This order has no products to receive.");
      for (OrderDetail line : lines) {
        if (line.getQuantity() == null || line.getQuantity() <= 0)
          throw new IllegalArgumentException("This order contains an invalid quantity.");
        stock.change(line.getProduct(), line.getQuantity());
      }
    }
    order.setStatus(next);
    orders.save(order);
  }
}
