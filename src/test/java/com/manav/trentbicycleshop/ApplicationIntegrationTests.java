package com.manav.trentbicycleshop;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.model.Return;
import com.manav.trentbicycleshop.repository.*;
import com.manav.trentbicycleshop.service.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class ApplicationIntegrationTests {
  @Autowired MockMvc mvc;
  @Autowired ProductRepository products;
  @Autowired ProductService productService;
  @Autowired CustomerService customerService;
  @Autowired SupplierService supplierService;
  @Autowired InventoryRepository inventory;
  @Autowired CustomerRepository customers;
  @Autowired SupplierRepository suppliers;
  @Autowired CategoryRepository categories;
  @Autowired StatusRepository statuses;
  @Autowired SaleRepository sales;
  @Autowired SalesDetailRepository saleDetails;
  @Autowired ReturnRepository returns;
  @Autowired OrderRepository orders;
  @Autowired UserRepository users;
  @Autowired SaleService saleService;
  @Autowired OrderService orderService;
  @Autowired ReturnService returnService;
  Product product;
  Customer customer;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setProductName("Test bicycle " + UUID.randomUUID());
    product.setBrand("Trent");
    product.setCategoryID(categories.findAll().get(0).getCategoryID());
    product.setUnitPrice(new BigDecimal("100.00"));
    products.save(product);
    Inventory row = new Inventory();
    row.setProduct(product);
    row.setCurrentStock(5);
    row.setLastUpdate(LocalDateTime.now());
    inventory.save(row);
    customer = customers.findAll().get(0);
  }

  int stock() {
    return inventory.findAllByProduct(product).get(0).getCurrentStock();
  }

  Sale newSale() {
    Sale sale = new Sale();
    sale.setCustomer(customer);
    return sale;
  }

  SalesDetail line(Product p, int quantity) {
    SalesDetail line = new SalesDetail();
    line.setProduct(p);
    line.setQuantity(quantity);
    line.setUnitPrice(new BigDecimal("0.01"));
    return line;
  }

  Return returned(Sale sale, Product p) {
    Return item = new Return();
    item.setSale(sale);
    item.setProduct(p);
    item.setRefundAmount(new BigDecimal("9999"));
    return item;
  }

  @Test
  void publicLoginAndAssetsWorkAndAnonymousUsersRedirect() throws Exception {
    mvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Welcome back.")));
    mvc.perform(get("/css/app.css")).andExpect(status().isOk());
    mvc.perform(get("/js/app.js")).andExpect(status().isOk());
    mvc.perform(get("/products")).andExpect(status().is3xxRedirection());
  }

  @Test
  void seededOwnerCanSignIn() throws Exception {
    mvc.perform(
            post("/login").with(csrf()).param("username", "owner").param("password", "owner123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/home"));
  }

  @Test
  void everyOwnerScreenRendersIncludingPopulatedReports() throws Exception {
    Sale sale = newSale();
    saleService.saveSale(sale, List.of(line(product, 1)));
    returnService.saveReturn(returned(sale, product));
    Order order = new Order();
    order.setSupplier(suppliers.findAll().get(0));
    OrderDetail detail = new OrderDetail();
    detail.setProduct(product);
    detail.setQuantity(1);
    orderService.saveOrder(order, List.of(detail));
    String[] routes = {
      "/",
      "/home",
      "/products",
      "/products/add",
      "/products/edit/" + product.getProductID(),
      "/inventory",
      "/customers",
      "/customers/add",
      "/customers/edit/" + customer.getCustomerID(),
      "/sales",
      "/sales/add",
      "/returns",
      "/returns/add",
      "/suppliers",
      "/suppliers/add",
      "/orders",
      "/orders/add",
      "/repairServices",
      "/repairServices/add",
      "/serviceTransactions",
      "/serviceTransactions/add",
      "/reports",
      "/reports/inventoryValue",
      "/reports/inventoryByProduct",
      "/reports/sales/daily",
      "/reports/sales/weekly",
      "/reports/sales/monthly",
      "/reports/repairs/weekly",
      "/admin/users",
      "/admin/users/add"
    };
    for (String route : routes)
      mvc.perform(get(route).with(user("owner").roles("Owner"))).andExpect(status().isOk());
    mvc.perform(
            post("/reports/inventoryByProduct")
                .with(user("owner").roles("Owner"))
                .with(csrf())
                .param("productId", product.getProductID().toString()))
        .andExpect(status().isOk());
  }

  @Test
  void employeeCannotSeeOrOpenOwnerAndAdminModules() throws Exception {
    for (String route : List.of("/suppliers", "/orders", "/admin/users"))
      mvc.perform(get(route).with(user("employee").roles("Employee")))
          .andExpect(status().isForbidden());
    mvc.perform(get("/home").with(user("employee").roles("Employee")))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("href=\"/suppliers\""))))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("href=\"/admin/users\""))));
    mvc.perform(get("/orders").with(user("admin").roles("Admin")))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteRequiresPostAndCsrf() throws Exception {
    mvc.perform(
            get("/products/delete/" + product.getProductID()).with(user("owner").roles("Owner")))
        .andExpect(status().isMethodNotAllowed());
    mvc.perform(
            post("/products/delete/" + product.getProductID()).with(user("owner").roles("Owner")))
        .andExpect(status().isForbidden());
    assertThat(products.existsById(product.getProductID())).isTrue();
  }

  @Test
  void productValidationKeepsFormAndDoesNotSaveBadPrice() throws Exception {
    long before = products.count();
    mvc.perform(
            post("/products/save")
                .with(user("owner").roles("Owner"))
                .with(csrf())
                .param("productName", "Invalid")
                .param("unitPrice", "-1")
                .param("categoryID", product.getCategoryID().toString()))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("product", "unitPrice"))
        .andExpect(view().name("product/form"));
    assertThat(products.count()).isEqualTo(before);
  }

  @Test
  void saleUsesCataloguePriceAndUpdatesStock() {
    Sale sale = newSale();
    saleService.saveSale(sale, List.of(line(product, 2)));
    assertThat(stock()).isEqualTo(3);
    assertThat(sales.findById(sale.getSalesID()).orElseThrow().getTotalAmount())
        .isEqualByComparingTo("200.00");
  }

  @Test
  void emptyNegativeAndOversoldSalesAreRejected() {
    long before = sales.count();
    assertThatThrownBy(() -> saleService.saveSale(newSale(), List.of()))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> saleService.saveSale(newSale(), List.of(line(product, -1))))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> saleService.saveSale(newSale(), List.of(line(product, 6))))
        .hasMessageContaining("Insufficient stock");
    assertThat(stock()).isEqualTo(5);
    assertThat(sales.count()).isEqualTo(before);
  }

  @Test
  void laterLineFailureRollsBackEarlierStockChange() {
    Product unavailable = new Product();
    unavailable.setProductName("Unavailable");
    unavailable.setUnitPrice(BigDecimal.TEN);
    products.save(unavailable);
    long before = sales.count();
    assertThatThrownBy(
            () -> saleService.saveSale(newSale(), List.of(line(product, 2), line(unavailable, 1))))
        .hasMessageContaining("Insufficient stock");
    assertThat(stock()).isEqualTo(5);
    assertThat(sales.count()).isEqualTo(before);
  }

  @Test
  void parallelSalesCannotOversell() throws Exception {
    var pool = Executors.newFixedThreadPool(2);
    var start = new CountDownLatch(1);
    var buyerIndex = new java.util.concurrent.atomic.AtomicInteger();
    var buyers = customers.findAll();
    Callable<Boolean> buy =
        () -> {
          start.await();
          try {
            Sale concurrentSale = new Sale();
            concurrentSale.setCustomer(buyers.get(buyerIndex.getAndIncrement()));
            saleService.saveSale(concurrentSale, List.of(line(product, 4)));
            return true;
          } catch (IllegalArgumentException exception) {
            return false;
          }
        };
    try {
      var one = pool.submit(buy);
      var two = pool.submit(buy);
      start.countDown();
      int successes =
          (one.get(10, TimeUnit.SECONDS) ? 1 : 0) + (two.get(10, TimeUnit.SECONDS) ? 1 : 0);
      assertThat(successes).isEqualTo(1);
      assertThat(stock()).isEqualTo(1);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void receivingAnOrderIsIdempotentAndCannotBeReopened() {
    Order order = new Order();
    order.setSupplier(suppliers.findAll().get(0));
    OrderDetail line = new OrderDetail();
    line.setProduct(product);
    line.setQuantity(3);
    orderService.saveOrder(order, List.of(line));
    assertThat(stock()).isEqualTo(5);
    int received = statuses.findByStatusName("Received").getStatusID();
    orderService.updateOrderStatus(order.getOrderID(), received);
    orderService.updateOrderStatus(order.getOrderID(), received);
    assertThat(stock()).isEqualTo(8);
    assertThatThrownBy(
            () ->
                orderService.updateOrderStatus(
                    order.getOrderID(), statuses.findByStatusName("Shipped").getStatusID()))
        .hasMessageContaining("cannot be reopened");
    assertThat(stock()).isEqualTo(8);
  }

  @Test
  void returnUsesHistoricalPriceAndCannotExceedPurchasedQuantity() {
    Sale sale = newSale();
    saleService.saveSale(sale, List.of(line(product, 1)));
    product.setUnitPrice(new BigDecimal("250.00"));
    products.save(product);
    Return item = returned(sale, product);
    returnService.saveReturn(item);
    assertThat(item.getRefundAmount()).isEqualByComparingTo("100.00");
    assertThat(stock()).isEqualTo(5);
    assertThatThrownBy(() -> returnService.saveReturn(returned(sale, product)))
        .hasMessageContaining("already been returned");
    assertThat(stock()).isEqualTo(5);
  }

  @Test
  void unrelatedProductCannotBeReturned() {
    Sale sale = newSale();
    saleService.saveSale(sale, List.of(line(product, 1)));
    Product other =
        products.findAll().stream()
            .filter(p -> !p.getProductID().equals(product.getProductID()))
            .findFirst()
            .orElseThrow();
    long before = returns.count();
    assertThatThrownBy(() -> returnService.saveReturn(returned(sale, other)))
        .hasMessageContaining("not included");
    assertThat(returns.count()).isEqualTo(before);
  }

  @Test
  void invalidSaleRedisplaysInputAndMessage() throws Exception {
    mvc.perform(
            post("/sales/save")
                .with(user("owner").roles("Owner"))
                .with(csrf())
                .param("customer.customerID", customer.getCustomerID().toString())
                .param("productIds", product.getProductID().toString())
                .param("quantities[" + product.getProductID() + "]", "99"))
        .andExpect(status().isOk())
        .andExpect(view().name("sale/form"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Insufficient stock")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"99\"")));
  }

  @Test
  void invalidOrderAndReturnRedisplayTheirForms() throws Exception {
    mvc.perform(
            post("/orders/save")
                .with(user("owner").roles("Owner"))
                .with(csrf())
                .param(
                    "supplier.supplierID", suppliers.findAll().get(0).getSupplierID().toString()))
        .andExpect(status().isOk())
        .andExpect(view().name("order/form"));
    mvc.perform(post("/returns/save").with(user("owner").roles("Owner")).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("return/form"));
  }

  @Test
  void adminCannotCreateOwnerAndOwnerCannotDeleteSelf() throws Exception {
    mvc.perform(
            post("/admin/users/save")
                .with(user("admin").roles("Admin"))
                .with(csrf())
                .param("username", "escalated")
                .param("hashedPassword", "long-test-password")
                .param("role", "Owner"))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/admin/users/delete/" + users.findByUsername("owner").getUserID())
                .with(user("owner").roles("Owner"))
                .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void linkedRecordsCannotBeDeletedEvenWithLegacyCascadeConstraints() {
    Sale sale = newSale();
    saleService.saveSale(sale, List.of(line(product, 1)));
    assertThatThrownBy(() -> productService.delete(product.getProductID()))
        .hasMessageContaining("cannot be deleted");
    assertThatThrownBy(() -> customerService.delete(customer.getCustomerID()))
        .hasMessageContaining("cannot be deleted");
    Order order = new Order();
    var supplier = suppliers.findAll().get(0);
    order.setSupplier(supplier);
    OrderDetail line = new OrderDetail();
    line.setProduct(product);
    line.setQuantity(1);
    orderService.saveOrder(order, List.of(line));
    assertThatThrownBy(() -> supplierService.delete(supplier.getSupplierID()))
        .hasMessageContaining("cannot be deleted");
    assertThat(sales.existsById(sale.getSalesID())).isTrue();
    assertThat(orders.existsById(order.getOrderID())).isTrue();
  }

  @Test
  void unlinkedProductsCanBeDeleted() {
    Product unused = new Product();
    unused.setProductName("Unused");
    unused.setUnitPrice(BigDecimal.TEN);
    products.save(unused);
    productService.delete(unused.getProductID());
    assertThat(products.existsById(unused.getProductID())).isFalse();
  }
}
