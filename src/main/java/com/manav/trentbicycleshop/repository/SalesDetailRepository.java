// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import com.manav.trentbicycleshop.model.SalesDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesDetailRepository extends JpaRepository<SalesDetail, Integer> {
  boolean existsByProductProductID(Integer id);

  List<SalesDetail> findBySaleSalesID(Integer salesID);
}
