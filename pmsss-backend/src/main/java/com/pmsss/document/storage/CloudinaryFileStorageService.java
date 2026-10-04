package com.pmsss.document.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pmsss.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class CloudinaryFileStorageService implements FileStorageService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CloudinaryFileStorageService.class);


    private final Cloudinary cloudinary;
    private final LocalFileStorageService localStorageService;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    private boolean isConfigured() {
        return cloudName != null && !cloudName.isBlank() && !"unconfigured".equalsIgnoreCase(cloudName) &&
               apiKey != null && !apiKey.isBlank() && !"unconfigured".equalsIgnoreCase(apiKey) &&
               apiSecret != null && !apiSecret.isBlank() && !"unconfigured".equalsIgnoreCase(apiSecret);
    }

    @Override
    public FileUploadResponse upload(MultipartFile file, String folderPath, String customFilename) {
        if (!isConfigured()) {
            log.info("Cloudinary credentials not set or incomplete. Falling back to LocalFileStorageService.");
            return localStorageService.upload(file, folderPath, customFilename);
        }

        try {
            String resourceType = "auto";
            if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                resourceType = "image";
            } else if (file.getContentType() != null && file.getContentType().equals("application/pdf")) {
                resourceType = "raw";
            }

            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folderPath != null ? folderPath : "pmsss/documents",
                    "resource_type", resourceType,
                    "use_filename", false,
                    "unique_filename", true
            );

            if (customFilename != null && !customFilename.isBlank()) {
                params.put("public_id", customFilename);
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("url");
            String secureUrl = (String) uploadResult.get("secure_url");
            String format = (String) uploadResult.get("format");
            String resType = (String) uploadResult.get("resource_type");
            Number bytesNum = (Number) uploadResult.get("bytes");
            Long bytes = bytesNum != null ? bytesNum.longValue() : file.getSize();

            log.info("Successfully uploaded file to Cloudinary with publicId: {}", publicId);

            return FileUploadResponse.builder()
                    .publicId(publicId)
                    .url(url)
                    .secureUrl(secureUrl)
                    .format(format)
                    .resourceType(resType)
                    .bytes(bytes)
                    .storageProvider("CLOUDINARY")
                    .storageKey(publicId)
                    .storageLocation(secureUrl)
                    .build();

        } catch (Exception e) {
            log.warn("Cloudinary upload failed ({}). Executing fallback to LocalFileStorageService.", e.getMessage());
            return localStorageService.upload(file, folderPath, customFilename);
        }
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        if (!isConfigured() || (storageKey != null && !storageKey.contains("pmsss/"))) {
            return localStorageService.loadAsResource(storageKey);
        }
        try {
            byte[] data = download(storageKey);
            return new ByteArrayResource(data);
        } catch (Exception e) {
            return localStorageService.loadAsResource(storageKey);
        }
    }

    @Override
    public byte[] download(String storageKey) {
        if (!isConfigured() || (storageKey != null && !storageKey.contains("pmsss/"))) {
            return localStorageService.download(storageKey);
        }
        try {
            String url = cloudinary.url().secure(true).generate(storageKey);
            try (InputStream in = new URL(url).openStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            log.warn("Failed to download from Cloudinary ({}), falling back to local storage.", e.getMessage());
            return localStorageService.download(storageKey);
        }
    }

    @Override
    public boolean delete(String storageKey) {
        if (!isConfigured() || (storageKey != null && !storageKey.contains("pmsss/"))) {
            return localStorageService.delete(storageKey);
        }
        try {
            Map result = cloudinary.uploader().destroy(storageKey, ObjectUtils.emptyMap());
            String resStr = (String) result.get("result");
            return "ok".equalsIgnoreCase(resStr);
        } catch (Exception e) {
            log.warn("Failed to delete resource from Cloudinary ({}), attempting local cleanup.", e.getMessage());
            return localStorageService.delete(storageKey);
        }
    }

    @Override
    public String generateAccessUrl(String storageKey, int expirationMinutes) {
        if (!isConfigured() || (storageKey != null && !storageKey.contains("pmsss/"))) {
            return localStorageService.generateAccessUrl(storageKey, expirationMinutes);
        }
        try {
            return cloudinary.url().secure(true).generate(storageKey);
        } catch (Exception e) {
            return localStorageService.generateAccessUrl(storageKey, expirationMinutes);
        }
    }
}
