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
public class AiCompletenessResult {
    private boolean complete;
    private List<String> missingItems;
    private List<String> warnings;
    private int completenessPercentage;
}
