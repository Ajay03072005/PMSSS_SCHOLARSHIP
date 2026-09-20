package com.pmsss.audit.service;

import com.pmsss.audit.entity.AuditLog;
import com.pmsss.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logAction(String username, String role, String action, String entityName, String entityId, String oldValue, String newValue) {
        try {
            AuditLog logEntry = AuditLog.builder()
                    .username(username != null ? username : "SYSTEM")
                    .role(role)
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build();
            return auditLogRepository.save(logEntry);
        } catch (Exception e) {
            log.warn("Failed to write audit log: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsForEntity(String entityName, String entityId, Pageable pageable) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(entityName, entityId, pageable);
    }
}
