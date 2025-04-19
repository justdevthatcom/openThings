package com.justdevthat.service.impl;

import com.justdevthat.service.ConsumerService;
import com.justdevthat.service.MainService;
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
  private final MainService mainService;

  public ConsumerServiceImpl(MainService mainService) {
    this.mainService = mainService;
  }

  @Override
  @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
  public void consumeTextMessageUpdates(Update update) {
    log.info("NODE: Text message received.");
    mainService.processTextMessage(update);
  }

  @Override
  @RabbitListener(queues = DOC_MESSAGE_UPDATE)
  public void consumeDocMessageUpdates(Update update) {
    log.info("NODE: Doc message received.");
    mainService.processDocMessage(update);
  }

  @Override
  @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
  public void consumePhotoMessageUpdates(Update update) {
    log.info("NODE: Photo message received.");
    mainService.processPhotoMessage(update);
  }
}
