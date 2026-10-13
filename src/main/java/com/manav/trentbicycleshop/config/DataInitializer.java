package com.manav.trentbicycleshop.config;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!demo & !test")
public class DataInitializer implements CommandLineRunner {
  private final UserRepository users;
  private final StatusRepository statuses;
  private final CategoryRepository categories;
  private final PasswordEncoder encoder;

  @Value("${app.bootstrap.username}")
  private String username;

  @Value("${app.bootstrap.password}")
  private String password;

  public DataInitializer(
      UserRepository users,
      StatusRepository statuses,
      CategoryRepository categories,
      PasswordEncoder encoder) {
    this.users = users;
    this.statuses = statuses;
    this.categories = categories;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    seedReferenceData(statuses, categories);
    if (users.count() == 0) {
      if (password == null || password.length() < 12) {
        throw new IllegalStateException(
            "Set BOOTSTRAP_PASSWORD to at least 12 characters for the initial owner account.");
      }
      User owner = new User();
      owner.setUsername(username);
      owner.setHashedPassword(encoder.encode(password));
      owner.setRole(Role.Owner);
      users.save(owner);
    }
  }

  static void seedReferenceData(StatusRepository statuses, CategoryRepository categories) {
    for (String name : new String[] {"Shipped", "Received", "Cancelled"}) {
      if (statuses.findByStatusName(name) == null) {
        Status status = new Status();
        status.setStatusName(name);
        statuses.save(status);
      }
    }
    if (categories.count() == 0) {
      for (String name : new String[] {"Bicycles", "Parts", "Accessories", "Apparel"}) {
        Category category = new Category();
        category.setCategoryName(name);
        categories.save(category);
      }
    }
  }
}
