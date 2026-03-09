// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manav.trentbicycleshop.model.Sale;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}