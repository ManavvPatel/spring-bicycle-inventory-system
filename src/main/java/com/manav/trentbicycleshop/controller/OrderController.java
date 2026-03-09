// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import com.manav.trentbicycleshop.service.OrderService;

import java.util.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final StatusRepository statusRepository;

    public OrderController(OrderService orderService, SupplierRepository supplierRepository, ProductRepository productRepository, StatusRepository statusRepository) {
        this.orderService = orderService;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.statusRepository = statusRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("orderStatuses", statusRepository.findAll());
        return "order/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("suppliers", supplierRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "order/form";
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order,
                            @RequestParam(name = "productIds", required = false) List<Integer> productIds,
                            @RequestParam Map<String, String> paramMap) {

        List<OrderDetail> details = new ArrayList<>();

        if (productIds != null) {
            for (Integer productId : productIds) {
                String quantityStr = paramMap.get("quantities[" + productId + "]");
                if (quantityStr != null && !quantityStr.isBlank()) {
                    int quantity = Integer.parseInt(quantityStr);

                    OrderDetail detail = new OrderDetail();
                    Product product = new Product();
                    product.setProductID(productId);
                    detail.setProduct(product);
                    detail.setQuantity(quantity);
                    details.add(detail);
                }
            }
        }

        orderService.saveOrder(order, details);
        return "redirect:/orders";
    }

    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam Integer orderID,
                                    @RequestParam Integer statusID) {
        orderService.updateOrderStatus(orderID, statusID);
        return "redirect:/orders";
    }
}