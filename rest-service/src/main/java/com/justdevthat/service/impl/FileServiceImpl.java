package com.justdevthat.service.impl;

import com.justdevthat.dao.AppDocumentDAO;
import com.justdevthat.dao.AppPhotoDAO;
import com.justdevthat.entity.AppDocument;
import com.justdevthat.entity.AppPhoto;
import com.justdevthat.service.FileService;
import com.justdevthat.utils.CryptoTool;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class FileServiceImpl implements FileService {
  private final AppDocumentDAO appDocumentDAO;
  private final AppPhotoDAO appPhotoDAO;
  private final CryptoTool cryptoTool;

  public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
    this.appDocumentDAO = appDocumentDAO;
    this.appPhotoDAO = appPhotoDAO;
    this.cryptoTool = cryptoTool;
  }

  @Override
  public AppDocument getDocument(String hashId) {
    // дешифруем сперва хеш пришедшего от rest-запроса Id
    Long id = cryptoTool.idOf(hashId);
    if (id == null)
      return null;
    return appDocumentDAO.findById(id).orElse(null);
  }

  @Override
  public AppPhoto getPhoto(String hashId) {
    // дешифруем сперва хеш пришедшего от rest-запроса Id
    Long id = cryptoTool.idOf(hashId);
    if (id == null)
      return null;
    return appPhotoDAO.findById(id).orElse(null);
  }

//  @Override
//  public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
//    try {
//      //TODO уточнить достаточно ли randomUUID для генерации имени временного файла
//      UUID randomUUID = UUID.randomUUID();
//      File temp = File.createTempFile("tempFile_" + randomUUID, "bin");
//      temp.deleteOnExit(); // удаляет файл из постоянной памяти при выходе из приложения
//      FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
//      return new FileSystemResource(temp);
//    } catch (IOException e) {
//      log.error(e);
//      return null;
//    }
//  }
}
