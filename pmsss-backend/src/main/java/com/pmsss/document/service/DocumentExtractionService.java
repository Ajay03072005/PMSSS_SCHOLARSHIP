package com.pmsss.document.service;

import com.pmsss.document.entity.Document;
import com.pmsss.document.entity.DocumentExtraction;
import com.pmsss.ocr.model.OcrExtractedData;

public interface DocumentExtractionService {
    DocumentExtraction processAndSaveExtraction(Document document, OcrExtractedData ocrData);
    DocumentExtraction getExtractionForDocument(Long documentId);
}
