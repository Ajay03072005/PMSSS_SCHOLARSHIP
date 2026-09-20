package com.pmsss.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiDuplicateResult {
    private boolean possibleDuplicate;
    private double confidence;
    private List<String> matchedFields;
    private List<String> duplicateWithApplicationIds;
    private String details;
}
