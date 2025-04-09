package com.justdevthat.dao;

import com.justdevthat.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserDAO extends JpaRepository<AppUser, Long> {
  AppUser findAppUserByTelegramUserId(Long telegramUserId);
}
