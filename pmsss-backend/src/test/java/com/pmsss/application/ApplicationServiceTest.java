package com.pmsss.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmsss.application.entity.Application;
import com.pmsss.application.entity.ApplicationStatusHistory;
import com.pmsss.application.repository.ApplicationHistoryRepository;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.application.response.ApplicationResponse;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationHistoryRepository historyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApplicationService applicationService;

    private Application sampleApp;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).email("student@pmsss.gov.in").build();
        sampleApp = Application.builder()
                .id(10L)
                .applicationId("PMSSS2026123456")
                .user(sampleUser)
                .firstName("Ajay")
                .lastName("Kumar")
                .email("student@pmsss.gov.in")
                .mobile("9876543210")
                .dateOfBirth(LocalDate.of(2003, 7, 3))
                .status(ApplicationStatus.SUBMITTED)
                .build();
    }

    @Test
    void testGetApplicationById() {
        when(applicationRepository.findByApplicationIdAndUserId("PMSSS2026123456", 1L))
                .thenReturn(Optional.of(sampleApp));

        ApplicationResponse res = applicationService.getApplicationById("PMSSS2026123456", 1L);

        assertNotNull(res);
        assertEquals("PMSSS2026123456", res.getApplicationId());
        assertEquals(ApplicationStatus.SUBMITTED, res.getStatus());
    }

    @Test
    void testUpdateStatusRecordsHistory() {
        when(applicationRepository.findByApplicationId("PMSSS2026123456"))
                .thenReturn(Optional.of(sampleApp));
        when(applicationRepository.save(any(Application.class))).thenReturn(sampleApp);

        ApplicationResponse res = applicationService.updateStatus(
                "PMSSS2026123456", ApplicationStatus.DOCUMENT_VERIFICATION, "Verified", 2L
        );

        assertNotNull(res);
        verify(historyRepository, times(1)).save(any(ApplicationStatusHistory.class));
        verify(notificationService, times(1)).sendNotification(any(), any(), any(), any(), any());
    }
}
