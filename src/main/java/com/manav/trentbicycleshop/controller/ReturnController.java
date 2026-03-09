// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.ProductRepository;
import com.manav.trentbicycleshop.repository.SaleRepository;
import com.manav.trentbicycleshop.service.ReturnService;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/returns")
public class ReturnController {

    private final ReturnService returnService;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public ReturnController(ReturnService returnService,
                            SaleRepository saleRepository,
                            ProductRepository productRepository) {
        this.returnService = returnService;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/add")
    public String showReturnForm(Model model) {
        model.addAttribute("aReturn", new Return());
        model.addAttribute("sales", saleRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "return/form";
    }

    @PostMapping("/save")
    public String saveReturn(@ModelAttribute("aReturn") Return aReturn,
                             @RequestParam(name = "productIds", required = false) List<Integer> productIds,
                             @RequestParam Map<String, String> paramMap) {
        // For simplicity, assume one product is returned per record.
        // You can extend this to allow multiple products in one return if needed.
        if (productIds != null && !productIds.isEmpty()) {
            // We'll only handle the first selected product for this example.
            Integer productId = productIds.get(0);
            String refundStr = paramMap.get("refundAmount");
            BigDecimal refund = refundStr != null ? new BigDecimal(refundStr) : BigDecimal.ZERO;
            Product product = new Product();
            product.setProductID(productId);
            aReturn.setProduct(product);
            aReturn.setRefundAmount(refund);
        }
        returnService.saveReturn(aReturn);
        return "redirect:/returns";
    }

    @GetMapping
    public String listReturns(Model model) {
        model.addAttribute("returns", returnService.getAllReturns());
        return "return/list";
    }
}