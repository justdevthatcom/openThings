package com.justdevthat.dispatcher.controller;

import com.justdevthat.dispatcher.service.UpdateProducer;
import com.justdevthat.dispatcher.utils.MessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.justdevthat.RabbitQueue.*;

@Component
public class UpdateController {
  private final static Logger log = LogManager.getLogger(UpdateController.class);
  private TelegramBot tgBot;
  private final MessageUtils messageUtils;
  @Autowired
  private UpdateProducer updateProducer;

  public UpdateController(MessageUtils messageUtils) {
    this.messageUtils = messageUtils;
  }

  public void registerBot(TelegramBot tgBot) {
    this.tgBot = tgBot;
  }


  public void processUpdate(Update update) {
    if (update == null) {
      log.error("Received update is null");
      return;
    }

    if (update.getMessage() != null) {
      distributeMessagesByType(update); // обрабатываем только обычные сообщения из чата, фото и доки, остальные типы сообщений будем расценивать как ошибочные (напр, редактирование сообщения)
    } else {
      log.error("Unsupported message type is received - " + update);
    }
  }

  private void distributeMessagesByType(Update update) {
    var message = update.getMessage();
    if (message.getText() != null) {
      processTextMessage(update);
    } else if (message.getDocument() != null) {
      processDocument(update);
    } else if (message.getPhoto() != null) {
      processPhoto(update);
    } else
      setUnsupportedMessageTypeView(update);
  }

  private void setUnsupportedMessageTypeView(Update update) {
    var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения!");
    setView(sendMessage);
  }

  private void setFileIsReceivedView(Update update) {
    var sendMessage = messageUtils.generateSendMessageWithText(update, "Файл получен! Обрабатываю ...");
    setView(sendMessage);
  }

  private void setPhotoIsReceivedView(Update update) {
    var sendMessage = messageUtils.generateSendMessageWithText(update, "Фотография получена! Обрабатываю ...");
    setView(sendMessage);
  }

  public void setView(SendMessage sendMessage) {
    tgBot.sendAnswerMessage(sendMessage);
  }

  private void processTextMessage(Update update) {
    updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
  }


  private void processDocument(Update update) {
    updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    setFileIsReceivedView(update);
  }

  private void processPhoto(Update update) {
    updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
    setPhotoIsReceivedView(update);
  }
}
