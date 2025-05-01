package com.justdevthat.service;

import com.justdevthat.entity.AppUser;

public interface AppUserService {
  String registerUser(AppUser appUser);
  String setEmail(AppUser appUser, String email);
}
