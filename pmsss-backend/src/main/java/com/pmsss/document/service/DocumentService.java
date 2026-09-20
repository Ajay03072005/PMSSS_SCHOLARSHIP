package com.pmsss.document.service;

import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.audit.service.AuditLogService;
import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.enums.DocumentStatus;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.common.exception.BusinessException;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtractedData;
import com.pmsss.document.repository.DocumentExtractedDataRepository;
import com.pmsss.document.repository.DocumentRepository;
import com.pmsss.document.storage.FileStorageService;
import com.pmsss.document.storage.FileUploadResponse;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.ocr.model.OcrExtractedData;
import com.pmsss.ocr.service.OcrService;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentExtractedDataRepository extractedDataRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final OcrService ocrService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("application/pdf", "image/jpeg", "image/png", "image/jpg");

    public String storeFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) return null;
        FileUploadResponse response = fileStorageService.upload(file, "pmsss/uploads/" + prefix, prefix + "_" + UUID.randomUUID().toString().substring(0, 8));
        return response.getStorageKey();
    }

    @Transactional
    public Document uploadDocument(String appIdentifier, DocumentType docType, MultipartFile file, UserPrincipal currentUser) {
        Application application = resolveApplication(appIdentifier);
        verifyUserAccessToApplication(application, currentUser);

        validateFile(file);

        String docUniqueId = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String folderPath = "pmsss/applications/" + application.getUniqueId() + "/" + docType.name() + "/" + docUniqueId;

        // Check for existing document version
        int nextVersion = 1;
        var existingDocOpt = documentRepository.findByApplicationIdAndDocumentType(application.getId(), docType);
        if (existingDocOpt.isEmpty()) {
            existingDocOpt = documentRepository.findByApplicationUniqueIdAndDocumentType(application.getUniqueId(), docType);
        }

        if (existingDocOpt.isPresent()) {
            Document oldDoc = existingDocOpt.get();
            nextVersion = oldDoc.getVersion() + 1;
            oldDoc.setVerificationStatus(DocumentStatus.RESUBMITTED);
            documentRepository.save(oldDoc);
        }

        FileUploadResponse uploadResponse = null;
        try {
            uploadResponse = fileStorageService.upload(file, folderPath, docUniqueId);

            User user = currentUser != null ? userRepository.findById(currentUser.getId()).orElse(null) : null;

            Document document = Document.builder()
                    .uniqueId(docUniqueId)
                    .application(application)
                    .documentType(docType)
                    .fileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : docUniqueId + ".pdf")
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .storageLocation(uploadResponse.getStorageLocation())
                    .storageKey(uploadResponse.getStorageKey())
                    .storageProvider(uploadResponse.getStorageProvider())
                    .cloudinaryPublicId(uploadResponse.getPublicId())
                    .cloudinaryResourceType(uploadResponse.getResourceType())
                    .cloudinaryUrl(uploadResponse.getSecureUrl())
                    .uploadedBy(user)
                    .verificationStatus(DocumentStatus.PROCESSING)
                    .version(nextVersion)
                    .build();

            document = documentRepository.save(document);

            // Execute OCR Extraction & Intelligence
            processOcrAndMatching(document, application, docType, file);

            document.setVerificationStatus(DocumentStatus.UPLOADED);
            document = documentRepository.save(document);

            // Trigger Notifications & Audit Log
            if (user != null) {
                notificationService.sendNotification(
                        user,
                        application,
                        "DOCUMENT_UPLOADED",
                        "Document Uploaded: " + docType,
                        "Your " + docType + " document has been uploaded and processed."
                );
                auditLogService.logAction(
                        user.getEmail(),
                        currentUser.getAuthorities().toString(),
                        "DOCUMENT_UPLOADED",
                        "Document",
                        docUniqueId,
                        null,
                        docType.name()
                );
            }

            return document;

        } catch (Exception ex) {
            log.error("Error during document upload/saving for application: " + appIdentifier, ex);
            // Cleanup storage resource if DB save fails (Transactional Safety)
            if (uploadResponse != null && uploadResponse.getStorageKey() != null) {
                fileStorageService.delete(uploadResponse.getStorageKey());
            }
            throw new BusinessException("Document upload failed: " + ex.getMessage());
        }
    }

    @Transactional
    public Document replaceDocument(String documentUniqueId, MultipartFile file, UserPrincipal currentUser) {
        Document existingDoc = getDocumentByUniqueId(documentUniqueId);
        Application application = existingDoc.getApplication();
        verifyUserAccessToApplication(application, currentUser);

        validateFile(file);

        // Upload new version
        DocumentType docType = existingDoc.getDocumentType();
        int newVersion = existingDoc.getVersion() + 1;

        // Update existing document status to RESUBMITTED
        existingDoc.setVerificationStatus(DocumentStatus.RESUBMITTED);
        documentRepository.save(existingDoc);

        String newDocUniqueId = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String folderPath = "pmsss/applications/" + application.getUniqueId() + "/" + docType.name() + "/" + newDocUniqueId;

        FileUploadResponse uploadResponse = fileStorageService.upload(file, folderPath, newDocUniqueId);

        User user = currentUser != null ? userRepository.findById(currentUser.getId()).orElse(null) : null;

        Document newDocument = Document.builder()
                .uniqueId(newDocUniqueId)
                .application(application)
                .documentType(docType)
                .fileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : newDocUniqueId + ".pdf")
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .storageLocation(uploadResponse.getStorageLocation())
                .storageKey(uploadResponse.getStorageKey())
                .storageProvider(uploadResponse.getStorageProvider())
                .cloudinaryPublicId(uploadResponse.getPublicId())
                .cloudinaryResourceType(uploadResponse.getResourceType())
                .cloudinaryUrl(uploadResponse.getSecureUrl())
                .uploadedBy(user)
                .verificationStatus(DocumentStatus.PROCESSING)
                .version(newVersion)
                .build();

        newDocument = documentRepository.save(newDocument);

        processOcrAndMatching(newDocument, application, docType, file);

        newDocument.setVerificationStatus(DocumentStatus.UPLOADED);
        newDocument = documentRepository.save(newDocument);

        if (user != null) {
            notificationService.sendNotification(
                    user,
                    application,
                    "CORRECTION_SUBMITTED",
                    "Document Replaced: " + docType,
                    "A corrected version (v" + newVersion + ") of your " + docType + " document has been uploaded."
            );
            auditLogService.logAction(
                    user.getEmail(),
                    currentUser.getAuthorities().toString(),
                    "DOCUMENT_REPLACED",
                    "Document",
                    newDocUniqueId,
                    existingDoc.getUniqueId(),
                    "Version " + newVersion
            );
        }

        return newDocument;
    }

    @Transactional(readOnly = true)
    public Document getDocumentByUniqueId(String documentUniqueId) {
        try {
            Long id = Long.parseLong(documentUniqueId);
            return documentRepository.findById(id)
                    .orElseGet(() -> documentRepository.findByUniqueId(documentUniqueId)
                            .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentUniqueId)));
        } catch (NumberFormatException e) {
            return documentRepository.findByUniqueId(documentUniqueId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document", "uniqueId", documentUniqueId));
        }
    }

    @Transactional(readOnly = true)
    public Resource loadDocumentResource(String documentUniqueId, UserPrincipal currentUser) {
        Document document = getDocumentByUniqueId(documentUniqueId);
        verifyUserAccessToApplication(document.getApplication(), currentUser);

        if (currentUser != null) {
            auditLogService.logAction(
                    currentUser.getUsername(),
                    currentUser.getAuthorities().toString(),
                    "DOCUMENT_VIEWED",
                    "Document",
                    documentUniqueId,
                    null,
                    document.getFileName()
            );
        }

        return fileStorageService.loadAsResource(document.getStorageKey());
    }

    @Transactional(readOnly = true)
    public Resource downloadDocumentResource(String documentUniqueId, UserPrincipal currentUser) {
        Document document = getDocumentByUniqueId(documentUniqueId);
        verifyUserAccessToApplication(document.getApplication(), currentUser);

        if (currentUser != null) {
            auditLogService.logAction(
                    currentUser.getUsername(),
                    currentUser.getAuthorities().toString(),
                    "DOCUMENT_DOWNLOADED",
                    "Document",
                    documentUniqueId,
                    null,
                    document.getFileName()
            );
        }

        return fileStorageService.loadAsResource(document.getStorageKey());
    }

    @Transactional
    public void deleteDocument(String documentUniqueId, UserPrincipal currentUser) {
        Document document = getDocumentByUniqueId(documentUniqueId);
        verifyUserAccessToApplication(document.getApplication(), currentUser);

        // Prevent deletion if application is already verified or locked
        if (com.pmsss.common.enums.ApplicationStatus.SAG_APPROVED.equals(document.getApplication().getStatus()) ||
            com.pmsss.common.enums.ApplicationStatus.COMPLETED.equals(document.getApplication().getStatus())) {
            throw new BusinessException("Cannot delete documents for an application that has been verified/approved.", "DELETION_FORBIDDEN");
        }

        // Delete from Storage
        fileStorageService.delete(document.getStorageKey());

        // Delete extracted OCR metadata if present
        extractedDataRepository.findByDocumentId(document.getId())
                .ifPresent(extractedDataRepository::delete);

        // Delete metadata
        documentRepository.delete(document);

        if (currentUser != null) {
            auditLogService.logAction(
                    currentUser.getUsername(),
                    currentUser.getAuthorities().toString(),
                    "DOCUMENT_DELETED",
                    "Document",
                    documentUniqueId,
                    document.getDocumentType().name(),
                    null
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsForApplication(String appIdentifier, UserPrincipal currentUser) {
        Application application = resolveApplication(appIdentifier);
        verifyUserAccessToApplication(application, currentUser);
        return documentRepository.findByApplicationId(application.getId());
    }

    @Transactional(readOnly = true)
    public DocumentExtractedData getExtractedDataForDocument(Long documentId) {
        return extractedDataRepository.findByDocumentId(documentId).orElse(null);
    }

    private Application resolveApplication(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            return applicationRepository.findById(id)
                    .orElseGet(() -> applicationRepository.findByUniqueId(identifier)
                            .orElseThrow(() -> new ResourceNotFoundException("Application", "identifier", identifier)));
        } catch (NumberFormatException e) {
            return applicationRepository.findByUniqueId(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("Application", "uniqueId", identifier));
        }
    }

    public void verifyUserAccessToApplication(Application application, UserPrincipal currentUser) {
        if (currentUser == null) return; // Permit internal system calls if needed

        String role = currentUser.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_SUPER_ADMIN".equals(role) || "ROLE_ADMIN".equals(role) ||
            "ROLE_SAG_OFFICER".equals(role) || "ROLE_FINANCE_OFFICER".equals(role)) {
            return; // Officers and Admins have authorized review access
        }

        // Student Access Check
        if (application.getUser() != null) {
            Long appOwnerId = application.getUser().getId();
            if (!appOwnerId.equals(currentUser.getId())) {
                throw new AccessDeniedException("Forbidden: You are not authorized to access documents for this application.");
            }
        }
    }

    private void processOcrAndMatching(Document document, Application application, DocumentType docType, MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("ocr_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            OcrExtractedData ocrResult = ocrService.extractDocumentData(tempFile.toFile(), docType.name());

            String detectedType = ocrResult.getDetectedDocumentType();
            document.setAiClassifiedType(detectedType);

            if (!"UNKNOWN".equalsIgnoreCase(detectedType) && !detectedType.equalsIgnoreCase(docType.name())) {
                document.setAiTypeMatch(false);
                document.setAiMismatchWarning("Uploaded document appears to be a " + detectedType + " instead of an " + docType.name() + ".");
            } else {
                document.setAiTypeMatch(true);
            }

            DocumentExtractedData extractedData = DocumentExtractedData.builder()
                    .document(document)
                    .application(application)
                    .extractedName(ocrResult.getName())
                    .extractedDob(ocrResult.getDateOfBirth())
                    .certificateNumber(ocrResult.getCertificateNumber())
                    .institutionName(ocrResult.getInstitution())
                    .courseName(ocrResult.getCourse())
                    .extractedIncome(ocrResult.getIncome())
                    .documentDate(ocrResult.getDocumentDate())
                    .ifscCode(ocrResult.getIfscCode())
                    .bankAccountNumber(ocrResult.getBankAccountNumber())
                    .rawExtractedText(ocrResult.getRawText())
                    .confidenceScore(ocrResult.getConfidence())
                    .extractionSource(ocrResult.getSource())
                    .build();

            extractedDataRepository.save(extractedData);
            Files.deleteIfExists(tempFile);

        } catch (Exception e) {
            log.warn("OCR extraction skipped or failed for document {}: {}", document.getUniqueId(), e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Uploaded file cannot be empty", "FILE_EMPTY");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Uploaded file size exceeds maximum limit of 10MB", "FILE_TOO_LARGE");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String cleanName = originalFilename.trim().toLowerCase();
            int extIdx = cleanName.lastIndexOf('.');
            if (extIdx <= 0 || extIdx == cleanName.length() - 1) {
                throw new BusinessException("Invalid filename. Extension missing.", "INVALID_FILENAME");
            }

            String ext = cleanName.substring(extIdx + 1);
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                throw new BusinessException("Unsupported file format: ." + ext + ". Allowed formats: PDF, JPG, JPEG, PNG", "UNSUPPORTED_FILE_TYPE");
            }

            // Reject executable / script extensions
            if (ext.equals("exe") || ext.equals("sh") || ext.equals("php") || ext.equals("js") || ext.equals("py") || ext.equals("bat")) {
                throw new BusinessException("Security Violation: Executable file upload rejected.", "SECURITY_VIOLATION");
            }
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("Invalid MIME type: " + contentType + ". Allowed types: PDF, JPG, JPEG, PNG", "INVALID_MIME_TYPE");
        }

        // Magic number signature verification
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int read = is.read(header);
            if (read >= 4) {
                // Check PDF (%PDF)
                boolean isPdf = header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46;
                // Check PNG (\x89PNG)
                boolean isPng = (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47;
                // Check JPG (\xFF\xD8\xFF)
                boolean isJpg = (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;

                if (!isPdf && !isPng && !isJpg) {
                    throw new BusinessException("File content header validation failed. File is corrupt or MIME type was spoofed.", "MIME_SPOOFING_DETECTED");
                }
            }
        } catch (IOException e) {
            throw new BusinessException("Could not inspect file contents: " + e.getMessage(), "FILE_READ_ERROR");
        }
    }
}
