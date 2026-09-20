package com.pmsss.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pmsss.common.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationTrackResponse {

    private String applicationId;
    private ApplicationStatus status;
    private String statusDescription;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewRemarks;

    private PersonalInfoDto personalInfo;
    private List<HistoryItemDto> statusHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalInfoDto {
        private String firstName;
        private String lastName;
        private String email;
        private String mobile;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryItemDto {
        private String status;
        private String remarks;
        private LocalDateTime createdAt;
    }
}
