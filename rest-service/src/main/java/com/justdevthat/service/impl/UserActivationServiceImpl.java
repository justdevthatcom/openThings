package com.justdevthat.service.impl;

import com.justdevthat.dao.AppUserDAO;
import com.justdevthat.entity.AppUser;
import com.justdevthat.utils.CryptoTool;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {
  private final AppUserDAO appUserDAO;
  private final CryptoTool cryptoTool;

  public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
    this.appUserDAO = appUserDAO;
    this.cryptoTool = cryptoTool;
  }

  @Override
  public boolean activation(String cryptoUserId) {
    Long userId = cryptoTool.idOf(cryptoUserId);
    Optional<AppUser> userOptional = appUserDAO.findById(userId);
    if (userOptional.isPresent()) {
      AppUser user = userOptional.get();
      user.setIsActive(true);
      appUserDAO.save(user);
      return true;
    }
    return false;
  }
}
