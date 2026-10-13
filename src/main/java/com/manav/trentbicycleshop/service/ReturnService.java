package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.Return;
import com.manav.trentbicycleshop.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReturnService {
  private final ReturnRepository returns;
  private final SaleRepository sales;
  private final SalesDetailRepository details;
  private final StockService stock;

  public ReturnService(
      ReturnRepository returns,
      SaleRepository sales,
      SalesDetailRepository details,
      StockService stock) {
    this.returns = returns;
    this.sales = sales;
    this.details = details;
    this.stock = stock;
  }

  public List<Return> getAllReturns() {
    return returns.findAll();
  }

  // One unit per return, matching the existing schema. Refunds use the historical sale price.
  public void saveReturn(Return item) {
    if (item.getReturnID() != null)
      throw new IllegalArgumentException("Existing returns cannot be overwritten.");
    if (item.getSale() == null
        || item.getSale().getSalesID() == null
        || item.getProduct() == null
        || item.getProduct().getProductID() == null)
      throw new IllegalArgumentException("Select the original sale and returned product.");
    var sale =
        sales
            .lockById(item.getSale().getSalesID())
            .orElseThrow(() -> new IllegalArgumentException("Sale not found."));
    Integer productID = item.getProduct().getProductID();
    var lines =
        details.findBySaleSalesID(sale.getSalesID()).stream()
            .filter(l -> l.getProduct().getProductID().equals(productID))
            .toList();
    long purchased = lines.stream().mapToLong(l -> l.getQuantity()).sum();
    long returned = returns.countBySaleSalesIDAndProductProductID(sale.getSalesID(), productID);
    if (purchased == 0)
      throw new IllegalArgumentException("This product was not included in the selected sale.");
    if (returned >= purchased)
      throw new IllegalArgumentException(
          "All units of this product from that sale have already been returned.");
    // Legacy sales may contain multiple lines for the same product at different prices.
    long offset = returned;
    var originalLine = lines.get(0);
    for (var line : lines) {
      if (offset < line.getQuantity()) {
        originalLine = line;
        break;
      }
      offset -= line.getQuantity();
    }
    item.setSale(sale);
    item.setProduct(originalLine.getProduct());
    item.setRefundAmount(originalLine.getUnitPrice());
    item.setReturnDate(LocalDateTime.now());
    stock.change(item.getProduct(), 1);
    returns.save(item);
  }
}
