package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Message createMessage(@Valid @RequestBody Message message) {
    return messageService.createMessage(message);
  }

  @GetMapping("/{id}")
  public Optional<Message> getMessageById(@PathVariable String id) {
    return messageService.getMessageById(id);
  }

  @GetMapping
  public List<Message> getAllMessages() {
    return messageService.getAllMessages();
  }

  @PutMapping("/{id}")
  public Message updateMessage(@PathVariable String id, @Valid @RequestBody Message updatedMessage) {
    return messageService.updateMessage(id, updatedMessage);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMessage(@PathVariable String id) {
    messageService.deleteMessage(id);
  }
}