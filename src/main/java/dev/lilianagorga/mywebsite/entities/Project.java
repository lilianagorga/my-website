package dev.lilianagorga.mywebsite.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
  private String title;
  @Field("description")
  private String description;
  @Field("image_url")
  private String imageUrl;
  @Field("tech_stack")
  private List<String> techStack;
  @Field("github_url")
  private String githubUrl;
  @Field("demo_url")
  private String demoUrl;
}