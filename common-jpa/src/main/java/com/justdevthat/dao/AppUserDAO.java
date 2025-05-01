package com.justdevthat.dao;

import com.justdevthat.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserDAO extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByTelegramUserId(Long telegramUserId);
  Optional<AppUser> findById(Long id);
  Optional<AppUser> findByEmail(String email);
}
