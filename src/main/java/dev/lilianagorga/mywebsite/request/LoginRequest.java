package dev.lilianagorga.mywebsite.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is mandatory")
  private String email;
  @NotBlank(message = "Password is mandatory")
  private String password;
}