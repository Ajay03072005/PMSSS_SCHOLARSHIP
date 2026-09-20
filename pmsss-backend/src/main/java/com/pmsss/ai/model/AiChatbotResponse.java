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
public class AiChatbotResponse {
    private String reply;
    private String intent;
    private List<String> suggestedFollowUps;
    private String applicationContext;
}
