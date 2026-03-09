// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.manav.trentbicycleshop.model.User;
import com.manav.trentbicycleshop.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/list";
    }
    
    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user/form";
    }
    
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer userID) {
        userService.deleteUser(userID);
        return "redirect:/admin/users";
    }
}