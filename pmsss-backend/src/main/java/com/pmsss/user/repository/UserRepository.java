package com.pmsss.user.repository;

import com.pmsss.common.enums.RoleType;
import com.pmsss.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByAadhar(String aadhar);

    boolean existsByEmail(String email);

    boolean existsByAadhar(String aadhar);

    Page<User> findByRole(RoleType role, Pageable pageable);

    java.util.List<User> findByRole(RoleType role);

    java.util.List<User> findByRoleIn(java.util.Collection<RoleType> roles);

    long countByRole(RoleType role);
}
