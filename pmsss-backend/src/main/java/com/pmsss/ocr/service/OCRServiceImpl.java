package com.pmsss.ocr.service;

import com.pmsss.ocr.model.OcrExtractedData;
import com.pmsss.ocr.provider.DocumentOcrProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class OCRServiceImpl implements OcrService {

    private final DocumentOcrProvider ocrProvider;

    @Override
    public OcrExtractedData extractDocumentData(File file, String expectedType) {
        log.info("Executing OCR and Document Intelligence extraction on file: {}", file != null ? file.getName() : "null");
        if (file == null || !file.exists()) {
            return OcrExtractedData.builder()
                    .rawText("File missing or unreadable")
                    .confidence(0.0)
                    .source("FILE_SYSTEM")
                    .detectedDocumentType("UNKNOWN")
                    .build();
        }
        return ocrProvider.extractFromDocument(file, expectedType);
    }
}
