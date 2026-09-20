package com.pmsss.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmsss.application.entity.Application;
import com.pmsss.application.entity.ApplicationStatusHistory;
import com.pmsss.application.repository.ApplicationHistoryRepository;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.application.request.ApplicationSubmitRequest;
import com.pmsss.application.response.ApplicationResponse;
import com.pmsss.application.response.ApplicationTrackResponse;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.exception.BusinessException;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final ApplicationValidationService applicationValidationService;
    private final com.pmsss.officer.service.OfficerAssignmentService officerAssignmentService;

    @Transactional
    public ApplicationResponse submitOrUpdateApplication(Long userId, ApplicationSubmitRequest request, Map<String, String> uploadedFiles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user already has an existing draft application
        Optional<Application> existingOpt = applicationRepository.findByUserIdAndStatus(userId, ApplicationStatus.DRAFT);
        Application application = existingOpt.orElseGet(() -> Application.builder()
                .user(user)
                .applicationId(generateUniqueApplicationId())
                .firstName(user.getFirstName() != null ? user.getFirstName() : "Student")
                .lastName(user.getLastName() != null ? user.getLastName() : "User")
                .email(user.getEmail() != null ? user.getEmail() : "student@pmsss.local")
                .mobile(user.getMobile() != null ? user.getMobile() : "9876543210")
                .aadhar(user.getAadhar() != null ? user.getAadhar() : "123456789012")
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth() : LocalDate.of(2005, 1, 15))
                .status(ApplicationStatus.DRAFT)
                .build());

        // Update fields from request
        if (request.getPersonalInfo() != null) {
            ApplicationSubmitRequest.PersonalInfo p = request.getPersonalInfo();
            if (p.getFirstName() != null) application.setFirstName(p.getFirstName());
            if (p.getMiddleName() != null) application.setMiddleName(p.getMiddleName());
            if (p.getLastName() != null) application.setLastName(p.getLastName());
            if (p.getDateOfBirth() != null) application.setDateOfBirth(p.getDateOfBirth());
            if (p.getGender() != null) application.setGender(p.getGender());
            if (p.getCategory() != null) application.setCategory(p.getCategory());
            if (p.getAadhar() != null) application.setAadhar(p.getAadhar());
            if (p.getMobile() != null) application.setMobile(p.getMobile());
            if (p.getEmail() != null) application.setEmail(p.getEmail());
            if (p.getAddress() != null) application.setAddress(p.getAddress());
            if (p.getDistrict() != null) application.setDistrict(p.getDistrict());
            if (p.getState() != null) application.setState(p.getState());
            if (p.getPincode() != null) application.setPincode(p.getPincode());
        }

        if (request.getAcademicInfo() != null) {
            try {
                application.setAcademicInfo(objectMapper.writeValueAsString(request.getAcademicInfo()));
            } catch (Exception e) {
                application.setAcademicInfo(request.getAcademicInfo().toString());
            }
        }

        if (request.getFamilyInfo() != null) {
            ApplicationSubmitRequest.FamilyInfo f = request.getFamilyInfo();
            if (f.getFather() != null) {
                application.setFatherName(f.getFather().getName());
                application.setFatherOccupation(f.getFather().getOccupation());
                application.setFatherMobile(f.getFather().getMobile());
            }
            if (f.getMother() != null) {
                application.setMotherName(f.getMother().getName());
                application.setMotherOccupation(f.getMother().getOccupation());
                application.setMotherMobile(f.getMother().getMobile());
            }
            if (f.getAnnualIncome() != null) application.setAnnualIncome(f.getAnnualIncome());
            if (f.getIncomeSource() != null) application.setIncomeSource(f.getIncomeSource());
        }

        if (request.getBankDetails() != null) {
            ApplicationSubmitRequest.BankDetails b = request.getBankDetails();
            if (b.getAccountHolderName() != null) application.setAccountHolderName(b.getAccountHolderName());
            if (b.getAccountNumber() != null) application.setAccountNumber(b.getAccountNumber());
            if (b.getIfscCode() != null) application.setIfscCode(b.getIfscCode());
            if (b.getBankName() != null) application.setBankName(b.getBankName());
            if (b.getBranchName() != null) application.setBranchName(b.getBranchName());
        }

        if (request.getDeclaration() != null) {
            application.setDeclaration(request.getDeclaration());
        }

        // Apply uploaded files
        if (uploadedFiles != null) {
            if (uploadedFiles.containsKey("photo")) application.setPhoto(uploadedFiles.get("photo"));
            if (uploadedFiles.containsKey("aadhar")) application.setAadharDoc(uploadedFiles.get("aadhar"));
            if (uploadedFiles.containsKey("domicile")) application.setDomicile(uploadedFiles.get("domicile"));
            if (uploadedFiles.containsKey("income")) application.setIncomeCert(uploadedFiles.get("income"));
            if (uploadedFiles.containsKey("tenthMarksheet")) application.setTenthMarksheet(uploadedFiles.get("tenthMarksheet"));
            if (uploadedFiles.containsKey("twelfthMarksheet")) application.setTwelfthMarksheet(uploadedFiles.get("twelfthMarksheet"));
            if (uploadedFiles.containsKey("admissionLetter")) application.setAdmissionLetter(uploadedFiles.get("admissionLetter"));
            if (uploadedFiles.containsKey("bankPassbook")) application.setBankPassbook(uploadedFiles.get("bankPassbook"));
        }

        boolean isSubmit = Boolean.TRUE.equals(request.getSubmit());
        if (isSubmit) {
            application.setStatus(ApplicationStatus.SUBMITTED);
            application.setSubmittedAt(LocalDateTime.now());
        }

        application = applicationRepository.save(application);

        if (isSubmit) {
            recordStatusHistory(application, "draft", ApplicationStatus.SUBMITTED, "Application submitted by student", userId);
            notificationService.sendNotification(user, application, "APPLICATION_SUBMITTED",
                    "Application Submitted Successfully",
                    "Your application " + application.getApplicationId() + " has been submitted for automated pre-validation.");

            // 1. Run Automated Pre-Validation Engine
            ApplicationValidationService.PreValidationResult valResult = applicationValidationService.performPreValidation(application);
            if (valResult.isPassed()) {
                // 2. Smart Officer Assignment to Officer Queue
                officerAssignmentService.assignApplication(application);
            }
        }

        return mapToResponse(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(Long userId) {
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(String applicationId, Long userId) {
        Application application;
        if (userId != null) {
            application = applicationRepository.findByApplicationIdAndUserId(applicationId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        } else {
            application = applicationRepository.findByApplicationId(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        }
        return mapToResponse(application);
    }

    @Transactional(readOnly = true)
    public ApplicationTrackResponse trackApplication(String applicationId, LocalDate dateOfBirth) {
        Application app = applicationRepository.findByApplicationIdAndDateOfBirth(applicationId, dateOfBirth)
                .orElseThrow(() -> new BusinessException("Application not found or Date of Birth does not match", "INVALID_TRACK_CREDENTIALS"));

        List<ApplicationStatusHistory> history = historyRepository.findByApplicationIdOrderByCreatedAtAsc(app.getId());

        List<ApplicationTrackResponse.HistoryItemDto> historyDtos = history.stream()
                .map(h -> ApplicationTrackResponse.HistoryItemDto.builder()
                        .status(h.getStatus().name())
                        .remarks(h.getRemarks())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ApplicationTrackResponse.builder()
                .applicationId(app.getApplicationId())
                .status(app.getStatus())
                .statusDescription(getStatusDescription(app.getStatus()))
                .submittedAt(app.getSubmittedAt())
                .reviewedAt(app.getReviewedAt())
                .reviewRemarks(app.getReviewRemarks())
                .personalInfo(ApplicationTrackResponse.PersonalInfoDto.builder()
                        .firstName(app.getFirstName())
                        .lastName(app.getLastName())
                        .email(app.getEmail())
                        .mobile(app.getMobile())
                        .build())
                .statusHistory(historyDtos)
                .build();
    }

    @Transactional
    public ApplicationResponse updateStatus(String applicationId, ApplicationStatus newStatus, String remarks, Long adminId) {
        Application application = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));

        String oldStatus = application.getStatus().name();
        application.setStatus(newStatus);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewedBy(adminId);
        application.setReviewRemarks(remarks);

        application = applicationRepository.save(application);
        recordStatusHistory(application, oldStatus, newStatus, remarks, adminId);

        notificationService.sendNotification(application.getUser(), application, "STATUS_UPDATE",
                "Application Status Updated",
                "Your application status is now: " + newStatus.name() + (remarks != null ? ". Remarks: " + remarks : ""));

        return mapToResponse(application);
    }

    @Transactional
    public void deleteApplication(String applicationId, Long adminId) {
        Application application = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));
        applicationRepository.delete(application);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> searchApplications(ApplicationStatus status, String search, Pageable pageable) {
        return applicationRepository.searchApplications(status, search, pageable)
                .map(this::mapToResponse);
    }

    public void recordStatusHistory(Application application, String oldStatus, ApplicationStatus newStatus, String remarks, Long updatedBy) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(application)
                .oldStatus(oldStatus)
                .status(newStatus)
                .remarks(remarks)
                .updatedBy(updatedBy)
                .build();
        historyRepository.save(history);
    }

    private String generateUniqueApplicationId() {
        int year = Year.now().getValue();
        String candidate;
        do {
            int randomNum = 100000 + new Random().nextInt(900000);
            candidate = "PMSSS" + year + randomNum;
        } while (applicationRepository.existsByApplicationId(candidate));
        return candidate;
    }

    public ApplicationResponse mapToResponse(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .userId(a.getUser() != null ? a.getUser().getId() : null)
                .applicationId(a.getApplicationId())
                .applicantName(a.getFirstName() + " " + a.getLastName())
                .email(a.getEmail())
                .mobile(a.getMobile())
                .aadhar(a.getAadhar())
                .dateOfBirth(a.getDateOfBirth())
                .gender(a.getGender())
                .category(a.getCategory())
                .district(a.getDistrict())
                .state(a.getState())
                .pincode(a.getPincode())
                .address(a.getAddress())
                .academicInfo(a.getAcademicInfo())
                .fatherName(a.getFatherName())
                .motherName(a.getMotherName())
                .annualIncome(a.getAnnualIncome())
                .incomeSource(a.getIncomeSource())
                .accountHolderName(a.getAccountHolderName())
                .accountNumber(a.getAccountNumber())
                .ifscCode(a.getIfscCode())
                .bankName(a.getBankName())
                .branchName(a.getBranchName())
                .photo(a.getPhoto())
                .aadharDoc(a.getAadharDoc())
                .domicile(a.getDomicile())
                .incomeCert(a.getIncomeCert())
                .tenthMarksheet(a.getTenthMarksheet())
                .twelfthMarksheet(a.getTwelfthMarksheet())
                .admissionLetter(a.getAdmissionLetter())
                .bankPassbook(a.getBankPassbook())
                .status(a.getStatus())
                .declaration(a.getDeclaration())
                .submittedAt(a.getSubmittedAt())
                .reviewedAt(a.getReviewedAt())
                .reviewRemarks(a.getReviewRemarks())
                .aiPriorityScore(a.getAiPriorityScore())
                .hasAnomalies(a.getHasAnomalies())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private String getStatusDescription(ApplicationStatus status) {
        switch (status) {
            case DRAFT: return "Your application is currently saved as draft. Complete all sections and submit.";
            case SUBMITTED: return "Application submitted successfully. Waiting for document verification queue.";
            case DOCUMENT_VERIFICATION: return "Your uploaded documents are being reviewed by the SAG verification officer.";
            case SAG_REVIEW: return "Documents verified. SAG committee reviewing scholarship eligibility.";
            case SAG_APPROVED: return "Application approved by SAG officer. Forwarded to finance for disbursement.";
            case SAG_REJECTED: return "Application was rejected. Check remarks for details.";
            case SENT_TO_FINANCE: return "Sent to finance department for bank verification and sanctioning.";
            case PAYMENT_PROCESSING: return "Payment instruction generated and processing through PFMS/DBT.";
            case PAYMENT_COMPLETED:
            case COMPLETED: return "Scholarship disbursed successfully to your bank account.";
            default: return "Application in progress.";
        }
    }
}
