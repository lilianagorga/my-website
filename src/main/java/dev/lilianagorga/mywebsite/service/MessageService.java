package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final NotificationService notificationService;

  public MessageService(MessageRepository messageRepository, NotificationService notificationService) {
    this.messageRepository = messageRepository;
    this.notificationService = notificationService;
  }


  public Message createMessage(Message message) {
    Message savedMessage = messageRepository.save(message);
    notificationService.notify(savedMessage);
    return savedMessage;
  }

  public Optional<Message> getMessageById(String id) {
    return messageRepository.findById(id);
  }

  public List<Message> getAllMessages() {
    return messageRepository.findAll();
  }

  public Optional<Message> getMessageByEmail(String email) {
    return messageRepository.findByEmail(email);
  }

  public Message updateMessage(String id, Message updatedMessage) {
    return messageRepository.findById(id).map(existingMessage -> {
      existingMessage.setName(updatedMessage.getName());
      existingMessage.setEmail(updatedMessage.getEmail());
      existingMessage.setMessage(updatedMessage.getMessage());
      return messageRepository.save(existingMessage);
    }).orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
  }

  public void deleteMessage(String id) {
    if (!messageRepository.existsById(id)) {
      throw new RuntimeException("Message not found with id: " + id);
    }
    messageRepository.deleteById(id);
  }
}