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
public class StudentProfile extends BaseEntity {

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getAlternateEmail() { return alternateEmail; }
    public void setAlternateEmail(String alternateEmail) { this.alternateEmail = alternateEmail; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getCurrentAddress() { return currentAddress; }
    public void setCurrentAddress(String currentAddress) { this.currentAddress = currentAddress; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public String getDomicileDistrict() { return domicileDistrict; }
    public void setDomicileDistrict(String domicileDistrict) { this.domicileDistrict = domicileDistrict; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentProfileBuilder builder() {
        return new StudentProfileBuilder();
    }

    public static class StudentProfileBuilder {
        private Long id;
        private User user;
        private String alternateEmail;
        private String emergencyContact;
        private String currentAddress;
        private String permanentAddress;
        private String domicileDistrict;
        private String state;
        private String pincode;
        private LocalDateTime updatedAt;

        public StudentProfileBuilder id(Long id) { this.id = id; return this; }
        public StudentProfileBuilder user(User user) { this.user = user; return this; }
        public StudentProfileBuilder alternateEmail(String alternateEmail) { this.alternateEmail = alternateEmail; return this; }
        public StudentProfileBuilder emergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; return this; }
        public StudentProfileBuilder currentAddress(String currentAddress) { this.currentAddress = currentAddress; return this; }
        public StudentProfileBuilder permanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; return this; }
        public StudentProfileBuilder domicileDistrict(String domicileDistrict) { this.domicileDistrict = domicileDistrict; return this; }
        public StudentProfileBuilder state(String state) { this.state = state; return this; }
        public StudentProfileBuilder pincode(String pincode) { this.pincode = pincode; return this; }
        public StudentProfileBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentProfile build() {
            StudentProfile sp = new StudentProfile();
            sp.id = this.id;
            sp.user = this.user;
            sp.alternateEmail = this.alternateEmail;
            sp.emergencyContact = this.emergencyContact;
            sp.currentAddress = this.currentAddress;
            sp.permanentAddress = this.permanentAddress;
            sp.domicileDistrict = this.domicileDistrict;
            sp.state = this.state;
            sp.pincode = this.pincode;
            sp.updatedAt = this.updatedAt;
            return sp;
        }
    }
}

