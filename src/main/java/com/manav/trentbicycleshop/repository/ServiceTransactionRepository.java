// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import com.manav.trentbicycleshop.model.ServiceTransaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTransactionRepository extends JpaRepository<ServiceTransaction, Integer> {
  boolean existsByCustomerCustomerID(Integer id);

  boolean existsByRepairServiceServiceID(Integer id);

  List<ServiceTransaction> findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(
      LocalDateTime start, LocalDateTime end);
}
