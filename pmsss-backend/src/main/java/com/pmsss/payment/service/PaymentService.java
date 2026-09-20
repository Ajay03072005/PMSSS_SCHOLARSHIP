package com.pmsss.payment.service;

import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.application.service.ApplicationService;
import com.pmsss.audit.service.AuditLogService;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.enums.PaymentStatus;
import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.notification.service.NotificationService;
import com.pmsss.payment.entity.Payment;
import com.pmsss.payment.entity.PaymentTransaction;
import com.pmsss.payment.repository.PaymentRepository;
import com.pmsss.payment.repository.PaymentTransactionRepository;
import com.pmsss.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public Payment initiatePayment(String applicationId, BigDecimal amount, Long financeOfficerId) {
        Application app = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "applicationId", applicationId));

        String paymentId = "PAY" + System.currentTimeMillis() + (int)(Math.random() * 900 + 100);

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .application(app)
                .student(app.getUser())
                .amount(amount != null ? amount : new BigDecimal("125000.00")) // default engineering/pmsss slab
                .bankAccount(app.getAccountNumber())
                .ifscCode(app.getIfscCode())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        applicationService.updateStatus(applicationId, ApplicationStatus.SENT_TO_FINANCE, "Sanctioned for DBT payment disbursement", financeOfficerId);

        auditLogService.logAction("FINANCE_OFFICER", "FINANCE", "PAYMENT_INITIATED", "Payment", payment.getPaymentId(), null, "PENDING");
        return payment;
    }

    @Transactional
    public Payment disbursePayment(String paymentId, Long financeOfficerId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));

        String txnRef = "DBT" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference(txnRef);
        payment.setPaymentDate(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionReference(txnRef)
                .amount(payment.getAmount())
                .status(PaymentStatus.SUCCESS)
                .gatewayResponse("PFMS DBT DISBURSEMENT SUCCESS. AC: " + payment.getBankAccount())
                .build();
        transactionRepository.save(transaction);

        Application application = payment.getApplication();
        applicationService.updateStatus(application.getApplicationId(), ApplicationStatus.PAYMENT_COMPLETED,
                "Scholarship amount ₹" + payment.getAmount() + " successfully transferred via DBT. Ref: " + txnRef, financeOfficerId);

        notificationService.sendNotification(payment.getStudent(), application, "PAYMENT_COMPLETED",
                "Scholarship Payment Disbursed",
                "Congratulations! An amount of ₹" + payment.getAmount() + " has been successfully disbursed to your bank account (" + payment.getBankAccount() + "). Transaction Ref: " + txnRef);

        auditLogService.logAction("FINANCE_OFFICER", "FINANCE", "PAYMENT_PROCESSED", "Payment", payment.getPaymentId(), "PENDING", "SUCCESS");
        return payment;
    }

    @Transactional
    public Payment failPayment(String paymentId, String reason, Long financeOfficerId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment = paymentRepository.save(payment);

        auditLogService.logAction("FINANCE_OFFICER", "FINANCE", "PAYMENT_FAILED", "Payment", payment.getPaymentId(), "PENDING", "FAILED: " + reason);
        return payment;
    }

    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByPaymentStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsForStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<PaymentTransaction> getTransactions(Long paymentId) {
        return transactionRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }
}
