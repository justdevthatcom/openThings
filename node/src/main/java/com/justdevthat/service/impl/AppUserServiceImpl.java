package com.justdevthat.service.impl;

import com.justdevthat.dao.AppUserDAO;
import com.justdevthat.dao.RawDataDAO;
import com.justdevthat.dto.MailParams;
import com.justdevthat.entity.AppUser;
import com.justdevthat.service.AppUserService;
import com.justdevthat.service.FileService;
import com.justdevthat.service.ProducerService;
import com.justdevthat.utils.CryptoTool;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static com.justdevthat.entity.enums.UserState.BASIC_STATE;
import static com.justdevthat.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j2
@Service
public class AppUserServiceImpl implements AppUserService {

  private final AppUserDAO appUserDAO;
  private final CryptoTool cryptoTool;

  @Value("${service.mail.uri}")
  private String mailServiceUri;

  public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
    this.appUserDAO = appUserDAO;
    this.cryptoTool = cryptoTool;
  }

  @Override
  public String registerUser(AppUser appUser) {
    if (appUser.getIsActive())
      return "Вы уже зарегистрированы!";
    else if (appUser.getEmail() != null)
      return "Вам на почту уже было отправлено письмо. Перейдите по ссылке в письме для завершения регистрации.";

    appUser.setUserState(WAIT_FOR_EMAIL_STATE);
    appUserDAO.save(appUser);
    return "Введите, пожалуйста, ваш email.";
  }

  @Override
  public String setEmail(AppUser appUser, String email) {
    try {
      InternetAddress internetAddress = new InternetAddress(email);
      internetAddress.validate();
    } catch (AddressException e) {
      return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel";
    }
    var optional = appUserDAO.findByEmail(email);
    if (optional.isEmpty()) {
      appUser.setEmail(email);
      appUser.setUserState(BASIC_STATE);
      appUser = appUserDAO.save(appUser);

      var cryptoUserId = cryptoTool.hashOf(appUser.getId());
      var response = sendRequestToMailService(cryptoUserId, email);
      if (response.getStatusCode() != HttpStatus.OK) {
        var msg = String.format("Отправка электронного письма на почту %s не удалась", email);
        log.error(msg);
        appUser.setEmail(null);
        appUserDAO.save(appUser);
        return msg;
      }
      return "Вам на почту было отправлено письмо. Перейдите по ссылке в письме для завершения регистрации.";
    } else {
      return "Этот email уже используется. Введите корректный email. Для отмены команды введите /cancel.";
    }
  }

  private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
    var restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    MailParams mailParams = MailParams.builder()
            .id(cryptoUserId)
            .emailTo(email)
            .build();
    HttpEntity<MailParams> request = new HttpEntity<>(mailParams, httpHeaders);
    return restTemplate.exchange(mailServiceUri,
            HttpMethod.POST,
            request,
            String.class);
  }
}
