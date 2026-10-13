// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.Product;
import com.manav.trentbicycleshop.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class ProductService {

  private final ProductRepository productRepository;
  private final DeletionGuard deletionGuard;

  public ProductService(ProductRepository productRepository, DeletionGuard deletionGuard) {
    this.deletionGuard = deletionGuard;
    this.productRepository = productRepository;
  }

  public List<Product> getAll() {
    return productRepository.findAll();
  }

  public Product get(Integer id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Record not found."));
  }

  public void save(Product product) {
    productRepository.save(product);
  }

  public void delete(Integer id) {
    deletionGuard.product(id);
    productRepository.deleteById(id);
  }
}
