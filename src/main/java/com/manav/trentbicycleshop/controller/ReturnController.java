// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.ProductRepository;
import com.manav.trentbicycleshop.repository.SaleRepository;
import com.manav.trentbicycleshop.service.ReturnService;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/returns")
public class ReturnController {
  @InitBinder("aReturn")
  public void bindFields(org.springframework.web.bind.WebDataBinder binder) {
    binder.setAllowedFields("sale.salesID", "product.productID");
  }

  private final ReturnService returnService;
  private final SaleRepository saleRepository;
  private final ProductRepository productRepository;

  public ReturnController(
      ReturnService returnService,
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
  public String saveReturn(
      @ModelAttribute("aReturn") Return item,
      org.springframework.validation.BindingResult binding,
      Model model,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    try {
      if (binding.hasErrors())
        throw new IllegalArgumentException("Select a valid sale and product.");
      returnService.saveReturn(item);
      redirect.addFlashAttribute(
          "success", "One unit returned. The refund uses the original sale price.");
      return "redirect:/returns";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("errorMessage", exception.getMessage());
      model.addAttribute("sales", saleRepository.findAll());
      model.addAttribute("products", productRepository.findAll());
      return "return/form";
    }
  }

  @GetMapping
  public String listReturns(Model model) {
    model.addAttribute("returns", returnService.getAllReturns());
    return "return/list";
  }
}
