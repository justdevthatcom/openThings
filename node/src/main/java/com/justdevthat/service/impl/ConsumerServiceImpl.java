package com.justdevthat.service.impl;

import com.justdevthat.service.ConsumerService;
import com.justdevthat.service.ProducerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.justdevthat.RabbitQueue.*;

@Service
public class ConsumerServiceImpl implements ConsumerService {
  private final static Logger log = LogManager.getLogger(ConsumerService.class);
  private ProducerService producerService;

  public ConsumerServiceImpl(ProducerService producerService) {
    this.producerService = producerService;
  }

  @Override
  @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
  public void consumeTextMessageUpdates(Update update) {
    log.info("NODE: Text message received.");

    var message = update.getMessage();
    var sendMessage = new SendMessage();
    sendMessage.setChatId(message.getChatId());
    sendMessage.setText("Hi from NODE microservice");
    producerService.produceAnswer(sendMessage);
  }

  @Override
  @RabbitListener(queues = DOC_MESSAGE_UPDATE)
  public void consumeDocMessageUpdates(Update update) {
    log.info("NODE: Doc message received.");
  }

  @Override
  @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
  public void consumePhotoMessageUpdates(Update update) {
    log.info("NODE: Photo message received.");
  }
}
