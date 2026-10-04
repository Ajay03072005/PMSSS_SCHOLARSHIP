package com.pmsss.document.storage;

import com.pmsss.common.exception.BusinessException;
import com.pmsss.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LocalFileStorageService.class);


    @Value("${app.upload.dir:uploads}")
    private String baseUploadDir;

    @Override
    public FileUploadResponse upload(MultipartFile file, String folderPath, String customFilename) {
        try {
            Path targetDir = Paths.get(baseUploadDir, folderPath != null ? folderPath : "").toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf");
            String extension = "";
            int extIdx = originalFilename.lastIndexOf('.');
            if (extIdx > 0) {
                extension = originalFilename.substring(extIdx);
            }

            String filename = (customFilename != null ? customFilename : "doc_" + UUID.randomUUID().toString().substring(0, 8)) + extension;
            Path targetLocation = targetDir.resolve(filename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String relativeKey = (folderPath != null && !folderPath.isBlank()) ? folderPath + "/" + filename : filename;
            String resourceType = (file.getContentType() != null && file.getContentType().startsWith("image/")) ? "image" : "raw";

            return FileUploadResponse.builder()
                    .publicId("local/" + relativeKey)
                    .url("/api/v1/documents/view/" + filename)
                    .secureUrl("/api/v1/documents/view/" + filename)
                    .format(extension.replace(".", ""))
                    .resourceType(resourceType)
                    .bytes(file.getSize())
                    .storageProvider("LOCAL")
                    .storageKey(relativeKey)
                    .storageLocation(targetLocation.toString())
                    .build();

        } catch (IOException ex) {
            log.error("Could not store file locally", ex);
            throw new BusinessException("Failed to store file: " + ex.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        try {
            Path filePath = Paths.get(baseUploadDir).resolve(storageKey).toAbsolutePath().normalize();
            if (!Files.exists(filePath)) {
                // Try resolve basename if path fails
                filePath = Paths.get(baseUploadDir).resolve(Paths.get(storageKey).getFileName().toString()).toAbsolutePath().normalize();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + storageKey);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + storageKey);
        }
    }

    @Override
    public byte[] download(String storageKey) {
        try {
            Resource resource = loadAsResource(storageKey);
            return resource.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new BusinessException("Failed to read document bytes: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String storageKey) {
        try {
            Path filePath = Paths.get(baseUploadDir).resolve(storageKey).toAbsolutePath().normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete local file: {}", storageKey, e);
            return false;
        }
    }

    @Override
    public String generateAccessUrl(String storageKey, int expirationMinutes) {
        return "/api/v1/documents/view/" + storageKey;
    }
}
