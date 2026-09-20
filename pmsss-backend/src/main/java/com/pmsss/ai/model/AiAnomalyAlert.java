package com.pmsss.ai.model;

import com.pmsss.common.enums.AnomalySeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnomalyAlert {
    private String id;
    private String applicationId;
    private String studentName;
    private String anomalyType;
    private String reason;
    private AnomalySeverity severity; // LOW, MEDIUM, HIGH
    private LocalDateTime detectedAt;
    private boolean resolved;
}
