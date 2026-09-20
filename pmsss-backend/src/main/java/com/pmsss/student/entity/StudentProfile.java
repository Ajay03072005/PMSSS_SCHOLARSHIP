package com.pmsss.student.entity;

import com.pmsss.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String alternateEmail;
    private String emergencyContact;
    private String currentAddress;
    private String permanentAddress;
    private String domicileDistrict;
    private String state; // J&K or Ladakh
    private String pincode;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
