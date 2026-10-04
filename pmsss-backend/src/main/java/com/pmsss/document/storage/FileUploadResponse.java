package com.pmsss.document.storage;

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

    public FileUploadResponse() {}

    public FileUploadResponse(String publicId, String url, String secureUrl, String format, String resourceType, Long bytes, String storageProvider, String storageKey, String storageLocation) {
        this.publicId = publicId;
        this.url = url;
        this.secureUrl = secureUrl;
        this.format = format;
        this.resourceType = resourceType;
        this.bytes = bytes;
        this.storageProvider = storageProvider;
        this.storageKey = storageKey;
        this.storageLocation = storageLocation;
    }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSecureUrl() { return secureUrl; }
    public void setSecureUrl(String secureUrl) { this.secureUrl = secureUrl; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public Long getBytes() { return bytes; }
    public void setBytes(Long bytes) { this.bytes = bytes; }

    public String getStorageProvider() { return storageProvider; }
    public void setStorageProvider(String storageProvider) { this.storageProvider = storageProvider; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public static FileUploadResponseBuilder builder() {
        return new FileUploadResponseBuilder();
    }

    public static class FileUploadResponseBuilder {
        private String publicId;
        private String url;
        private String secureUrl;
        private String format;
        private String resourceType;
        private Long bytes;
        private String storageProvider;
        private String storageKey;
        private String storageLocation;

        public FileUploadResponseBuilder publicId(String publicId) { this.publicId = publicId; return this; }
        public FileUploadResponseBuilder url(String url) { this.url = url; return this; }
        public FileUploadResponseBuilder secureUrl(String secureUrl) { this.secureUrl = secureUrl; return this; }
        public FileUploadResponseBuilder format(String format) { this.format = format; return this; }
        public FileUploadResponseBuilder resourceType(String resourceType) { this.resourceType = resourceType; return this; }
        public FileUploadResponseBuilder bytes(Long bytes) { this.bytes = bytes; return this; }
        public FileUploadResponseBuilder storageProvider(String storageProvider) { this.storageProvider = storageProvider; return this; }
        public FileUploadResponseBuilder storageKey(String storageKey) { this.storageKey = storageKey; return this; }
        public FileUploadResponseBuilder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }

        public FileUploadResponse build() {
            return new FileUploadResponse(publicId, url, secureUrl, format, resourceType, bytes, storageProvider, storageKey, storageLocation);
        }
    }
}

