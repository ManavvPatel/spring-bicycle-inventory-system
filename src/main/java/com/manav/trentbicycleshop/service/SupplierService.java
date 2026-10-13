// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.Supplier;
import com.manav.trentbicycleshop.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class SupplierService {

  private final SupplierRepository supplierRepository;
  private final DeletionGuard deletionGuard;

  public SupplierService(SupplierRepository supplierRepository, DeletionGuard deletionGuard) {
    this.deletionGuard = deletionGuard;
    this.supplierRepository = supplierRepository;
  }

  public List<Supplier> getAll() {
    return supplierRepository.findAll();
  }

  public Supplier get(Integer id) {
    return supplierRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Record not found."));
  }

  public void save(Supplier supplier) {
    supplierRepository.save(supplier);
  }

  public void delete(Integer id) {
    deletionGuard.supplier(id);
    supplierRepository.deleteById(id);
  }
}
