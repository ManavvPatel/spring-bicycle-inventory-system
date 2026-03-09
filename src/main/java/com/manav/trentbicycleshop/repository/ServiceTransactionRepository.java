// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manav.trentbicycleshop.model.ServiceTransaction;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceTransactionRepository extends JpaRepository<ServiceTransaction, Integer> {
    List<ServiceTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}