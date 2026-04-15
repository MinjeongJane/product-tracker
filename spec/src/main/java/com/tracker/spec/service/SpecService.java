package com.tracker.spec.service;

import com.tracker.common.exception.BusinessException;
import com.tracker.common.exception.ErrorCode;
import com.tracker.spec.domain.SpecHistory;
import com.tracker.spec.repository.SpecHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecService {

    private final SpecHistoryRepository specHistoryRepository;

    public SpecService(SpecHistoryRepository specHistoryRepository) {
        this.specHistoryRepository = specHistoryRepository;
    }

    @Transactional
    public SpecHistory save(Long productId, String specContent, String changeReason) {
        int nextVersion = specHistoryRepository.findTopByProductIdOrderByVersionDesc(productId)
                .map(latest -> latest.getVersion() + 1)
                .orElse(1);
        SpecHistory specHistory = new SpecHistory(productId, nextVersion, specContent, changeReason);
        return specHistoryRepository.save(specHistory);
    }

    public List<SpecHistory> findAll(Long productId) {
        return specHistoryRepository.findAllByProductIdOrderByVersionDesc(productId);
    }

    public SpecHistory findLatest(Long productId) {
        return specHistoryRepository.findTopByProductIdOrderByVersionDesc(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPEC_NOT_FOUND));
    }

    public SpecHistory findByVersion(Long productId, int version) {
        return specHistoryRepository.findByProductIdAndVersion(productId, version)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPEC_NOT_FOUND));
    }
}