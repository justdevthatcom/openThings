package com.justdevthat.service;

import com.justdevthat.entity.AppDocument;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
  AppDocument processDoc(Message externalMessage);
}
