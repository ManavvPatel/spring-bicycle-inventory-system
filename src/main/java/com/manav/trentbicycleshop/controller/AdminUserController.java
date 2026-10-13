// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.controller;

import com.manav.trentbicycleshop.model.User;
import com.manav.trentbicycleshop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

  private boolean isOwner() {
    return org.springframework.security.core.context.SecurityContextHolder.getContext()
        .getAuthentication()
        .getAuthorities()
        .stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_Owner"));
  }

  @InitBinder("user")
  public void bindFields(org.springframework.web.bind.WebDataBinder binder) {
    binder.setAllowedFields("username", "hashedPassword", "role");
  }

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
  public String saveUser(@ModelAttribute User user, java.security.Principal principal) {
    if (user.getUsername() == null
        || user.getUsername().isBlank()
        || user.getUsername().length() > 50
        || user.getRole() == null
        || user.getHashedPassword() == null
        || user.getHashedPassword().length() < 12)
      throw new IllegalArgumentException(
          "Enter a username, role and password of at least 12 characters.");
    user.setUserID(null);
    if (user.getRole() == com.manav.trentbicycleshop.model.Role.Owner && !isOwner())
      throw new org.springframework.security.access.AccessDeniedException(
          "Only owners can create owners.");
    user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
    userService.saveUser(user);
    return "redirect:/admin/users";
  }

  @PostMapping("/delete/{id}")
  public String deleteUser(@PathVariable("id") Integer userID, java.security.Principal principal) {
    User target = userService.getUser(userID);
    if (target.getUsername().equals(principal.getName()))
      throw new IllegalArgumentException("You cannot delete your own account.");
    if (target.getRole() == com.manav.trentbicycleshop.model.Role.Owner)
      throw new IllegalArgumentException("Owner accounts cannot be deleted here.");
    userService.deleteUser(userID);
    return "redirect:/admin/users";
  }
}
