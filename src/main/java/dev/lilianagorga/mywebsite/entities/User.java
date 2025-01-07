package dev.lilianagorga.mywebsite.entities;

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

  private String username;
  private String email;
  private String password;

  @Builder.Default
  private List<String> roles = List.of("USER");
}