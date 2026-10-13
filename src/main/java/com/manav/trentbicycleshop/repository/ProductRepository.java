// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import com.manav.trentbicycleshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select p from Product p where p.productID = :id")
  java.util.Optional<Product> lockById(
      @org.springframework.data.repository.query.Param("id") Integer id);
}
