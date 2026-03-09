// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.service;

import org.springframework.stereotype.Service;

import com.manav.trentbicycleshop.model.Supplier;
import com.manav.trentbicycleshop.repository.SupplierRepository;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    public Supplier get(Integer id) {
        return supplierRepository.findById(id).orElse(null);
    }

    public void save(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    public void delete(Integer id) {
        supplierRepository.deleteById(id);
    }
}