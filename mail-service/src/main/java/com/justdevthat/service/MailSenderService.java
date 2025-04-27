package com.justdevthat.service;

import com.justdevthat.dto.MailParams;

public interface MailSenderService {
  void send(MailParams mailParams);
}
