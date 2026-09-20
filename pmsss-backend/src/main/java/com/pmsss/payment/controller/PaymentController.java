package com.pmsss.payment.controller;

import com.pmsss.auth.security.UserPrincipal;
import com.pmsss.common.enums.PaymentStatus;
import com.pmsss.common.response.ApiResponse;
import com.pmsss.common.response.PageResponse;
import com.pmsss.payment.entity.Payment;
import com.pmsss.payment.entity.PaymentTransaction;
import com.pmsss.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Scholarship disbursement, DBT processing, and payment transaction tracking")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Initiate scholarship disbursement for an approved application")
    public ResponseEntity<ApiResponse<Payment>> initiatePayment(
            @RequestParam String applicationId,
            @RequestParam(required = false) BigDecimal amount,
            @AuthenticationPrincipal UserPrincipal officer) {

        Payment payment = paymentService.initiatePayment(applicationId, amount, officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Payment initiated successfully", payment));
    }

    @PostMapping("/disburse/{paymentId}")
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Execute DBT disbursement and mark payment completed")
    public ResponseEntity<ApiResponse<Payment>> disbursePayment(
            @PathVariable String paymentId,
            @AuthenticationPrincipal UserPrincipal officer) {

        Payment payment = paymentService.disbursePayment(paymentId, officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Payment disbursed successfully", payment));
    }

    @PostMapping("/fail/{paymentId}")
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Mark payment failed with reason")
    public ResponseEntity<ApiResponse<Payment>> failPayment(
            @PathVariable String paymentId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal officer) {

        Payment payment = paymentService.failPayment(paymentId, reason, officer.getId());
        return ResponseEntity.ok(ApiResponse.ok("Payment status marked as FAILED", payment));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current student's payments")
    public ResponseEntity<ApiResponse<List<Payment>>> getMyPayments(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<Payment> list = paymentService.getPaymentsForStudent(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved", list));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "List payments by status (Finance Officer / Admin)")
    public ResponseEntity<ApiResponse<PageResponse<Payment>>> listPayments(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        Page<Payment> pageResult = paymentService.getPaymentsByStatus(paymentStatus, PageRequest.of(page, size, Sort.by("createdAt").descending()));

        PageResponse<Payment> response = PageResponse.<Payment>builder()
                .content(pageResult.getContent())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved", response));
    }

    @GetMapping("/{paymentId}/transactions")
    @Operation(summary = "Get transaction history for a payment")
    public ResponseEntity<ApiResponse<List<PaymentTransaction>>> getTransactions(@PathVariable Long paymentId) {
        List<PaymentTransaction> list = paymentService.getTransactions(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Transactions retrieved", list));
    }
}
