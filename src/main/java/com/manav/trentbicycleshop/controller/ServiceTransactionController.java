// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.manav.trentbicycleshop.model.ServiceTransaction;
import com.manav.trentbicycleshop.repository.CustomerRepository;
import com.manav.trentbicycleshop.repository.RepairServiceRepository;
import com.manav.trentbicycleshop.service.ServiceTransactionService;

@Controller
@RequestMapping("/serviceTransactions")
public class ServiceTransactionController {

    private final ServiceTransactionService serviceTransactionService;
    private final CustomerRepository customerRepository;
    private final RepairServiceRepository repairServiceRepository;

    public ServiceTransactionController(ServiceTransactionService serviceTransactionService,
                                        CustomerRepository customerRepository,
                                        RepairServiceRepository repairServiceRepository) {
        this.serviceTransactionService = serviceTransactionService;
        this.customerRepository = customerRepository;
        this.repairServiceRepository = repairServiceRepository;
    }

    @GetMapping
    public String listTransactions(Model model) {
        model.addAttribute("transactions", serviceTransactionService.getAllTransactions());
        return "serviceTransaction/list";
    }

    @GetMapping("/add")
    public String showTransactionForm(Model model) {
        model.addAttribute("transaction", new ServiceTransaction());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("repairServices", repairServiceRepository.findAll());
        return "serviceTransaction/form";
    }

    @PostMapping("/save")
    public String saveTransaction(@ModelAttribute("transaction") ServiceTransaction transaction) {
        serviceTransactionService.saveTransaction(transaction);
        return "redirect:/serviceTransactions";
    }
}