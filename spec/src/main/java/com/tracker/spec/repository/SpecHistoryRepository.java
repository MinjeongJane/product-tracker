package com.tracker.spec.repository;

import com.tracker.spec.domain.SpecHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecHistoryRepository extends JpaRepository<SpecHistory, Long> {

    List<SpecHistory> findAllByProductIdOrderByVersionDesc(Long productId);

    Optional<SpecHistory> findTopByProductIdOrderByVersionDesc(Long productId);

    Optional<SpecHistory> findByProductIdAndVersion(Long productId, int version);
}