package com.pmsss.ai.repository;

import com.pmsss.ai.entity.EligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityRuleRepository extends JpaRepository<EligibilityRule, Long> {
    Optional<EligibilityRule> findByRuleCode(String ruleCode);
    List<EligibilityRule> findByIsActiveTrue();
}
