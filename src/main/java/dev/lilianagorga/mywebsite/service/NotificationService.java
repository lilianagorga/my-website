package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Message;

public interface NotificationService {
  void notify(Message message);
}