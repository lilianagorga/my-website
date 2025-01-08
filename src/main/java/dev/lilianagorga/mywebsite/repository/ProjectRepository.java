package dev.lilianagorga.mywebsite.repository;

import dev.lilianagorga.mywebsite.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
  Optional<Project> findByTitle(String title);
}