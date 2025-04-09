package com.justdevthat.service.impl;

import com.justdevthat.dao.AppUserDAO;
import com.justdevthat.dao.RawDataDAO;
import com.justdevthat.entity.AppUser;
import com.justdevthat.entity.RawData;
import com.justdevthat.entity.UserState;
import com.justdevthat.service.MainService;
import com.justdevthat.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class MainServiceImpl implements MainService {
  private final RawDataDAO rawDataDAO;
  private final ProducerService producerService;
  private final AppUserDAO appUserDAO;

  public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
    this.rawDataDAO = rawDataDAO;
    this.producerService = producerService;
    this.appUserDAO = appUserDAO;
  }

  @Override
  public void processTextMessage(Update update) {
    saveRawDate(update);
    var textMessage = update.getMessage();
    var telegramUser = textMessage.getFrom();
    var appUser = findOrSaveAppUser(telegramUser);

    var message = update.getMessage();
    var sendMessage = new SendMessage();
    sendMessage.setChatId(message.getChatId());
    sendMessage.setText("Hi from NODE microservice");
    producerService.produceAnswer(sendMessage);
  }

  private AppUser findOrSaveAppUser(User telegramUser) {
    AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
    if (persistentAppUser == null) {
      AppUser transientAppUser = AppUser.builder()
              .telegramUserId(telegramUser.getId())
              .userName(telegramUser.getUserName())
              .firstName(telegramUser.getFirstName())
              .lastName(telegramUser.getLastName())
              //TODO изменить значение по умолчанию после добавления модуля регистрации пользователя
              .isActive(true)
              .userState(UserState.BASIC_STATE)
              .build();
      return appUserDAO.save(transientAppUser); // возвращаем сохраненный в БД объект уже с ID и с привязкой к сессии Hibernate
    }
    return persistentAppUser;
  }

  private void saveRawDate(Update update) {
    RawData rawData = RawData.builder()
            .event(update)
            .build();
    rawDataDAO.save(rawData);
  }
}
