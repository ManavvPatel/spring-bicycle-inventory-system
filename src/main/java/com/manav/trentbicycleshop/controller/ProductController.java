// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.Product;
import com.manav.trentbicycleshop.repository.CategoryRepository;
import com.manav.trentbicycleshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;
  private final CategoryRepository categoryRepository;

  public ProductController(ProductService productService, CategoryRepository categoryRepository) {
    this.productService = productService;
    this.categoryRepository = categoryRepository;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("products", productService.getAll());
    model.addAttribute(
        "categoryNames",
        categoryRepository.findAll().stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    c -> c.getCategoryID(), c -> c.getCategoryName())));
    return "product/list";
  }

  @GetMapping("/add")
  public String showAddForm(Model model) {
    model.addAttribute("product", new Product());
    model.addAttribute("categories", categoryRepository.findAll());
    return "product/form";
  }

  @PostMapping("/save")
  public String save(
      @jakarta.validation.Valid @ModelAttribute("product") Product product,
      org.springframework.validation.BindingResult binding,
      Model model,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    if (binding.hasErrors()) {
      model.addAttribute("categories", categoryRepository.findAll());
      return "product/form";
    }
    redirect.addFlashAttribute("success", "Changes saved successfully.");
    if (product.getCategoryID() == null
        || !categoryRepository.existsById(product.getCategoryID())) {
      binding.rejectValue("categoryID", "invalid", "Select an existing category.");
      model.addAttribute("categories", categoryRepository.findAll());
      return "product/form";
    }
    productService.save(product);
    return "redirect:/products";
  }

  @GetMapping("/edit/{id}")
  public String edit(@PathVariable Integer id, Model model) {
    model.addAttribute("product", productService.get(id));
    model.addAttribute("categories", categoryRepository.findAll());
    return "product/form";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id) {
    productService.delete(id);
    return "redirect:/products";
  }
}
