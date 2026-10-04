package com.pmsss.document.service;

import com.pmsss.application.entity.Application;
import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtraction;
import com.pmsss.document.entity.DocumentMatchResult;

import java.util.List;

public interface DocumentMatchingService {
    List<DocumentMatchResult> matchDocumentWithApplication(Document document, Application application, DocumentExtraction extraction);
    List<DocumentMatchResult> getMatchResultsForDocument(Long documentId);
    List<DocumentMatchResult> getMatchResultsForApplication(Long applicationId);
}
