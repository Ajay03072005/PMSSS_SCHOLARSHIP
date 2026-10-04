package com.pmsss.document.repository;

import com.pmsss.document.entity.DocumentMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentMatchResultRepository extends JpaRepository<DocumentMatchResult, Long> {

    Optional<DocumentMatchResult> findByUniqueId(String uniqueId);

    List<DocumentMatchResult> findByDocumentId(Long documentId);

    List<DocumentMatchResult> findByApplicationId(Long applicationId);

    void deleteByDocumentId(Long documentId);
}
