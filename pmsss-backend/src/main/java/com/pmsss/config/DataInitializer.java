package com.pmsss.config;

import com.pmsss.application.entity.Application;
import com.pmsss.application.repository.ApplicationRepository;
import com.pmsss.common.enums.ApplicationStatus;
import com.pmsss.common.enums.PaymentStatus;
import com.pmsss.common.enums.RoleType;
import com.pmsss.notification.entity.NotificationLog;
import com.pmsss.notification.enums.NotificationChannel;
import com.pmsss.notification.enums.NotificationStatus;
import com.pmsss.notification.repository.NotificationLogRepository;
import com.pmsss.payment.entity.Payment;
import com.pmsss.payment.repository.PaymentRepository;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);


    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.pmsss.ai.repository.EligibilityRuleRepository eligibilityRuleRepository;
    private final com.pmsss.officer.repository.OfficerWorkloadRepository officerWorkloadRepository;
    private final com.pmsss.officer.repository.ApplicationAssignmentRepository assignmentRepository;
    private final com.pmsss.ai.repository.AnomalyAlertRepository anomalyAlertRepository;
    private final com.pmsss.student.repository.StudentProfileRepository studentProfileRepository;

    @Override
    public void run(String... args) {
        log.info("Starting PMSSS 2.0 Database Initialization & Seeding...");

        // 1. Seed Core Role Users
        seedUserIfNotExists("admin@pmsss.gov.in", "System", "Admin", "Admin@123", "9999999999", "999999999999", RoleType.ROLE_ADMIN);
        seedUserIfNotExists("superadmin@pmsss.gov.in", "Super", "Admin", "SuperAdmin@123", "9999999998", "999999999998", RoleType.ROLE_SUPER_ADMIN);
        seedUserIfNotExists("sag.officer@pmsss.gov.in", "Sunil", "Verma", "Officer@123", "9999999997", "999999999997", RoleType.ROLE_SAG_OFFICER);
        seedUserIfNotExists("finance.officer@pmsss.gov.in", "Anjali", "Gupta", "Finance@123", "9999999996", "999999999996", RoleType.ROLE_FINANCE_OFFICER);
        User student = seedUserIfNotExists("student@pmsss.gov.in", "Ajay", "Kumar", "Student@123", "9876543210", "123456789012", RoleType.ROLE_STUDENT);

        // Additional sample students for queues
        User aarav = seedUserIfNotExists("aarav.sharma@example.com", "Aarav", "Sharma", "Student@123", "9876543211", "123456789013", RoleType.ROLE_STUDENT);
        User zainab = seedUserIfNotExists("zainab.fatima@example.com", "Zainab", "Fatima", "Student@123", "9876543212", "123456789014", RoleType.ROLE_STUDENT);
        User rohit = seedUserIfNotExists("rohit.verma@example.com", "Rohit", "Verma", "Student@123", "9876543213", "123456789015", RoleType.ROLE_STUDENT);

        // 2. Seed Eligibility Rules
        seedEligibilityRules();

        // 3. Seed Officer Workload
        seedOfficerWorkload();

        // 4. Seed Applications in MySQL Database
        seedApplications(student, aarav, zainab, rohit);

        // 5. Seed Payments in MySQL Database
        seedPayments(student, rohit);

        // 6. Seed Notification Logs
        seedNotifications(student);

        // 7. Seed Student Profile
        seedStudentProfile(student);

        // 8. Seed Application Assignments for SAG Officer
        seedApplicationAssignments();

        // 9. Seed Anomaly Alerts
        seedAnomalyAlerts();

        log.info("PMSSS 2.0 Database Initialization & Seeding completed successfully.");
    }

    private void seedEligibilityRules() {
        if (eligibilityRuleRepository.count() == 0) {
            eligibilityRuleRepository.save(com.pmsss.ai.entity.EligibilityRule.builder()
                    .ruleCode("PMSSS_GEN_2026")
                    .courseType("General Degree")
                    .category("All")
                    .minPercentage(60.0)
                    .maxAnnualIncome(new BigDecimal("800000.00"))
                    .domicileRequired(true)
                    .description("PMSSS General Degree scholarship criteria for UTs of J&K and Ladakh. Max income ₹8.00 Lakhs.")
                    .isActive(true)
                    .build());
            eligibilityRuleRepository.save(com.pmsss.ai.entity.EligibilityRule.builder()
                    .ruleCode("PMSSS_PROF_2026")
                    .courseType("Professional Degree (Engineering / Architecture)")
                    .category("All")
                    .minPercentage(60.0)
                    .maxAnnualIncome(new BigDecimal("800000.00"))
                    .domicileRequired(true)
                    .description("PMSSS Professional Engineering Degree scholarship criteria.")
                    .isActive(true)
                    .build());
            log.info("Seeded default PMSSS eligibility rules");
        }
    }

    private void seedOfficerWorkload() {
        userRepository.findByEmail("sag.officer@pmsss.gov.in").ifPresent(officer -> {
            if (officerWorkloadRepository.findByOfficerId(officer.getId()).isEmpty()) {
                officerWorkloadRepository.save(com.pmsss.officer.entity.OfficerWorkload.builder()
                        .officer(officer)
                        .officerName(officer.getFullName())
                        .officerEmail(officer.getEmail())
                        .currentWorkload(3)
                        .maxCapacity(100)
                        .overdueCount(0)
                        .completedToday(2)
                        .isActive(true)
                        .build());
                log.info("Seeded initial OfficerWorkload for {}", officer.getEmail());
            }
        });
    }

    private User seedUserIfNotExists(String email, String firstName, String lastName, String rawPassword, String mobile, String aadhar, RoleType role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .mobile(mobile)
                    .aadhar(aadhar)
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .role(role)
                    .isVerified(true)
                    .isActive(true)
                    .build();
            user = userRepository.save(user);
            log.info("Seeded user: {} ({})", email, role);
            return user;
        });
    }

    private void seedApplications(User student, User aarav, User zainab, User rohit) {
        if (applicationRepository.count() == 0) {
            // App 1: Aarav Sharma (Ready for Review)
            applicationRepository.save(Application.builder()
                    .user(aarav)
                    .applicationId("PMSSS2026000001")
                    .firstName("Aarav")
                    .lastName("Sharma")
                    .email(aarav.getEmail())
                    .mobile(aarav.getMobile())
                    .aadhar(aarav.getAadhar())
                    .dateOfBirth(LocalDate.of(2004, 5, 12))
                    .gender("Male")
                    .category("General")
                    .state("Jammu & Kashmir")
                    .district("Srinagar")
                    .address("Rajbagh Near Bridge, Srinagar, J&K")
                    .pincode("190008")
                    .annualIncome(new BigDecimal("450000.00"))
                    .incomeSource("Salaried Private Sector")
                    .accountHolderName("Aarav Sharma")
                    .accountNumber("987654321013")
                    .ifscCode("SBIN0001234")
                    .bankName("State Bank of India")
                    .branchName("Srinagar Main")
                    .status(ApplicationStatus.READY_FOR_REVIEW)
                    .aiPriorityScore(95)
                    .hasAnomalies(false)
                    .declaration(true)
                    .submittedAt(LocalDateTime.now().minusDays(2))
                    .academicInfo("{\"stream\":\"ENGINEERING\",\"twelfthPercentage\":88.4,\"rollNumber\":\"JKB-12-4412\"}")
                    .build());

            // App 2: Zainab Fatima (Under Review - Has Anomaly)
            applicationRepository.save(Application.builder()
                    .user(zainab)
                    .applicationId("PMSSS2026000002")
                    .firstName("Zainab")
                    .lastName("Fatima")
                    .email(zainab.getEmail())
                    .mobile(zainab.getMobile())
                    .aadhar(zainab.getAadhar())
                    .dateOfBirth(LocalDate.of(2005, 3, 20))
                    .gender("Female")
                    .category("SEBC")
                    .state("Jammu & Kashmir")
                    .district("Anantnag")
                    .address("Khanabal, Anantnag, J&K")
                    .pincode("192101")
                    .annualIncome(new BigDecimal("620000.00"))
                    .incomeSource("Agriculture & Trade")
                    .accountHolderName("Zainab Fatima")
                    .accountNumber("987654321014")
                    .ifscCode("JAKA0ANANTN")
                    .bankName("J&K Bank")
                    .branchName("Khanabal Anantnag")
                    .status(ApplicationStatus.UNDER_REVIEW)
                    .aiPriorityScore(74)
                    .hasAnomalies(true)
                    .declaration(true)
                    .submittedAt(LocalDateTime.now().minusDays(3))
                    .reviewRemarks("Income certificate issued date requires re-validation.")
                    .academicInfo("{\"stream\":\"MEDICAL_NURSING\",\"twelfthPercentage\":81.2,\"rollNumber\":\"JKB-12-8821\"}")
                    .build());

            // App 3: Rohit Verma (Approved / Sent to Finance)
            applicationRepository.save(Application.builder()
                    .user(rohit)
                    .applicationId("PMSSS2026000003")
                    .firstName("Rohit")
                    .lastName("Verma")
                    .email(rohit.getEmail())
                    .mobile(rohit.getMobile())
                    .aadhar(rohit.getAadhar())
                    .dateOfBirth(LocalDate.of(2004, 11, 8))
                    .gender("Male")
                    .category("SC")
                    .state("Jammu & Kashmir")
                    .district("Jammu")
                    .address("Gandhi Nagar, Jammu, J&K")
                    .pincode("180004")
                    .annualIncome(new BigDecimal("320000.00"))
                    .incomeSource("Government Employee (Class IV)")
                    .accountHolderName("Rohit Verma")
                    .accountNumber("987654321015")
                    .ifscCode("PUNB0021300")
                    .bankName("Punjab National Bank")
                    .branchName("Jammu City")
                    .status(ApplicationStatus.SENT_TO_FINANCE)
                    .aiPriorityScore(92)
                    .hasAnomalies(false)
                    .declaration(true)
                    .submittedAt(LocalDateTime.now().minusDays(5))
                    .reviewedAt(LocalDateTime.now().minusDays(1))
                    .reviewRemarks("All documents authenticated against JKBOSE repository. Domicile valid.")
                    .academicInfo("{\"stream\":\"GENERAL\",\"twelfthPercentage\":76.5,\"rollNumber\":\"JKB-12-3109\"}")
                    .build());

            // App 4: Ajay Kumar (Student User's own application)
            applicationRepository.save(Application.builder()
                    .user(student)
                    .applicationId("PMSSS2026000004")
                    .firstName("Ajay")
                    .lastName("Kumar")
                    .email(student.getEmail())
                    .mobile(student.getMobile())
                    .aadhar(student.getAadhar())
                    .dateOfBirth(LocalDate.of(2004, 6, 15))
                    .gender("Male")
                    .category("General")
                    .state("Jammu & Kashmir")
                    .district("Srinagar")
                    .address("House No. 42, Rajbagh, Srinagar, J&K")
                    .pincode("190008")
                    .annualIncome(new BigDecimal("400000.00"))
                    .incomeSource("Business Services")
                    .accountHolderName("Ajay Kumar")
                    .accountNumber("987654321012")
                    .ifscCode("SBIN0001234")
                    .bankName("State Bank of India")
                    .branchName("Main Branch Srinagar")
                    .status(ApplicationStatus.SUBMITTED)
                    .aiPriorityScore(98)
                    .hasAnomalies(false)
                    .declaration(true)
                    .submittedAt(LocalDateTime.now().minusHours(12))
                    .academicInfo("{\"stream\":\"ENGINEERING\",\"twelfthPercentage\":92.0,\"rollNumber\":\"JKB-12-9921\"}")
                    .build());

            log.info("Seeded 4 realistic PMSSS scholarship applications into applications table.");
        }
    }

    private void seedPayments(User student, User rohit) {
        if (paymentRepository.count() == 0) {
            applicationRepository.findByApplicationId("PMSSS2026000004").ifPresent(app -> {
                // Payment 1: Disbursed Maintenance Allowance
                paymentRepository.save(Payment.builder()
                        .paymentId("PAY20260001")
                        .application(app)
                        .student(student)
                        .amount(new BigDecimal("50000.00"))
                        .transactionReference("SBIN202608159981")
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .paymentDate(LocalDateTime.now().minusDays(20))
                        .build());

                // Payment 2: Disbursed Academic Fee to Institute
                paymentRepository.save(Payment.builder()
                        .paymentId("PAY20260002")
                        .application(app)
                        .student(student)
                        .amount(new BigDecimal("125000.00"))
                        .transactionReference("AICTE202609018821")
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .paymentDate(LocalDateTime.now().minusDays(10))
                        .build());

                // Payment 3: Pending 2nd Installment
                paymentRepository.save(Payment.builder()
                        .paymentId("PAY20260003")
                        .application(app)
                        .student(student)
                        .amount(new BigDecimal("50000.00"))
                        .transactionReference("UTR_PENDING")
                        .paymentStatus(PaymentStatus.PENDING)
                        .build());

                log.info("Seeded 3 DBT payment records for application PMSSS2026000004");
            });

            applicationRepository.findByApplicationId("PMSSS2026000003").ifPresent(app -> {
                paymentRepository.save(Payment.builder()
                        .paymentId("PAY20260004")
                        .application(app)
                        .student(rohit)
                        .amount(new BigDecimal("50000.00"))
                        .transactionReference("UTR_PENDING_DISBURSE")
                        .paymentStatus(PaymentStatus.PENDING)
                        .build());
            });
        }
    }

    private void seedNotifications(User student) {
        if (notificationLogRepository.count() == 0) {
            notificationLogRepository.save(NotificationLog.builder()
                    .recipient(student.getEmail())
                    .user(student)
                    .notificationType("APPLICATION_SUBMITTED")
                    .channel(NotificationChannel.EMAIL)
                    .title("PMSSS 2026 Application Successfully Received")
                    .message("Dear Ajay Kumar, your PMSSS scholarship application (ID: PMSSS2026000004) has been successfully submitted and queued for verification.")
                    .status(NotificationStatus.SENT)
                    .retryCount(1)
                    .maxRetries(3)
                    .sentAt(LocalDateTime.now().minusHours(12))
                    .build());

            notificationLogRepository.save(NotificationLog.builder()
                    .recipient(student.getMobile())
                    .user(student)
                    .notificationType("APPLICATION_SUBMITTED")
                    .channel(NotificationChannel.SMS)
                    .title("PMSSS SMS Alert")
                    .message("PMSSS: Application PMSSS2026000004 submitted. Check status at pmsss.aicte-india.org")
                    .status(NotificationStatus.SENT)
                    .retryCount(1)
                    .maxRetries(3)
                    .sentAt(LocalDateTime.now().minusHours(12))
                    .build());

            notificationLogRepository.save(NotificationLog.builder()
                    .recipient(student.getEmail())
                    .user(student)
                    .notificationType("DOCUMENT_VERIFICATION")
                    .channel(NotificationChannel.IN_APP)
                    .title("Automated Pre-Validation Passed")
                    .message("All your uploaded documents (10th/12th marksheets, domicile, income certificate) passed automated OCR pre-checks.")
                    .status(NotificationStatus.SENT)
                    .retryCount(0)
                    .maxRetries(3)
                    .sentAt(LocalDateTime.now().minusHours(6))
                    .build());

            log.info("Seeded initial notification logs in notification_logs table.");
        }
    }

    private void seedStudentProfile(User student) {
        if (studentProfileRepository.findByUserId(student.getId()).isEmpty()) {
            studentProfileRepository.save(com.pmsss.student.entity.StudentProfile.builder()
                    .user(student)
                    .alternateEmail("ajay.alternate@gmail.com")
                    .emergencyContact("9876543299")
                    .currentAddress("House No. 42, Rajbagh, Srinagar, J&K")
                    .permanentAddress("House No. 42, Rajbagh, Srinagar, J&K")
                    .domicileDistrict("Srinagar")
                    .state("Jammu & Kashmir")
                    .pincode("190008")
                    .build());
            log.info("Seeded student profile for user: {}", student.getEmail());
        }
    }

    private void seedApplicationAssignments() {
        if (assignmentRepository.count() == 0) {
            userRepository.findByEmail("sag.officer@pmsss.gov.in").ifPresent(officer -> {
                // Assignment 1: Aarav Sharma - Quick Review
                assignmentRepository.save(com.pmsss.officer.entity.ApplicationAssignment.builder()
                        .applicationId("PMSSS2026000001")
                        .officer(officer)
                        .officerEmail(officer.getEmail())
                        .officerName(officer.getFullName())
                        .assignedBy("SYSTEM_AUTO_ASSIGN")
                        .assignedAt(LocalDateTime.now().minusDays(1))
                        .slaDueAt(LocalDateTime.now().plusHours(36))
                        .priority("HIGH")
                        .reviewCategory("QUICK_REVIEW")
                        .status("ACTIVE")
                        .triageReason("All OCR matches match 100%; Income certificate authenticated; Clean cross-check")
                        .build());

                // Assignment 2: Zainab Fatima - Needs Attention
                assignmentRepository.save(com.pmsss.officer.entity.ApplicationAssignment.builder()
                        .applicationId("PMSSS2026000002")
                        .officer(officer)
                        .officerEmail(officer.getEmail())
                        .officerName(officer.getFullName())
                        .assignedBy("SYSTEM_AUTO_ASSIGN")
                        .assignedAt(LocalDateTime.now().minusDays(1))
                        .slaDueAt(LocalDateTime.now().plusHours(18))
                        .priority("HIGH")
                        .reviewCategory("NEEDS_ATTENTION")
                        .status("ACTIVE")
                        .triageReason("Income certificate date older than 1 year; Aadhaar name spelling minor variance")
                        .build());

                // Assignment 3: Ajay Kumar - Quick Review
                assignmentRepository.save(com.pmsss.officer.entity.ApplicationAssignment.builder()
                        .applicationId("PMSSS2026000004")
                        .officer(officer)
                        .officerEmail(officer.getEmail())
                        .officerName(officer.getFullName())
                        .assignedBy("SYSTEM_AUTO_ASSIGN")
                        .assignedAt(LocalDateTime.now().minusHours(6))
                        .slaDueAt(LocalDateTime.now().plusHours(42))
                        .priority("MEDIUM")
                        .reviewCategory("QUICK_REVIEW")
                        .status("ACTIVE")
                        .triageReason("CBSE 12th marks 92% verified; Domicile certificate J&K verified")
                        .build());

                log.info("Seeded 3 application assignments for SAG officer in application_assignments table.");
            });
        }
    }

    private void seedAnomalyAlerts() {
        if (anomalyAlertRepository.count() == 0) {
            anomalyAlertRepository.save(com.pmsss.ai.entity.AnomalyAlertEntity.builder()
                    .applicationId("PMSSS2026000002")
                    .studentName("Zainab Fatima")
                    .anomalyType("INCOME_CERTIFICATE_EXPIRED")
                    .reason("Income certificate issued date is older than 12 months from application cutoff.")
                    .severity(com.pmsss.common.enums.AnomalySeverity.HIGH)
                    .isResolved(false)
                    .detectedAt(LocalDateTime.now().minusDays(2))
                    .build());

            anomalyAlertRepository.save(com.pmsss.ai.entity.AnomalyAlertEntity.builder()
                    .applicationId("PMSSS2026000002")
                    .studentName("Zainab Fatima")
                    .anomalyType("NAME_SPELLING_VARIANCE")
                    .reason("Aadhaar card spelling 'Zaynab' does not match 10th marksheet spelling 'Zainab'.")
                    .severity(com.pmsss.common.enums.AnomalySeverity.MEDIUM)
                    .isResolved(false)
                    .detectedAt(LocalDateTime.now().minusDays(2))
                    .build());

            log.info("Seeded 2 anomaly alerts in anomaly_alerts table for PMSSS2026000002.");
        }
    }
}
