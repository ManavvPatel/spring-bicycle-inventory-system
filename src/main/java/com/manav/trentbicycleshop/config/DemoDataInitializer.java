package com.manav.trentbicycleshop.config;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("demo")
public class DemoDataInitializer implements CommandLineRunner {
  private final UserRepository users;
  private final CategoryRepository categories;
  private final StatusRepository statuses;
  private final ProductRepository products;
  private final InventoryRepository inventory;
  private final CustomerRepository customers;
  private final SupplierRepository suppliers;
  private final RepairServiceRepository repairs;
  private final PasswordEncoder encoder;

  public DemoDataInitializer(
      UserRepository users,
      CategoryRepository categories,
      StatusRepository statuses,
      ProductRepository products,
      InventoryRepository inventory,
      CustomerRepository customers,
      SupplierRepository suppliers,
      RepairServiceRepository repairs,
      PasswordEncoder encoder) {
    this.users = users;
    this.categories = categories;
    this.statuses = statuses;
    this.products = products;
    this.inventory = inventory;
    this.customers = customers;
    this.suppliers = suppliers;
    this.repairs = repairs;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    DataInitializer.seedReferenceData(statuses, categories);
    for (Role role : Role.values()) {
      String name = role.name().toLowerCase(java.util.Locale.ROOT);
      if (users.findByUsername(name) == null) {
        User user = new User();
        user.setUsername(name);
        user.setRole(role);
        user.setHashedPassword(encoder.encode(name + "123"));
        users.save(user);
      }
    }
    if (products.count() > 0) return;
    var cats = categories.findAll();
    String[][] data = {
      {"Ridgeway 29", "Trent", "1299.00", "8", "0"},
      {"Cityline Commuter", "Trent", "849.00", "3", "0"},
      {"Gravel Explorer", "Northline", "1799.00", "6", "0"},
      {"Urban Helmet", "Northline", "89.00", "14", "2"},
      {"All-weather Light Set", "Beacon", "49.00", "2", "2"},
      {"Performance Pedals", "Velocity", "119.00", "0", "1"},
      {"Trail Gloves", "Northline", "39.00", "18", "3"},
      {"Road Tube 700c", "Velocity", "12.00", "24", "1"}
    };
    for (String[] d : data) {
      Product product = new Product();
      product.setProductName(d[0]);
      product.setBrand(d[1]);
      product.setUnitPrice(new BigDecimal(d[2]));
      product.setCategoryID(cats.get(Integer.parseInt(d[4])).getCategoryID());
      products.save(product);
      Inventory row = new Inventory();
      row.setProduct(product);
      row.setCurrentStock(Integer.parseInt(d[3]));
      row.setLastUpdate(LocalDateTime.now());
      inventory.save(row);
    }
    for (String[] name :
        new String[][] {{"Alex", "Morgan"}, {"Sam", "Chen"}, {"Taylor", "Singh"}}) {
      Customer c = new Customer();
      c.setFirstName(name[0]);
      c.setLastName(name[1]);
      c.setEmail(name[0].toLowerCase() + "@example.com");
      customers.save(c);
    }
    Supplier supplier = new Supplier();
    supplier.setSupplierName("Northline Cycling Supply");
    supplier.setContactRep("Jamie Lee");
    supplier.setEmail("supply@example.com");
    suppliers.save(supplier);
    RepairService repair = new RepairService();
    repair.setServiceName("Essential tune-up");
    repair.setDescription("Brake adjustment, drivetrain check and tire inspection.");
    repair.setCost(new BigDecimal("79.00"));
    repairs.save(repair);
  }
}
