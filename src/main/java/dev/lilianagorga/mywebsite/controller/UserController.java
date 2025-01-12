package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public UserController(UserService userService, PasswordEncoder passwordEncoder) {

    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@Valid @RequestBody User user) {
    if (user.getRoles() == null || user.getRoles().isEmpty()) {
      user.setRoles(Collections.singletonList("USER"));
    }
    if (user.getUsername() == null || user.getUsername().isBlank()) {
      user.setUsername("default_username");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userService.createUser(user);
  }

  @GetMapping("/{id}")
  public Optional<User> getUserById(@PathVariable String id) {
    return userService.getUserById(id);
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @PutMapping("/{id}")
  public User updateUser(@PathVariable String id, @Valid @RequestBody User updatedUser) {
    return userService.updateUser(id, updatedUser);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
  }
}