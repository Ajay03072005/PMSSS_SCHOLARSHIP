package com.pmsss.ocr.service;

import com.pmsss.ocr.model.OcrExtractedData;
import com.pmsss.ocr.provider.DocumentOcrProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

    private final DocumentOcrProvider ocrProvider;

    public OcrExtractedData extractDocumentData(File file, String expectedType) {
        log.info("Performing OCR and Document Intelligence extraction on: {}", file.getName());
        return ocrProvider.extractFromDocument(file, expectedType);
    }
}
