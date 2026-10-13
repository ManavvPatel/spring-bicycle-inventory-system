package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockService {
  private final ProductRepository products;
  private final InventoryRepository inventory;

  public StockService(ProductRepository products, InventoryRepository inventory) {
    this.products = products;
    this.inventory = inventory;
  }

  public Product lockProduct(Integer id) {
    if (id == null) throw new IllegalArgumentException("Select a product.");
    return products
        .lockById(id)
        .orElseThrow(() -> new IllegalArgumentException("This product no longer exists."));
  }

  public void change(Product product, int delta) {
    // All mutations lock the parent product first; missing inventory is also serialized.
    Product locked = lockProduct(product.getProductID());
    var rows = inventory.findAllByProduct(locked);
    if (rows.size() > 1)
      throw new IllegalArgumentException(
          "Multiple inventory records exist for "
              + locked.getProductName()
              + ". Reconcile them before continuing.");
    Inventory row = rows.isEmpty() ? new Inventory() : rows.get(0);
    int current = row.getCurrentStock() == null ? 0 : row.getCurrentStock();
    long next = (long) current + delta;
    if (next < 0)
      throw new IllegalArgumentException(
          "Insufficient stock for " + locked.getProductName() + ". Available: " + current + ".");
    if (next > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Stock quantity is too large.");
    row.setProduct(locked);
    row.setCurrentStock((int) next);
    row.setLastUpdate(LocalDateTime.now());
    inventory.save(row);
  }
}
