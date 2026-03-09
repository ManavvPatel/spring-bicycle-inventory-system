// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manav.trentbicycleshop.model.ServiceTransaction;
import com.manav.trentbicycleshop.repository.ServiceTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ServiceTransactionService {

    private final ServiceTransactionRepository serviceTransactionRepository;

    public ServiceTransactionService(ServiceTransactionRepository serviceTransactionRepository) {
        this.serviceTransactionRepository = serviceTransactionRepository;
    }

    public List<ServiceTransaction> getAllTransactions() {
        return serviceTransactionRepository.findAll();
    }

    public void saveTransaction(ServiceTransaction transaction) {
        serviceTransactionRepository.save(transaction);
    }

    public List<ServiceTransaction> getTransactionsBetween(LocalDateTime start, LocalDateTime end) {
    return serviceTransactionRepository.findByTransactionDateBetween(start, end);
}
}