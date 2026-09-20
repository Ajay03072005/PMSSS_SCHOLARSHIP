package com.pmsss.document.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private String resourceType;
    private Long bytes;
    private String storageProvider; // "CLOUDINARY" or "LOCAL"
    private String storageKey;
    private String storageLocation;
}
