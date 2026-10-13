// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import com.manav.trentbicycleshop.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRepository extends JpaRepository<Return, Integer> {
  boolean existsByProductProductID(Integer id);

  long countBySaleSalesIDAndProductProductID(Integer salesID, Integer productID);
}
