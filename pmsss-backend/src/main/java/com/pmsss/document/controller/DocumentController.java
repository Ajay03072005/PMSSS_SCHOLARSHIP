package com.pmsss.document.controller;

import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.enums.DocumentType;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtractedData;
import com.pmsss.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Cloudinary Document Management, Uploads, View, Replace, Delete & Intelligence")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document to Cloudinary storage for an application")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadDocument(
            @RequestParam(value = "applicationUniqueId", required = false) String applicationUniqueId,
            @RequestParam(value = "applicationId", required = false) String applicationId,
            @RequestParam("documentType") String documentTypeStr,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String appIdentifier = applicationUniqueId != null ? applicationUniqueId : applicationId;
        DocumentType docType = DocumentType.fromString(documentTypeStr);

        Document document = documentService.uploadDocument(appIdentifier, docType, file, currentUser);

        Map<String, Object> data = new HashMap<>();
        data.put("documentUniqueId", document.getUniqueId());
        data.put("applicationUniqueId", document.getApplication().getUniqueId());
        data.put("documentType", document.getDocumentType().name());
        data.put("fileName", document.getFileName());
        data.put("status", document.getVerificationStatus().name());
        data.put("version", document.getVersion());
        data.put("cloudinaryUrl", document.getCloudinaryUrl());
        data.put("storageProvider", document.getStorageProvider());

        return ResponseEntity.ok(ApiResponse.ok("Document uploaded successfully", data));
    }

    @PostMapping(value = "/documents/upload/{applicationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Legacy endpoint: Upload a document for an application ID")
    public ResponseEntity<ApiResponse<Document>> uploadDocumentLegacyPath(
            @PathVariable String applicationId,
            @RequestParam("documentType") String documentTypeStr,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DocumentType docType = DocumentType.fromString(documentTypeStr);
        Document document = documentService.uploadDocument(applicationId, docType, file, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Document uploaded successfully", document));
    }

    @PutMapping(value = "/documents/{documentUniqueId}/replace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Replace an existing rejected/resubmitted document with a new version")
    public ResponseEntity<ApiResponse<Map<String, Object>>> replaceDocument(
            @PathVariable String documentUniqueId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Document document = documentService.replaceDocument(documentUniqueId, file, currentUser);

        Map<String, Object> data = new HashMap<>();
        data.put("documentUniqueId", document.getUniqueId());
        data.put("applicationUniqueId", document.getApplication().getUniqueId());
        data.put("documentType", document.getDocumentType().name());
        data.put("fileName", document.getFileName());
        data.put("status", document.getVerificationStatus().name());
        data.put("version", document.getVersion());
        data.put("cloudinaryUrl", document.getCloudinaryUrl());

        return ResponseEntity.ok(ApiResponse.ok("Document replaced successfully with version " + document.getVersion(), data));
    }

    @GetMapping("/documents/{documentUniqueId}/view")
    @Operation(summary = "View/stream document directly in browser with access control")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable String documentUniqueId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Resource resource = documentService.loadDocumentResource(documentUniqueId, currentUser);
        String filename = resource.getFilename() != null ? resource.getFilename().toLowerCase() : "document.pdf";
        String contentType = filename.endsWith(".pdf") ? "application/pdf" : filename.endsWith(".png") ? "image/png" : "image/jpeg";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/documents/view/{fileName:.+}")
    @Operation(summary = "Legacy view endpoint")
    public ResponseEntity<Resource> viewDocumentByFilename(
            @PathVariable String fileName,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Resource resource = documentService.loadDocumentResource(fileName, currentUser);
        String contentType = fileName.toLowerCase().endsWith(".pdf") ? "application/pdf" : "image/jpeg";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/documents/{documentUniqueId}/download")
    @Operation(summary = "Securely download a document")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String documentUniqueId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Resource resource = documentService.downloadDocumentResource(documentUniqueId, currentUser);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/documents/{documentUniqueId}")
    @Operation(summary = "Delete a document from DB and Cloudinary")
    public ResponseEntity<ApiResponse<String>> deleteDocument(
            @PathVariable String documentUniqueId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        documentService.deleteDocument(documentUniqueId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Document deleted successfully", documentUniqueId));
    }

    @GetMapping("/applications/{applicationUniqueId}/documents")
    @Operation(summary = "Get all documents belonging to an application")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsForApplication(
            @PathVariable String applicationUniqueId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<Document> list = documentService.getDocumentsForApplication(applicationUniqueId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Documents retrieved", list));
    }

    @GetMapping("/documents/application/{applicationId}")
    @Operation(summary = "Legacy endpoint: Get all documents belonging to an application ID")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsForApplicationLegacy(
            @PathVariable String applicationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<Document> list = documentService.getDocumentsForApplication(applicationId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Documents retrieved", list));
    }

    @GetMapping("/documents/{documentId}/extracted-data")
    @Operation(summary = "Get OCR extracted structured data for a document")
    public ResponseEntity<ApiResponse<DocumentExtractedData>> getExtractedData(@PathVariable Long documentId) {
        DocumentExtractedData data = documentService.getExtractedDataForDocument(documentId);
        return ResponseEntity.ok(ApiResponse.ok("Extracted data retrieved", data));
    }
}
