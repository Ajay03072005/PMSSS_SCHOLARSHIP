package com.pmsss.audit.repository;

import com.pmsss.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, String entityId, Pageable pageable);
    Page<AuditLog> findByUsernameOrderByTimestampDesc(String username, Pageable pageable);
    List<AuditLog> findTop50ByOrderByTimestampDesc();
}
