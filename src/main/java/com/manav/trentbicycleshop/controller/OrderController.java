// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import com.manav.trentbicycleshop.service.OrderService;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {
  @InitBinder("order")
  public void bindFields(org.springframework.web.bind.WebDataBinder binder) {
    binder.setAllowedFields("supplier.supplierID");
  }

  private final OrderService orderService;
  private final SupplierRepository supplierRepository;
  private final ProductRepository productRepository;
  private final StatusRepository statusRepository;

  public OrderController(
      OrderService orderService,
      SupplierRepository supplierRepository,
      ProductRepository productRepository,
      StatusRepository statusRepository) {
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
  public String saveOrder(
      @ModelAttribute Order order,
      org.springframework.validation.BindingResult binding,
      @RequestParam(name = "productIds", required = false) List<Integer> productIds,
      @RequestParam Map<String, String> params,
      Model model,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    try {
      if (binding.hasErrors()) throw new IllegalArgumentException("Select a valid supplier.");
      List<OrderDetail> details = new ArrayList<>();
      if (productIds != null)
        for (Integer id : productIds) {
          OrderDetail line = new OrderDetail();
          Product product = new Product();
          product.setProductID(id);
          line.setProduct(product);
          line.setQuantity(SaleController.parseQuantity(params.get("quantities[" + id + "]")));
          details.add(line);
        }
      orderService.saveOrder(order, details);
      redirect.addFlashAttribute(
          "success", "Order created. Receive it when the shipment arrives to update stock.");
      return "redirect:/orders";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("errorMessage", exception.getMessage());
      model.addAttribute("suppliers", supplierRepository.findAll());
      model.addAttribute("products", productRepository.findAll());
      model.addAttribute("selectedProducts", productIds);
      model.addAttribute("quantities", params);
      return "order/form";
    }
  }

  @PostMapping("/updateStatus")
  public String updateOrderStatus(
      @RequestParam Integer orderID,
      @RequestParam Integer statusID,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    try {
      orderService.updateOrderStatus(orderID, statusID);
      redirect.addFlashAttribute("success", "Order status updated.");
    } catch (IllegalArgumentException exception) {
      redirect.addFlashAttribute("errorMessage", exception.getMessage());
    }
    return "redirect:/orders";
  }
}
