package com.pmsss.document.entity;

import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.DocumentStatus;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_doc_app_id", columnList = "application_id"),
        @Index(name = "idx_doc_type", columnList = "document_type"),
        @Index(name = "idx_doc_status", columnList = "verification_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true, length = 64)
    private String uniqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "storage_location", nullable = false, length = 500)
    private String storageLocation;

    @Column(name = "storage_key", length = 255)
    private String storageKey;

    @Column(name = "storage_provider", length = 30)
    @Builder.Default
    private String storageProvider = "CLOUDINARY";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    @Builder.Default
    private DocumentStatus verificationStatus = DocumentStatus.UPLOADED;

    @Builder.Default
    private Integer version = 1;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // AI Classification & Validation Flags
    @Column(name = "ai_classified_type", length = 50)
    private String aiClassifiedType;

    @Column(name = "ai_type_match")
    private Boolean aiTypeMatch;

    @Column(name = "ai_mismatch_warning")
    private String aiMismatchWarning;

    // Cloudinary Storage Metadata
    @Column(name = "cloudinary_public_id", length = 255)
    private String cloudinaryPublicId;

    @Column(name = "cloudinary_resource_type", length = 50)
    private String cloudinaryResourceType;

    @Column(name = "cloudinary_url", length = 500)
    private String cloudinaryUrl;

    @CreationTimestamp
    @Column(name = "uploaded_timestamp", updatable = false)
    private LocalDateTime uploadedTimestamp;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    public String getStorageProvider() { return storageProvider; }
    public void setStorageProvider(String storageProvider) { this.storageProvider = storageProvider; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    public DocumentStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(DocumentStatus verificationStatus) { this.verificationStatus = verificationStatus; }

    public Integer getVersion() { return version != null ? version : 1; }
    public void setVersion(Integer version) { this.version = version; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getAiClassifiedType() { return aiClassifiedType; }
    public void setAiClassifiedType(String aiClassifiedType) { this.aiClassifiedType = aiClassifiedType; }

    public Boolean getAiTypeMatch() { return aiTypeMatch; }
    public void setAiTypeMatch(Boolean aiTypeMatch) { this.aiTypeMatch = aiTypeMatch; }

    public String getAiMismatchWarning() { return aiMismatchWarning; }
    public void setAiMismatchWarning(String aiMismatchWarning) { this.aiMismatchWarning = aiMismatchWarning; }

    public String getCloudinaryPublicId() { return cloudinaryPublicId; }
    public void setCloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; }

    public String getCloudinaryResourceType() { return cloudinaryResourceType; }
    public void setCloudinaryResourceType(String cloudinaryResourceType) { this.cloudinaryResourceType = cloudinaryResourceType; }

    public String getCloudinaryUrl() { return cloudinaryUrl; }
    public void setCloudinaryUrl(String cloudinaryUrl) { this.cloudinaryUrl = cloudinaryUrl; }

    public LocalDateTime getUploadedTimestamp() { return uploadedTimestamp; }
    public void setUploadedTimestamp(LocalDateTime uploadedTimestamp) { this.uploadedTimestamp = uploadedTimestamp; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static DocumentBuilder builder() {
        return new DocumentBuilder();
    }

    public static class DocumentBuilder {
        private Long id;
        private String uniqueId;
        private Application application;
        private DocumentType documentType;
        private String fileName;
        private String fileType;
        private Long fileSize;
        private String storageLocation;
        private String storageKey;
        private String storageProvider = "CLOUDINARY";
        private User uploadedBy;
        private DocumentStatus verificationStatus = DocumentStatus.UPLOADED;
        private Integer version = 1;
        private String rejectionReason;
        private String aiClassifiedType;
        private Boolean aiTypeMatch;
        private String aiMismatchWarning;
        private String cloudinaryPublicId;
        private String cloudinaryResourceType;
        private String cloudinaryUrl;
        private LocalDateTime uploadedTimestamp;
        private LocalDateTime updatedAt;

        public DocumentBuilder id(Long id) { this.id = id; return this; }
        public DocumentBuilder uniqueId(String uniqueId) { this.uniqueId = uniqueId; return this; }
        public DocumentBuilder application(Application application) { this.application = application; return this; }
        public DocumentBuilder documentType(DocumentType documentType) { this.documentType = documentType; return this; }
        public DocumentBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public DocumentBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public DocumentBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public DocumentBuilder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }
        public DocumentBuilder storageKey(String storageKey) { this.storageKey = storageKey; return this; }
        public DocumentBuilder storageProvider(String storageProvider) { this.storageProvider = storageProvider; return this; }
        public DocumentBuilder uploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; return this; }
        public DocumentBuilder verificationStatus(DocumentStatus verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public DocumentBuilder version(Integer version) { this.version = version; return this; }
        public DocumentBuilder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public DocumentBuilder aiClassifiedType(String aiClassifiedType) { this.aiClassifiedType = aiClassifiedType; return this; }
        public DocumentBuilder aiTypeMatch(Boolean aiTypeMatch) { this.aiTypeMatch = aiTypeMatch; return this; }
        public DocumentBuilder aiMismatchWarning(String aiMismatchWarning) { this.aiMismatchWarning = aiMismatchWarning; return this; }
        public DocumentBuilder cloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; return this; }
        public DocumentBuilder cloudinaryResourceType(String cloudinaryResourceType) { this.cloudinaryResourceType = cloudinaryResourceType; return this; }
        public DocumentBuilder cloudinaryUrl(String cloudinaryUrl) { this.cloudinaryUrl = cloudinaryUrl; return this; }
        public DocumentBuilder uploadedTimestamp(LocalDateTime uploadedTimestamp) { this.uploadedTimestamp = uploadedTimestamp; return this; }
        public DocumentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Document build() {
            Document doc = new Document();
            doc.id = this.id;
            doc.uniqueId = this.uniqueId;
            doc.application = this.application;
            doc.documentType = this.documentType;
            doc.fileName = this.fileName;
            doc.fileType = this.fileType;
            doc.fileSize = this.fileSize;
            doc.storageLocation = this.storageLocation;
            doc.storageKey = this.storageKey;
            doc.storageProvider = this.storageProvider;
            doc.uploadedBy = this.uploadedBy;
            doc.verificationStatus = this.verificationStatus;
            doc.version = this.version;
            doc.rejectionReason = this.rejectionReason;
            doc.aiClassifiedType = this.aiClassifiedType;
            doc.aiTypeMatch = this.aiTypeMatch;
            doc.aiMismatchWarning = this.aiMismatchWarning;
            doc.cloudinaryPublicId = this.cloudinaryPublicId;
            doc.cloudinaryResourceType = this.cloudinaryResourceType;
            doc.cloudinaryUrl = this.cloudinaryUrl;
            doc.uploadedTimestamp = this.uploadedTimestamp;
            doc.updatedAt = this.updatedAt;
            return doc;
        }
    }
}

