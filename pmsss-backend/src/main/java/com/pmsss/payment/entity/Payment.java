package com.pmsss.payment.entity;

import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.PaymentStatus;
import com.pmsss.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_pay_app_id", columnList = "application_id"),
        @Index(name = "idx_pay_student_id", columnList = "student_id"),
        @Index(name = "idx_pay_status", columnList = "payment_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false, unique = true, length = 50)
    private String paymentId;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    public String getApplicationNumber() {
        return application != null ? application.getApplicationId() : null;
    }

    public String getStudentEmail() {
        return student != null ? student.getEmail() : null;
    }

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private Long id;
        private String paymentId;
        private Application application;
        private User student;
        private BigDecimal amount;
        private String transactionReference;
        private LocalDateTime paymentDate;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private String failureReason;
        private String bankAccount;
        private String ifscCode;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PaymentBuilder id(Long id) { this.id = id; return this; }
        public PaymentBuilder paymentId(String paymentId) { this.paymentId = paymentId; return this; }
        public PaymentBuilder application(Application application) { this.application = application; return this; }
        public PaymentBuilder student(User student) { this.student = student; return this; }
        public PaymentBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentBuilder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }
        public PaymentBuilder paymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; return this; }
        public PaymentBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public PaymentBuilder failureReason(String failureReason) { this.failureReason = failureReason; return this; }
        public PaymentBuilder bankAccount(String bankAccount) { this.bankAccount = bankAccount; return this; }
        public PaymentBuilder ifscCode(String ifscCode) { this.ifscCode = ifscCode; return this; }
        public PaymentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaymentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Payment build() {
            Payment p = new Payment();
            p.id = this.id;
            p.paymentId = this.paymentId;
            p.application = this.application;
            p.student = this.student;
            p.amount = this.amount;
            p.transactionReference = this.transactionReference;
            p.paymentDate = this.paymentDate;
            p.paymentStatus = this.paymentStatus;
            p.failureReason = this.failureReason;
            p.bankAccount = this.bankAccount;
            p.ifscCode = this.ifscCode;
            p.createdAt = this.createdAt;
            p.updatedAt = this.updatedAt;
            return p;
        }
    }
}

