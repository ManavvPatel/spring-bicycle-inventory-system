// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.Supplier;
import com.manav.trentbicycleshop.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

  private final SupplierService supplierService;

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("suppliers", supplierService.getAll());
    return "supplier/list";
  }

  @GetMapping("/add")
  public String showAddForm(Model model) {
    model.addAttribute("supplier", new Supplier());
    return "supplier/form";
  }

  @PostMapping("/save")
  public String save(
      @jakarta.validation.Valid @ModelAttribute("supplier") Supplier supplier,
      org.springframework.validation.BindingResult binding,
      Model model,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    if (binding.hasErrors()) {
      return "supplier/form";
    }
    redirect.addFlashAttribute("success", "Changes saved successfully.");
    supplierService.save(supplier);
    return "redirect:/suppliers";
  }

  @GetMapping("/edit/{id}")
  public String edit(@PathVariable Integer id, Model model) {
    model.addAttribute("supplier", supplierService.get(id));
    return "supplier/form";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id) {
    supplierService.delete(id);
    return "redirect:/suppliers";
  }
}
