package com.pmsss.ocr.service;

import com.pmsss.ocr.model.OcrExtractedData;

import java.io.File;

public interface OcrService {
    OcrExtractedData extractDocumentData(File file, String expectedType);
}
