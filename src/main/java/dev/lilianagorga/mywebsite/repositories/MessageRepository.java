package dev.lilianagorga.mywebsite.repositories;

import dev.lilianagorga.mywebsite.entities.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
}