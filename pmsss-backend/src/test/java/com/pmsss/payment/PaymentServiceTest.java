package com.pmsss.payment;

import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.audit.service.AuditLogService;
import com.pmsss.common.enums.PaymentStatus;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.payment.entity.Payment;
import com.pmsss.payment.entity.PaymentTransaction;
import com.pmsss.payment.repository.PaymentRepository;
import com.pmsss.payment.repository.PaymentTransactionRepository;
import com.pmsss.payment.service.PaymentService;
import com.pmsss.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentTransactionRepository transactionRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PaymentService paymentService;

    private Application sampleApp;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("student@pmsss.gov.in").build();
        sampleApp = Application.builder()
                .id(10L)
                .applicationId("PMSSS2026999")
                .user(user)
                .accountNumber("1234567890")
                .ifscCode("SBIN0001234")
                .build();

        samplePayment = Payment.builder()
                .id(1L)
                .paymentId("PAY123456")
                .application(sampleApp)
                .student(user)
                .amount(new BigDecimal("125000.00"))
                .bankAccount("1234567890")
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void testDisbursePaymentSuccess() {
        when(paymentRepository.findByPaymentId("PAY123456")).thenReturn(Optional.of(samplePayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        Payment result = paymentService.disbursePayment("PAY123456", 2L);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        assertNotNull(result.getTransactionReference());
        verify(transactionRepository, times(1)).save(any(PaymentTransaction.class));
        verify(notificationService, times(1)).sendNotification(any(), any(), any(), any(), any());
    }
}
