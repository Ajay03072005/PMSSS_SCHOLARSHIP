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
}
