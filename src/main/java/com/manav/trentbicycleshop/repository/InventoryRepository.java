// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manav.trentbicycleshop.model.Inventory;
import com.manav.trentbicycleshop.model.Product;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findAllByProduct(Product product);
}