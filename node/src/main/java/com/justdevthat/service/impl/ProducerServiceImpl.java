package com.justdevthat.service.impl;

import com.justdevthat.service.ProducerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.justdevthat.RabbitQueue.ANSWER_MESSAGE;

@Service
public class ProducerServiceImpl implements ProducerService {
  private final static Logger log = LogManager.getLogger(ProducerServiceImpl.class);
  private final RabbitTemplate rabbitTemplate;

  public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void produceAnswer(SendMessage sendMessage) {
    log.info("NODE: ProducerServiceImpl::produceAnswer " + sendMessage);
    rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
  }
}
