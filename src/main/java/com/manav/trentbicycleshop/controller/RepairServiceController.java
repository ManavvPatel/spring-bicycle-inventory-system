// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.manav.trentbicycleshop.model.RepairService;
import com.manav.trentbicycleshop.service.RepairServiceService;

@Controller
@RequestMapping("/repairServices")
public class RepairServiceController {

    private final RepairServiceService repairServiceService;

    public RepairServiceController(RepairServiceService repairServiceService) {
        this.repairServiceService = repairServiceService;
    }

    @GetMapping
    public String listServices(Model model) {
        model.addAttribute("services", repairServiceService.getAllServices());
        return "repairService/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("service", new RepairService());
        return "repairService/form";
    }

    @PostMapping("/save")
    public String saveService(@ModelAttribute("service") RepairService service) {
        repairServiceService.saveService(service);
        return "redirect:/repairServices";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        RepairService service = repairServiceService.getServiceById(id);
        model.addAttribute("service", service);
        return "repairService/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Integer id) {
        repairServiceService.deleteService(id);
        return "redirect:/repairServices";
    }
}