package com.manav.trentbicycleshop.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FormErrorAdvice {
  @ExceptionHandler(IllegalArgumentException.class)
  public ModelAndView invalid(IllegalArgumentException exception) {
    return error(HttpStatus.BAD_REQUEST, "Please check your entry", exception.getMessage());
  }

  @ExceptionHandler(BindException.class)
  public ModelAndView binding(BindException exception) {
    return error(
        HttpStatus.BAD_REQUEST,
        "Please check your entry",
        "Some fields are missing or invalid. Go back to review the form.");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ModelAndView integrity(DataIntegrityViolationException exception) {
    return error(
        HttpStatus.CONFLICT,
        "This change could not be saved",
        "The record may already exist or be linked to a sale, order or service. Keep linked records"
            + " to preserve your shop history.");
  }

  private ModelAndView error(HttpStatus status, String title, String message) {
    var view = new ModelAndView("error");
    view.setStatus(status);
    view.addObject("errorTitle", title);
    view.addObject("errorMessage", message);
    return view;
  }
}
