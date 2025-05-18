package com.justdevthat.dispatcher.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
//@Log4j //- работает только с import lombok.extern.log4j.Log4j; то есть с версией Log4j 1.x
public class TelegramBot extends TelegramLongPollingBot { // без статического IP
//public class TelegramBot extends TelegramWebhookBot { // нужен статический IP
  private final static Logger log = LogManager.getLogger(TelegramBot.class);
  @Value("${bot.name}")
  private String botName;
  @Value("${bot.token}")
  private String botToken;
  @Value("${bot.uri}")
  private String botUri;

  private UpdateProcessor updateProcessor;


  public TelegramBot(UpdateProcessor updateProcessor) {
    this.updateProcessor = updateProcessor;
  }

  @PostConstruct
  public void init() {
    updateProcessor.registerBot(this);
  }


  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  // Для TelegramLongPollingBot
  @Override
  public void onUpdateReceived(Update update) {
//    // -- просто для обратной связи
//    var origMessage = update.getMessage();
//    if (origMessage != null) {
//      log.warn(origMessage.getText());
//      System.out.println("from sout: " + origMessage.getText());
//    }
//    // -- конец

    updateProcessor.processUpdate(update);
//
//    var response = new SendMessage();
//    response.setChatId(origMessage.getChatId());
//    response.setText(String.format("Ответ ботика: Точно %s ???", origMessage.getText()));
//    sendAnswerMessage(response);
  }

  public void sendAnswerMessage(SendMessage message) {
    if (message != null) {
      try {
        execute(message);
      } catch (TelegramApiException e) {
        log.error(e);
      }
    }
  }

//  // Для TelegramWebhookBot
//  @Override
//  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//    return null; // метод не понадобится
//  }
//
//  @Override
//  public String getBotPath() {
//    return "/update"; // <bot.uri> + "/callback" + "/update". Например, "https:/openThings.ru/callback/update"
//  }
}
