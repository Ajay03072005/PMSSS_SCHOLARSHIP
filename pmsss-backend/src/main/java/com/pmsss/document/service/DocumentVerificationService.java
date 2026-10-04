package com.pmsss.document.service;

import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtraction;
import com.pmsss.document.entity.DocumentMatchResult;
import com.pmsss.document.entity.DocumentVerification;
import com.pmsss.user.entity.User;

import java.util.List;

public interface DocumentVerificationService {
    DocumentVerification evaluateAndCreateVerification(Document document, DocumentExtraction extraction, List<DocumentMatchResult> matchResults);
    DocumentVerification manualVerification(Long documentId, String status, String notes, User officer);
    DocumentVerification getLatestVerificationForDocument(Long documentId);
}
