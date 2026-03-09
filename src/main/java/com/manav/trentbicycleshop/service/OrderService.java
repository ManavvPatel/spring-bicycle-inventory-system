// Project 2 - Part 2 : COSC4606 - 001
//Student Name: Manav Patel
//Student ID: 239466460

package com.manav.trentbicycleshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manav.trentbicycleshop.model.*;
import com.manav.trentbicycleshop.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StatusRepository statusRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, StatusRepository statusRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.statusRepository = statusRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void saveOrder(Order order, List<OrderDetail> details) {
        // Set default order date and status
        order.setOrderDate(LocalDateTime.now());
        Status shippedStatus = statusRepository.findByStatusName("Shipped");
        order.setStatus(shippedStatus);
        Order savedOrder = orderRepository.save(order);

        for (OrderDetail detail : details) {
            detail.setOrder(savedOrder);
            orderDetailRepository.save(detail);
        }
    }

    @Transactional
    public void updateOrderStatus(Integer orderID, Integer newStatusID) {
        Order order = orderRepository.findById(orderID).orElse(null);
        if (order == null) {
            System.out.println("Order with ID " + orderID + " not found.");
            return;
        }
        
        Status newStatus = statusRepository.findById(newStatusID).orElse(null);
        if (newStatus == null) {
            System.out.println("New status with ID " + newStatusID + " not found.");
            return;
        }
        
        String currentStatus = order.getStatus() != null ? order.getStatus().getStatusName() : null;
        System.out.println("Updating Order ID " + orderID);
        System.out.println("Current status: " + currentStatus);
        System.out.println("Requested new status: " + newStatus.getStatusName());
        
        // Check if we are transitioning from "Shipped" to "Received"
        if ("Shipped".equals(currentStatus) && "Received".equals(newStatus.getStatusName())) {
            List<OrderDetail> details = orderDetailRepository.findByOrderOrderID(orderID);
            System.out.println("Found " + details.size() + " order details for Order ID " + orderID);
            for (OrderDetail detail : details) {
                Product product = detail.getProduct();
                System.out.println("Processing OrderDetail for Product ID " + product.getProductID() + " with quantity " + detail.getQuantity());
                List<Inventory> invList = inventoryRepository.findAllByProduct(product);
                Inventory inventory;
                if (invList.isEmpty()) {
                    System.out.println("No inventory record for Product ID " + product.getProductID() + ". Creating new record with stock " + detail.getQuantity());
                    inventory = new Inventory();
                    inventory.setProduct(product);
                    inventory.setCurrentStock(detail.getQuantity());
                } else {
                    // If there are multiple records, sum them up (or choose first record)
                    inventory = invList.get(0);
                    int updatedStock = inventory.getCurrentStock() + detail.getQuantity();
                    System.out.println("Existing inventory record found. Old stock: " + inventory.getCurrentStock() + ", adding: " + detail.getQuantity() + ", new stock: " + updatedStock);
                    inventory.setCurrentStock(updatedStock);
                }
                inventory.setLastUpdate(LocalDateTime.now());
                inventoryRepository.save(inventory);
                System.out.println("Inventory updated for Product ID " + product.getProductID());
            }
        } else {
            System.out.println("No inventory update needed. Transition not from 'Shipped' to 'Received'.");
        }
        
        // Update the order status regardless
        order.setStatus(newStatus);
        orderRepository.save(order);
        System.out.println("Order status updated for Order ID " + orderID + " to " + newStatus.getStatusName());
    }
}