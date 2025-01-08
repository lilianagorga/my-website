package dev.lilianagorga.mywebsite.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "messages")
public class Message {

  @Id
  private String id;

  @NotBlank(message = "Name is mandatory")
  private String name;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is mandatory")
  private String email;

  @NotBlank(message = "Message cannot be empty")
  private String message;

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}