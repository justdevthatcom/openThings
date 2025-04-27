package com.justdevthat.service.impl;

import com.justdevthat.dao.AppDocumentDAO;
import com.justdevthat.dao.AppPhotoDAO;
import com.justdevthat.dao.BinaryContentDAO;
import com.justdevthat.entity.AppDocument;
import com.justdevthat.entity.AppPhoto;
import com.justdevthat.entity.BinaryContent;
import com.justdevthat.exceptions.UploadFileException;
import com.justdevthat.service.FileService;
import com.justdevthat.service.enums.LinkType;
import com.justdevthat.utils.CryptoTool;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j2
//@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
  @Value("${token}")
  private String token;

  @Value("${service.file_info.uri}")
  private String fileInfoUri;

  @Value("${service.file_storage.uri}")
  private String fileStorageUri;

  @Value("${link.address}")
  private String linkAddress;

  private final AppDocumentDAO appDocumentDAO;
  private final AppPhotoDAO appPhotoDAO;

  private final BinaryContentDAO binaryContentDAO;
  private final CryptoTool cryptoTool;

//  private final Hashids hashids;

  public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO, CryptoTool cryptoTool) {
    this.appDocumentDAO = appDocumentDAO;
    this.appPhotoDAO = appPhotoDAO;
    this.binaryContentDAO = binaryContentDAO;
    this.cryptoTool = cryptoTool;
  }

  @Override
  public AppDocument processDoc(Message telegramMessage) {
    Document telegramDoc = telegramMessage.getDocument();
    var fileId = telegramDoc.getFileId();
    ResponseEntity<String> response = getFilePath(fileId);
    if (response.getStatusCode() == HttpStatus.OK) {
      BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
      var transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
      return appDocumentDAO.save(transientAppDoc);
    } else {
      throw new UploadFileException("Bad response from telegram service: " + response);
    }
  }

  @Override
  public AppPhoto processPhoto(Message telegramMessage) {
    //TODO пока обрабатываем одно фото
    var photoSizeCount = telegramMessage.getPhoto().size();
    int photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
    PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
    var fileId = telegramPhoto.getFileId();
    ResponseEntity<String> response = getFilePath(fileId);
    if (response.getStatusCode() == HttpStatus.OK) {
      BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
      AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
      return appPhotoDAO.save(transientAppPhoto);
    } else {
      throw new UploadFileException("Bad response from telegram service: " + response);
    }
  }


  private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
    var filePath = getFilePath(response);
    var fileInByte = downloadFile(filePath);
    var transientBinaryContent = BinaryContent.builder()
            .fileAsArrayOfBytes(fileInByte)
            .build();
    return binaryContentDAO.save(transientBinaryContent);
  }

  private String getFilePath(ResponseEntity<String> response) {
    var jsonObject = new JSONObject(response.getBody());
    return String.valueOf(jsonObject
            .getJSONObject("result")
            .getString("file_path"));
  }

  private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
    return AppDocument.builder()
            .telegramFileId(telegramDoc.getFileId())
            .docName(telegramDoc.getFileName())
            .binaryContent(persistentBinaryContent)
            .mimeType(telegramDoc.getMimeType())
            .fileSize(telegramDoc.getFileSize())
            .build();
  }

  private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
    return AppPhoto.builder()
            .telegramFileId(telegramPhoto.getFileId())
            .binaryContent(persistentBinaryContent)
            .fileSize(telegramPhoto.getFileSize())
            .build();
  }

  private ResponseEntity<String> getFilePath(String fileId) {
    var restTemplate = new RestTemplate();
    var headers = new HttpHeaders();
    var request = new HttpEntity<>(headers);

    return restTemplate.exchange(
            fileInfoUri,
            HttpMethod.GET,
            request,
            String.class,
            token, fileId
    );
  }

  private byte[] downloadFile(String filePath) {
    var fullUri = fileStorageUri.replace("{token}", token)
            .replace("{filePath}", filePath);
    URL urlObj = null;
    try {
      urlObj = new URL(fullUri);
    } catch (MalformedURLException e) {
      throw new UploadFileException(e);
    }

    //TODO подумать над оптимизацией скачивания файла. Пока качаем одной партией в оперативку
    try (InputStream is = urlObj.openStream()) {
      return is.readAllBytes();
    } catch (IOException e) {
      throw new UploadFileException(urlObj.toExternalForm(), e);
    }
  }

  @Override
  public String generateLink(Long docId, LinkType linkType) {
    var hash = cryptoTool.hashOf(docId);
    return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
//    return linkAddress + "/api/" + linkType + "?id=" + hash;
  }
}
