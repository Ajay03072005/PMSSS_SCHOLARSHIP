package com.pmsss.payment.repository;

import com.pmsss.common.enums.PaymentStatus;
import com.pmsss.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByStudentId(Long studentId);
    List<Payment> findByApplicationId(Long applicationId);
    Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);
    long countByPaymentStatus(PaymentStatus status);
}
