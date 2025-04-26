package com.justdevthat.service;

import com.justdevthat.entity.AppDocument;
import com.justdevthat.entity.AppPhoto;
import com.justdevthat.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
  AppDocument getDocument(String id);
  AppPhoto getPhoto(String id);
  FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
