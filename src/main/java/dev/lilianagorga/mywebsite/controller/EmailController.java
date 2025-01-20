package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.dto.EmailRequest;
import dev.lilianagorga.mywebsite.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

  private final EmailService emailService;

  @PostMapping("/send-email")
  public String sendEmail(@RequestBody EmailRequest emailRequest) {
    return emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getPlainTextBody(), emailRequest.getHtmlBody());
  }
}