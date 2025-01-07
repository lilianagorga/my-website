package dev.lilianagorga.mywebsite.entities;

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

  private String name;
  private String email;
  private String message;

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}