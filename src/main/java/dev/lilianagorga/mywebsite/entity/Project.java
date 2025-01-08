package dev.lilianagorga.mywebsite.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "projects")
public class Project {

  @Id
  private String id;

  @Field("title")
  @NotBlank(message = "Title is mandatory")
  @Size(max = 100, message = "Title cannot exceed 100 characters")
  private String title;

  @Field("description")
  @NotBlank(message = "Description is mandatory")
  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @Field("tech_stack")
  private List<String> techStack;

  @NotBlank(message = "Image URL is mandatory")
  @Pattern(regexp = "^(https?://).*", message = "Image URL must start with https:// or https://")
  private String imageUrl;

  @NotBlank(message = "GitHub URL is mandatory")
  @Pattern(regexp = "^(https?://).*", message = "GitHub URL must start with https:// or https://")
  private String githubUrl;

  @NotBlank(message = "Demo URL is mandatory")
  @Pattern(regexp = "^(https?://).*", message = "Demo URL must start with https:// or https://")
  private String demoUrl;
}