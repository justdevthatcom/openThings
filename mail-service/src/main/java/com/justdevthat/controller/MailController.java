package com.justdevthat.controller;

import com.justdevthat.dto.MailParams;
import com.justdevthat.service.MailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mail")
@RestController
public class MailController {
  private final MailSenderService mailSenderService;

  public MailController(MailSenderService mailSenderService) {
    this.mailSenderService = mailSenderService;
  }

  @PostMapping("/send")
  public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) {
    // Пока самый простой вариант без ControllerAdvice
    //TODO не забыть вписать пароль от почты. Лучше сделать отдельную сервисную почту для разработки
    mailSenderService.send(mailParams);
    return ResponseEntity.ok().build();
  }
}
