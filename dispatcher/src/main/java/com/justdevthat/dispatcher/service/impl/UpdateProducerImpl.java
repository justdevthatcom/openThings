package com.justdevthat.dispatcher.service.impl;

import com.justdevthat.dispatcher.service.UpdateProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UpdateProducerImpl implements UpdateProducer {
  private final static Logger log = LogManager.getLogger(UpdateProducerImpl.class);

  private final RabbitTemplate rabbitTemplate;

  public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void produce(String rabbitQueue, Update update) {
    log.info(update.getMessage().getText());

    rabbitTemplate.convertAndSend(rabbitQueue, update);
  }
}
