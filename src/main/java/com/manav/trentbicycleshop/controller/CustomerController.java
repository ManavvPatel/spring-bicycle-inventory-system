// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.Customer;
import com.manav.trentbicycleshop.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/customers")
  public String showCustomerList(Model model) {
    model.addAttribute("customers", customerService.getAll());
    return "customer/list";
  }

  @GetMapping("/customers/add")
  public String showAddForm(Model model) {
    model.addAttribute("customer", new Customer());
    return "customer/form";
  }

  @PostMapping("/customers/save")
  public String saveCustomer(
      @jakarta.validation.Valid @ModelAttribute("customer") Customer customer,
      org.springframework.validation.BindingResult binding,
      Model model,
      org.springframework.web.servlet.mvc.support.RedirectAttributes redirect) {
    if (binding.hasErrors()) {
      return "customer/form";
    }
    redirect.addFlashAttribute("success", "Changes saved successfully.");
    customerService.save(customer);
    return "redirect:/customers";
  }

  @GetMapping("/customers/edit/{id}")
  public String editCustomer(@PathVariable Integer id, Model model) {
    model.addAttribute("customer", customerService.get(id));
    return "customer/form";
  }

  @PostMapping("/customers/delete/{id}")
  public String deleteCustomer(@PathVariable Integer id) {
    customerService.delete(id);
    return "redirect:/customers";
  }
}
