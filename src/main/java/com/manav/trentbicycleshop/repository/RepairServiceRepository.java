// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manav.trentbicycleshop.model.RepairService;

public interface RepairServiceRepository extends JpaRepository<RepairService, Integer> {
}