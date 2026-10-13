package com.manav.trentbicycleshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ViewAdvice {
  private final Environment environment;

  public ViewAdvice(Environment environment) {
    this.environment = environment;
  }

  @ModelAttribute("currentPath")
  public String currentPath(HttpServletRequest request) {
    return request.getRequestURI().substring(request.getContextPath().length());
  }

  @ModelAttribute("demoMode")
  public boolean demoMode() {
    return environment.acceptsProfiles(Profiles.of("demo"));
  }
}
