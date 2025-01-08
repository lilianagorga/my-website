package dev.lilianagorga.mywebsite.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

  @Id
  private String id;

  @NotBlank(message = "Username is mandatory")
  private String username;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is mandatory")
  private String email;

  @NotBlank(message = "Password is mandatory")
  @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
  private String password;

  @Builder.Default
  private List<String> roles = List.of("USER");
}