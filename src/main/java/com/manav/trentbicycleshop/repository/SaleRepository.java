// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import com.manav.trentbicycleshop.model.Sale;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
  boolean existsByCustomerCustomerID(Integer id);

  List<Sale> findTop5ByOrderBySaleDateDesc();

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select x from Sale x where x.salesID = :id")
  java.util.Optional<Sale> lockById(
      @org.springframework.data.repository.query.Param("id") Integer id);

  List<Sale> findBySaleDateGreaterThanEqualAndSaleDateLessThan(
      LocalDateTime start, LocalDateTime end);
}
