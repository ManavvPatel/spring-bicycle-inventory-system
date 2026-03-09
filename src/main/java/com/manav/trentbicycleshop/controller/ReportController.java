// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.manav.trentbicycleshop.model.Product;
import com.manav.trentbicycleshop.model.Sale;
import com.manav.trentbicycleshop.model.ServiceTransaction;
import com.manav.trentbicycleshop.repository.ProductRepository;
import com.manav.trentbicycleshop.service.InventoryService;
import com.manav.trentbicycleshop.service.SaleService;
import com.manav.trentbicycleshop.service.ServiceTransactionService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final InventoryService inventoryService;
    private final ProductRepository productRepository;
    private final SaleService saleService;
    private final ServiceTransactionService serviceTransactionService;

    public ReportController(InventoryService inventoryService, ProductRepository productRepository, SaleService saleService, ServiceTransactionService serviceTransactionService) {
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
        this.saleService = saleService;
        this.serviceTransactionService = serviceTransactionService;
    }

    @GetMapping
    public String reportsHome(Model model) {
        return "report/index";  // This should correspond to src/main/resources/templates/report/index.html
    }

    // Report 3: Display inventory details for a selected product.
    @GetMapping("/inventoryByProduct")
    public String showInventoryByProductForm(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "report/inventoryByProductForm";  // Form to select a product.
    }

    @PostMapping("/inventoryByProduct")
    public String showInventoryByProduct(@RequestParam("productId") Integer productId, Model model) {
        model.addAttribute("inventoryDetails", inventoryService.getInventoryForProduct(productId));
        Product product = productRepository.findById(productId).orElse(null);
        model.addAttribute("selectedProduct", product);
        return "report/inventoryByProduct";  // Report for the selected product.
    }

   // Report for overall inventory value
   @GetMapping("/inventoryValue")
   public String showInventoryValue(Model model) {
       model.addAttribute("inventoryDTOs", inventoryService.getInventoryValueDTOList());
       model.addAttribute("totalInventoryValue", inventoryService.getTotalInventoryValue());
       return "report/inventoryValue";
   }

   // Daily Sales Report
    @GetMapping("/sales/daily")
    public String showDailySalesReport(Model model) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Sale> sales = saleService.getSalesBetween(start, end);
        model.addAttribute("sales", sales);
        model.addAttribute("reportTitle", "Daily Sales Report");
        return "report/salesReport";
    }

    // Weekly Sales Report
    @GetMapping("/sales/weekly")
    public String showWeeklySalesReport(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = start.plusDays(7);
        List<Sale> sales = saleService.getSalesBetween(start, end);
        model.addAttribute("sales", sales);
        model.addAttribute("reportTitle", "Weekly Sales Report");
        return "report/salesReport";
    }

    // Monthly Sales Report
    @GetMapping("/sales/monthly")
    public String showMonthlySalesReport(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDateTime start = startOfMonth.atStartOfDay();
        LocalDateTime end = start.plusMonths(1);
        List<Sale> sales = saleService.getSalesBetween(start, end);
        model.addAttribute("sales", sales);
        model.addAttribute("reportTitle", "Monthly Sales Report");
        return "report/salesReport";
    }

    // Weekly Repairs Report
    @GetMapping("/repairs/weekly")
    public String showWeeklyRepairsReport(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = start.plusDays(7);
        List<ServiceTransaction> repairs = serviceTransactionService.getTransactionsBetween(start, end);
        model.addAttribute("repairs", repairs);
        model.addAttribute("reportTitle", "Weekly Repairs Report");
        return "report/repairsReport";
    }
}