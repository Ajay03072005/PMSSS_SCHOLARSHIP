package com.pmsss.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaturalLanguageQueryResponse {
    private String query;
    private String identifiedIntent;
    private String answer;
    private Map<String, Object> metrics;
}
