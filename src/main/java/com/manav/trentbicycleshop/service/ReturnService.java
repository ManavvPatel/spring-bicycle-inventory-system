// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manav.trentbicycleshop.model.Inventory;
import com.manav.trentbicycleshop.model.Return;
import com.manav.trentbicycleshop.repository.InventoryRepository;
import com.manav.trentbicycleshop.repository.ReturnRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final InventoryRepository inventoryRepository;

    public ReturnService(ReturnRepository returnRepository,
                         InventoryRepository inventoryRepository) {
        this.returnRepository = returnRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Return> getAllReturns() {
        return returnRepository.findAll();
    }

    public void saveReturn(Return aReturn) {
        aReturn.setReturnDate(LocalDateTime.now());
        Return savedReturn = returnRepository.save(aReturn);

        List<Inventory> invList = inventoryRepository.findAllByProduct(savedReturn.getProduct());
        if (!invList.isEmpty()) {
            Inventory inventory = invList.get(0);
           
            int returnedQuantity = 1;
            int newStock = inventory.getCurrentStock() + returnedQuantity;
            inventory.setCurrentStock(newStock);
            inventory.setLastUpdate(LocalDateTime.now());
            inventoryRepository.save(inventory);
        } else {
            Inventory inventory = new Inventory();
            inventory.setProduct(savedReturn.getProduct());
            // Sample return Quantity
            inventory.setCurrentStock(1);
            inventory.setLastUpdate(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }
    }
}