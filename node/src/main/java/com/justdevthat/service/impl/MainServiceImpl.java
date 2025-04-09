package com.justdevthat.service.impl;

import com.justdevthat.dao.RawDataDAO;
import com.justdevthat.entity.RawData;
import com.justdevthat.service.MainService;
import com.justdevthat.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainServiceImpl implements MainService {
  private final RawDataDAO rawDataDAO;
  private final ProducerService producerService;

  public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService) {
    this.rawDataDAO = rawDataDAO;
    this.producerService = producerService;
  }

  @Override
  public void processTextMessage(Update update) {
    saveRawDate(update);

    var message = update.getMessage();
    var sendMessage = new SendMessage();
    sendMessage.setChatId(message.getChatId());
    sendMessage.setText("Hi from NODE microservice");
    producerService.produceAnswer(sendMessage);
  }

  private void saveRawDate(Update update) {
    RawData rawData = RawData.builder()
            .event(update)
            .build();
    rawDataDAO.save(rawData);
  }
}
