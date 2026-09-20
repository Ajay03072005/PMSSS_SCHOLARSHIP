package com.pmsss.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_rules", indexes = {
        @Index(name = "idx_rule_code", columnList = "rule_code"),
        @Index(name = "idx_rule_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_code", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @Column(name = "course_type", length = 100)
    private String courseType; // General, Professional, Medical

    @Column(length = 50)
    private String category; // General, OBC, SC, ST

    @Column(name = "min_percentage")
    private Double minPercentage; // e.g. 60.0

    @Column(name = "max_annual_income", precision = 12, scale = 2)
    private BigDecimal maxAnnualIncome; // e.g. 800000.00

    @Column(name = "domicile_required", nullable = false)
    @Builder.Default
    private Boolean domicileRequired = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
