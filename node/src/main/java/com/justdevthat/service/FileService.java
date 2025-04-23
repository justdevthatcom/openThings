package com.justdevthat.service;

import com.justdevthat.entity.AppDocument;
import com.justdevthat.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
  AppDocument processDoc(Message telegramMessage);
  AppPhoto processPhoto(Message telegramMessage);
}
