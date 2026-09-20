package com.pmsss.student.service;

import com.pmsss.common.exception.ResourceNotFoundException;
import com.pmsss.student.entity.StudentProfile;
import com.pmsss.student.repository.StudentProfileRepository;
import com.pmsss.user.entity.User;
import com.pmsss.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StudentProfile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    StudentProfile profile = StudentProfile.builder()
                            .user(user)
                            .state("J&K")
                            .build();
                    return profileRepository.save(profile);
                });
    }

    @Transactional
    public StudentProfile updateProfile(Long userId, StudentProfile updated) {
        StudentProfile existing = getProfileByUserId(userId);
        if (updated.getAlternateEmail() != null) existing.setAlternateEmail(updated.getAlternateEmail());
        if (updated.getEmergencyContact() != null) existing.setEmergencyContact(updated.getEmergencyContact());
        if (updated.getCurrentAddress() != null) existing.setCurrentAddress(updated.getCurrentAddress());
        if (updated.getPermanentAddress() != null) existing.setPermanentAddress(updated.getPermanentAddress());
        if (updated.getDomicileDistrict() != null) existing.setDomicileDistrict(updated.getDomicileDistrict());
        if (updated.getState() != null) existing.setState(updated.getState());
        if (updated.getPincode() != null) existing.setPincode(updated.getPincode());
        return profileRepository.save(existing);
    }
}
