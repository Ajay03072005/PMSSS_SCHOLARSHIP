package com.pmsss.document.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileUploadResponse upload(MultipartFile file, String folderPath, String customFilename);
    Resource loadAsResource(String storageKey);
    byte[] download(String storageKey);
    boolean delete(String storageKey);
    String generateAccessUrl(String storageKey, int expirationMinutes);
}
