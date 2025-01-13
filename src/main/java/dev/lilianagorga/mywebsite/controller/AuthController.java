package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import dev.lilianagorga.mywebsite.request.LoginRequest;
import dev.lilianagorga.mywebsite.response.AuthResponse;
import dev.lilianagorga.mywebsite.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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
    if (user.getRoles() == null || user.getRoles().isEmpty()) {
      user.setRoles(Collections.singletonList("USER"));
    } else if (!List.of("USER", "ADMIN").containsAll(user.getRoles())) {
      throw new IllegalArgumentException("Invalid role provided.");
    }
    if (user.getUsername() == null || user.getUsername().isBlank()) {
      user.setUsername("default_username");
    }
    userRepository.save(user);
    return new AuthResponse("User registered successfully.", null);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
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