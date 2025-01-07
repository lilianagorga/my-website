package dev.lilianagorga.mywebsite.repositories;

import dev.lilianagorga.mywebsite.entities.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
}