// Project 2 - Part 2 : COSC4606 - 001
// Student Name: Manav Patel
// Student ID: 239466460

package com.manav.trentbicycleshop.service;

import com.manav.trentbicycleshop.model.User;
import com.manav.trentbicycleshop.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUser(Integer userID) {
    return userRepository
        .findById(userID)
        .orElseThrow(() -> new IllegalArgumentException("Record not found."));
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }

  public void deleteUser(Integer userID) {
    userRepository.deleteById(userID);
  }
}
