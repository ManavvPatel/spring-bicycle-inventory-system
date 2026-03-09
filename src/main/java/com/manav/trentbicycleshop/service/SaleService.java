// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.InventoryRepository;
import com.manav.trentbicycleshop.repository.SaleRepository;
import com.manav.trentbicycleshop.repository.SalesDetailRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SalesDetailRepository salesDetailRepository;
    private final InventoryRepository inventoryRepository;

    public SaleService(SaleRepository saleRepository, 
                       SalesDetailRepository salesDetailRepository,
                       InventoryRepository inventoryRepository) {
        this.saleRepository = saleRepository;
        this.salesDetailRepository = salesDetailRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public void saveSale(Sale sale, List<SalesDetail> saleDetails) {

        sale.setSaleDate(LocalDateTime.now());
        sale.setTotalAmount(BigDecimal.ZERO);
        
        Sale savedSale = saleRepository.save(sale);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SalesDetail detail : saleDetails) {
            detail.setSale(savedSale);
            
            if(detail.getUnitPrice() != null && detail.getQuantity() != null) {
                BigDecimal subTotal = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
                detail.setSubTotal(subTotal);
                totalAmount = totalAmount.add(subTotal);
            }
            salesDetailRepository.save(detail);

            // Update inventory: subtract sold quantity
            List<Inventory> invList = inventoryRepository.findAllByProduct(detail.getProduct());
            if (!invList.isEmpty()) {
                Inventory inventory = invList.get(0);
                int newStock = inventory.getCurrentStock() - detail.getQuantity();
                inventory.setCurrentStock(newStock);
                inventory.setLastUpdate(LocalDateTime.now());
                inventoryRepository.save(inventory);
            } else {
                System.out.println("No inventory record found for product " + detail.getProduct().getProductID());
            }
        }
        savedSale.setTotalAmount(totalAmount);
        saleRepository.save(savedSale);
    }

    public List<Sale> getSalesBetween(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findBySaleDateBetween(start, end);
    }
}