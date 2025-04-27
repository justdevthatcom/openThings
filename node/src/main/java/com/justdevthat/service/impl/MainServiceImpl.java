package com.justdevthat.service.impl;

import com.justdevthat.dao.AppUserDAO;
import com.justdevthat.dao.RawDataDAO;
import com.justdevthat.entity.AppDocument;
import com.justdevthat.entity.AppPhoto;
import com.justdevthat.entity.AppUser;
import com.justdevthat.entity.RawData;
import com.justdevthat.entity.enums.UserState;
import com.justdevthat.exceptions.UploadFileException;
import com.justdevthat.service.FileService;
import com.justdevthat.service.MainService;
import com.justdevthat.service.ProducerService;
import com.justdevthat.service.enums.LinkType;
import com.justdevthat.service.enums.ServiceCommands;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.justdevthat.entity.enums.UserState.BASIC_STATE;
import static com.justdevthat.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.justdevthat.service.enums.ServiceCommands.*;

@Service
@Log4j2
public class MainServiceImpl implements MainService {
  private final RawDataDAO rawDataDAO;
  private final ProducerService producerService;
  private final AppUserDAO appUserDAO;
  private final FileService fileService;

  public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService) {
    this.rawDataDAO = rawDataDAO;
    this.producerService = producerService;
    this.appUserDAO = appUserDAO;
    this.fileService = fileService;
  }

  @Override
  public void processTextMessage(Update update) {
    saveRawData(update);
    var appUser = findOrSaveAppUser(update);
    var userStat = appUser.getUserState();
    var text = update.getMessage().getText();
    var output = "";

    ServiceCommands serviceCommands = fromValue(text);
    if (CANCEL.equals(serviceCommands)) {
      output = cancelProcess(appUser);
    } else if (BASIC_STATE.equals(userStat)) {
      output = processServiceCommands(appUser, text);
    } else if (WAIT_FOR_EMAIL_STATE.equals(userStat)) {
      //TODO добавить регистрацию по email
    } else {
      log.error("Unknown user state: " + userStat);
      output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
    }

    var chatId = update.getMessage().getChatId();
    sendAnswer(output, chatId);

  }

  @Override
  public void processDocMessage(Update update) {
    saveRawData(update);
    var appUser = findOrSaveAppUser(update);
    var chatId = update.getMessage().getChatId();
    if (isNotAllowToSendContent(chatId, appUser)) {
      return;
    }
    try {
      AppDocument doc = fileService.processDoc(update.getMessage());
      var link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
      var answer = "Документ успешно загружен! "
              + "Ссылка для скачивания: " + link;
      sendAnswer(answer, chatId);
    } catch (UploadFileException ex) {
      log.error(ex);
      String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
      sendAnswer(error, chatId);
    }
  }

  private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
    UserState userState = appUser.getUserState();
    if (!appUser.getIsActive()) {
      var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
      sendAnswer(error, chatId);
      return true;
    } else if (!BASIC_STATE.equals(appUser.getUserState())) {
      var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
      sendAnswer(error, chatId);
      return true;
    }
    return false;
  }

  @Override
  public void processPhotoMessage(Update update) {
    saveRawData(update);
    var appUser = findOrSaveAppUser(update);
    var chatId = update.getMessage().getChatId();
    if (isNotAllowToSendContent(chatId, appUser)) {
      return;
    }
    try {
      AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
      var link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);
      var answer = "Фото успешно загружено! (заглушка) Ссылка для скачивания: " + link;
      sendAnswer(answer, chatId);
    } catch (UploadFileException ex) {
      log.error(ex);
      String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
      sendAnswer(error, chatId);
    }
  }

  private void sendAnswer(String output, Long chatId) {
//    var message = update.getMessage();
    var sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(output);
    producerService.produceAnswer(sendMessage);
  }

  private String processServiceCommands(AppUser appUser, String cmd) {
    ServiceCommands serviceCommand = fromValue(cmd);
    if (REGISTRATION.equals(serviceCommand))
      //TODO сделать регистрацию
      return "Временно недоступно.";
    else if (HELP.equals(serviceCommand))
      return help();
    else if (START.equals(serviceCommand))
      return "Приветствую! Чтобы посмотреть список всех команд введите /help";
    else
      return "Неизвестная команда! Чтобы посмотреть список всех команд введите /help";
  }

  private String help() {
    return "Список доспуных команд:\n"
            + "/cancel - отмена выполнения текущей команды;\n"
            + "/registration - регистрация пользователя.";
  }

  private String cancelProcess(AppUser appUser) {
    appUser.setUserState(BASIC_STATE);
    appUserDAO.save(appUser);
    return "Команда отменена!";
  }

  private AppUser findOrSaveAppUser(Update update) {
    User telegramUser = update.getMessage().getFrom();
    AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
    if (persistentAppUser == null) {
      AppUser transientAppUser = AppUser.builder()
              .telegramUserId(telegramUser.getId())
              .userName(telegramUser.getUserName())
              .firstName(telegramUser.getFirstName())
              .lastName(telegramUser.getLastName())
              //TODO изменить значение по умолчанию после добавления модуля регистрации пользователя
              .isActive(true)
              .userState(BASIC_STATE)
              .build();
      return appUserDAO.save(transientAppUser); // возвращаем сохраненный в БД объект уже с ID и с привязкой к сессии Hibernate
    }
    return persistentAppUser;
  }

  private void saveRawData(Update update) {
    RawData rawData = RawData.builder()
            .event(update)
            .build();
    rawDataDAO.save(rawData);
  }
}
