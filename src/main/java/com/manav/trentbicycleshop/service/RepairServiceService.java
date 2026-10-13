// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.RepairService;
import com.manav.trentbicycleshop.repository.RepairServiceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RepairServiceService {

  private final RepairServiceRepository repairServiceRepository;
  private final DeletionGuard deletionGuard;

  public RepairServiceService(
      RepairServiceRepository repairServiceRepository, DeletionGuard deletionGuard) {
    this.deletionGuard = deletionGuard;
    this.repairServiceRepository = repairServiceRepository;
  }

  public List<RepairService> getAllServices() {
    return repairServiceRepository.findAll();
  }

  public RepairService getServiceById(Integer serviceID) {
    return repairServiceRepository
        .findById(serviceID)
        .orElseThrow(() -> new IllegalArgumentException("Record not found."));
  }

  public void saveService(RepairService service) {
    repairServiceRepository.save(service);
  }

  public void deleteService(Integer serviceID) {
    deletionGuard.repair(serviceID);
    repairServiceRepository.deleteById(serviceID);
  }
}
