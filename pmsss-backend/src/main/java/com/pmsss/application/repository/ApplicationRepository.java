package com.pmsss.application.repository;

import com.pmsss.application.entity.Application;
import com.pmsss.common.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByApplicationId(String applicationId);

    default Optional<Application> findByUniqueId(String uniqueId) {
        return findByApplicationId(uniqueId);
    }

    Optional<Application> findByApplicationIdAndUserId(String applicationId, Long userId);

    Optional<Application> findByApplicationIdAndDateOfBirth(String applicationId, LocalDate dateOfBirth);

    List<Application> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Application> findByUserIdAndStatus(Long userId, ApplicationStatus status);

    boolean existsByApplicationId(String applicationId);

    boolean existsByAadhar(String aadhar);

    List<Application> findByAadhar(String aadhar);

    List<Application> findByAccountNumber(String accountNumber);

    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:search IS NULL OR lower(a.applicationId) LIKE lower(concat('%', :search, '%')) OR " +
            "lower(a.firstName) LIKE lower(concat('%', :search, '%')) OR " +
            "lower(a.lastName) LIKE lower(concat('%', :search, '%')) OR " +
            "lower(a.email) LIKE lower(concat('%', :search, '%')))")
    Page<Application> searchApplications(@Param("status") ApplicationStatus status,
                                         @Param("search") String search,
                                         Pageable pageable);

    long countByStatus(ApplicationStatus status);

    @Query("SELECT a.status, count(a) FROM Application a GROUP BY a.status")
    List<Object[]> countByStatusGroup();
}
