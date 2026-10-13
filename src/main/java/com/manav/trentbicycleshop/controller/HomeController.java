package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.Sale;
import com.manav.trentbicycleshop.repository.*;
import com.manav.trentbicycleshop.service.InventoryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  private final InventoryService inventory;
  private final CustomerRepository customers;
  private final SaleRepository sales;

  public HomeController(
      InventoryService inventory, CustomerRepository customers, SaleRepository sales) {
    this.inventory = inventory;
    this.customers = customers;
    this.sales = sales;
  }

  @GetMapping({"/", "/home"})
  public String home(Model model) {
    var rows = inventory.getFullInventoryList();
    var today = LocalDate.now().atStartOfDay();
    var todaySales =
        sales.findBySaleDateGreaterThanEqualAndSaleDateLessThan(today, today.plusDays(1));
    model.addAttribute("productCount", rows.size());
    model.addAttribute(
        "stockCount",
        rows.stream().mapToLong(i -> i.getCurrentStock() == null ? 0 : i.getCurrentStock()).sum());
    model.addAttribute(
        "lowStock",
        rows.stream()
            .filter(i -> i.getCurrentStock() == null || i.getCurrentStock() <= 5)
            .toList());
    model.addAttribute("inventoryValue", inventory.getTotalInventoryValue());
    model.addAttribute("customerCount", customers.count());
    model.addAttribute(
        "todayRevenue",
        todaySales.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    model.addAttribute("todaySalesCount", todaySales.size());
    model.addAttribute("recentSales", sales.findTop5ByOrderBySaleDateDesc());
    return "index";
  }
}
