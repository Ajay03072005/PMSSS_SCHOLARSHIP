package com.pmsss.notification.repository;

import com.pmsss.notification.entity.NotificationLog;
import com.pmsss.notification.enums.NotificationChannel;
import com.pmsss.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    Page<NotificationLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<NotificationLog> findByStatusOrderByCreatedAtDesc(NotificationStatus status, Pageable pageable);
    Page<NotificationLog> findByChannelOrderByCreatedAtDesc(NotificationChannel channel, Pageable pageable);
    List<NotificationLog> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);
}
