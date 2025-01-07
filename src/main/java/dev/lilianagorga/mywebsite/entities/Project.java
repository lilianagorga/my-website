package dev.lilianagorga.mywebsite.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "projects")
public class Project {

  @Id
  private String id;

  private String title;
  private String description;
  private String imageUrl;
  private List<String> techStack;

  private String githubUrl;
  private String demoUrl;
}