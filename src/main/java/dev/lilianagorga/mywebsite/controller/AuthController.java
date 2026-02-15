package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import dev.lilianagorga.mywebsite.request.LoginRequest;
import dev.lilianagorga.mywebsite.response.AuthResponse;
import dev.lilianagorga.mywebsite.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody User user) {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      return new AuthResponse("Email already in use.", null);
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    // Public registration is always a USER. Admin role must be assigned via protected admin flow.
    user.setRoles(Collections.singletonList("USER"));
    if (user.getUsername() == null || user.getUsername().isBlank()) {
      user.setUsername("default_username");
    }
    userRepository.save(user);
    return new AuthResponse("User registered successfully.", null);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            )
    );

    User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
    String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
    return new AuthResponse("Login successful.", token);
  }

  @PostMapping("/logout")
  public AuthResponse logout() {
    return new AuthResponse("Logout successful.", null);
  }
}
