// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.service;

import org.springframework.stereotype.Service;

import com.manav.trentbicycleshop.dto.InventoryValueDTO;
import com.manav.trentbicycleshop.model.Inventory;
import com.manav.trentbicycleshop.model.Product;
import com.manav.trentbicycleshop.repository.InventoryRepository;
import com.manav.trentbicycleshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public List<Inventory> getFullInventoryList() {
        List<Product> products = productRepository.findAll();
        List<Inventory> fullInventory = new ArrayList<>();

        for (Product product : products) {
            List<Inventory> invList = inventoryRepository.findAllByProduct(product);
            if (invList.isEmpty()) {
                
                Inventory inventory = new Inventory();
                inventory.setProduct(product);
                inventory.setCurrentStock(0);
                inventory.setLastUpdate(null); 
                fullInventory.add(inventory);
            } else {

                fullInventory.add(invList.get(0));
            }
        }
        return fullInventory;
    }

    public List<Inventory> getInventoryForProduct(Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return new ArrayList<>();
        }
        List<Inventory> invList = inventoryRepository.findAllByProduct(product);
        if (invList.isEmpty()) {

            Inventory dummy = new Inventory();
            dummy.setProduct(product);
            dummy.setCurrentStock(0);
            dummy.setLastUpdate(null);
            List<Inventory> result = new ArrayList<>();
            result.add(dummy);
            return result;
        }
        return invList;
    }

    public List<InventoryValueDTO> getInventoryValueDTOList() {
        List<Inventory> fullInventory = getFullInventoryList();
        List<InventoryValueDTO> dtoList = new ArrayList<>();
        for (Inventory inv : fullInventory) {
            Product product = inv.getProduct();
            BigDecimal unitPrice = product.getUnitPrice() != null ? product.getUnitPrice() : BigDecimal.ZERO;
            Integer stock = inv.getCurrentStock() != null ? inv.getCurrentStock() : 0;
            BigDecimal totalValue = unitPrice.multiply(BigDecimal.valueOf(stock));
            dtoList.add(new InventoryValueDTO(
                product.getProductID(),
                product.getProductName(),
                stock,
                unitPrice,
                totalValue
            ));
        }
        return dtoList;
    }

    public BigDecimal getTotalInventoryValue() {
        List<InventoryValueDTO> dtos = getInventoryValueDTOList();
        BigDecimal total = BigDecimal.ZERO;
        for (InventoryValueDTO dto : dtos) {
            total = total.add(dto.getTotalValue());
        }
        return total;
    }
}