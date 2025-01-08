package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(User user) {
    return userRepository.save(user);
  }

  public Optional<User> getUserById(String id) {
    return userRepository.findById(id);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User updateUser(String id, User updatedUser) {
    return userRepository.findById(id).map(existingUser -> {
      existingUser.setUsername(updatedUser.getUsername());
      existingUser.setEmail(updatedUser.getEmail());
      existingUser.setPassword(updatedUser.getPassword());
      existingUser.setRoles(updatedUser.getRoles());
      return userRepository.save(existingUser);
    }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  public void deleteUser(String id) {
    if (!userRepository.existsById(id)) {
      throw new RuntimeException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
  }
}