package com.justdevthat.controller;

import com.justdevthat.entity.BinaryContent;
import com.justdevthat.service.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequestMapping("/file")
@RestController //Spring не ищет view в ресурсах, а сразу возвращает raw data которую даёт метод, здесь это binary data файла
public class FileController {
  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
  public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
    //TODO для формирования badRequest сделать ControllerAdvice
    var doc = fileService.getDocument(id);
    if (doc == null)
      return ResponseEntity.badRequest().build();

    BinaryContent binaryContent = doc.getBinaryContent();
    FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
    if (fileSystemResource == null)
      return ResponseEntity.internalServerError().build();

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(doc.getMimeType())) // указывает автоматическое определение на основе MimeType, чтобы браузер смог из потока байт создать файл с нужным расширением
            .header("Content-disposition", "attachment; filename=" + doc.getDocName()) // указывает как именно браузеру(клиентскому приложению) правильно воспринимать полученную информацию. В нашем случае файл будет скачан, а не открыт браузером
            .body(fileSystemResource);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
  public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
    //TODO для формирования badRequest сделать ControllerAdvice
    var photo = fileService.getPhoto(id);
    if (photo == null)
      return ResponseEntity.badRequest().build();

    BinaryContent binaryContent = photo.getBinaryContent();
    FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
    if (fileSystemResource == null)
      return ResponseEntity.internalServerError().build();

    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header("Content-disposition", "attachment;")
            .body(fileSystemResource);
  }
}
