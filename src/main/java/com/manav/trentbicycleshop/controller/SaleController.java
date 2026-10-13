// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.CustomerRepository;
import com.manav.trentbicycleshop.repository.ProductRepository;
import com.manav.trentbicycleshop.service.SaleService;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sales")
public class SaleController {
  @InitBinder("sale")
  public void bindFields(org.springframework.web.bind.WebDataBinder binder) {
    binder.setAllowedFields("customer.customerID");
  }

  private final SaleService saleService;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;

  public SaleController(
      SaleService saleService,
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
  public String saveSale(
      @ModelAttribute Sale sale,
      org.springframework.validation.BindingResult binding,
      @RequestParam(name = "productIds", required = false) List<Integer> productIds,
      @RequestParam Map<String, String> params,
      Model model,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    try {
      if (binding.hasErrors()) throw new IllegalArgumentException("Select a valid customer.");
      List<SalesDetail> details = new ArrayList<>();
      if (productIds != null)
        for (Integer id : productIds) {
          SalesDetail line = new SalesDetail();
          Product product = new Product();
          product.setProductID(id);
          line.setProduct(product);
          line.setQuantity(parseQuantity(params.get("quantities[" + id + "]")));
          details.add(line);
        }
      saleService.saveSale(sale, details);
      redirect.addFlashAttribute("success", "Sale recorded. Stock has been updated.");
      return "redirect:/sales";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("errorMessage", exception.getMessage());
      model.addAttribute("customers", customerRepository.findAll());
      model.addAttribute("products", productRepository.findAll());
      model.addAttribute("selectedProducts", productIds);
      model.addAttribute("quantities", params);
      return "sale/form";
    }
  }

  static int parseQuantity(String value) {
    try {
      int quantity = Integer.parseInt(value);
      if (quantity > 0) return quantity;
    } catch (NumberFormatException ignored) {
    }
    throw new IllegalArgumentException(
        "Enter a positive whole-number quantity for each selected product.");
  }

  @GetMapping
  public String listSales(Model model) {
    model.addAttribute("sales", saleService.getAllSales());
    return "sale/list";
  }
}
