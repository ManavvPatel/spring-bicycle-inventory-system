// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.Customer;
import com.manav.trentbicycleshop.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final DeletionGuard deletionGuard;

  public CustomerService(CustomerRepository customerRepository, DeletionGuard deletionGuard) {
    this.deletionGuard = deletionGuard;
    this.customerRepository = customerRepository;
  }

  public List<Customer> getAll() {
    return customerRepository.findAll();
  }

  public Customer get(Integer id) {
    return customerRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Record not found."));
  }

  public void save(Customer customer) {
    customerRepository.save(customer);
  }

  public void delete(Integer id) {
    deletionGuard.customer(id);
    customerRepository.deleteById(id);
  }
}
