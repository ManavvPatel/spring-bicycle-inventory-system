// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.CustomerRepository;
import com.manav.trentbicycleshop.repository.ProductRepository;
import com.manav.trentbicycleshop.service.SaleService;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public SaleController(SaleService saleService,
                          CustomerRepository customerRepository,
                          ProductRepository productRepository) {
        this.saleService = saleService;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/add")
    public String showSaleForm(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "sale/form";
    }

    @PostMapping("/save")
    public String saveSale(@ModelAttribute Sale sale,
                        @RequestParam(name = "productIds", required = false) List<Integer> productIds,
                        @RequestParam Map<String, String> paramMap) {
        List<SalesDetail> details = new ArrayList<>();
        if (productIds != null) {
            for (Integer productId : productIds) {
                String qtyStr = paramMap.get("quantities[" + productId + "]");
                if (qtyStr != null && !qtyStr.isBlank()) {
                    int quantity = Integer.parseInt(qtyStr);
                    SalesDetail detail = new SalesDetail();

                    // Retrieve the product from the repository
                    Product product = productRepository.findById(productId).orElse(null);
                    if (product != null) {
                        detail.setProduct(product);
                        detail.setUnitPrice(product.getUnitPrice());
                        detail.setQuantity(quantity);
                        detail.setSubTotal(product.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
                        details.add(detail);
                    }
                }
            }
        }
        saleService.saveSale(sale, details);
        return "redirect:/sales";
    }

    @GetMapping
    public String listSales(Model model) {
        model.addAttribute("sales", saleService.getAllSales());
        return "sale/list";
    }
}