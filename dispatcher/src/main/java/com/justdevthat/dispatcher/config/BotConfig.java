package com.justdevthat.dispatcher.config;

import com.justdevthat.dispatcher.controller.TelegramBot;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

/**
 * Не присутствовал изначально, но без него бот не хотел работать
 * в т.ч. не было зависимости "javax.annotation"
 * */
@Configuration
public class BotConfig {
  private final TelegramBot bot;

  public BotConfig(TelegramBot bot) {
    this.bot = bot;
  }

  @PostConstruct
  public void init() {
    try {
      TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
      botsApi.registerBot(bot);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}