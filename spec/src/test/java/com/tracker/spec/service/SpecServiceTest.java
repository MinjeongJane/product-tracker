package com.tracker.spec.service;

import com.tracker.common.exception.BusinessException;
import com.tracker.spec.domain.SpecHistory;
import com.tracker.spec.repository.SpecHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SpecServiceTest {

    @Mock
    private SpecHistoryRepository specHistoryRepository;

    @InjectMocks
    private SpecService specService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("첫 번째 스펙 등록 시 version은 1이다")
    void save_firstSpec_versionIsOne() {
        given(specHistoryRepository.findTopByProductIdOrderByVersionDesc(1L)).willReturn(Optional.empty());
        SpecHistory saved = new SpecHistory(1L, 1, "스펙 내용", "최초 등록");
        given(specHistoryRepository.save(any())).willReturn(saved);

        SpecHistory result = specService.save(1L, "스펙 내용", "최초 등록");

        assertThat(result.getVersion()).isEqualTo(1);
        verify(specHistoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("두 번째 스펙 등록 시 version은 이전 version + 1이다")
    void save_secondSpec_versionIncremented() {
        SpecHistory latest = new SpecHistory(1L, 3, "이전 스펙", "이전 변경");
        given(specHistoryRepository.findTopByProductIdOrderByVersionDesc(1L)).willReturn(Optional.of(latest));
        SpecHistory saved = new SpecHistory(1L, 4, "새 스펙", "새 변경");
        given(specHistoryRepository.save(any())).willReturn(saved);

        SpecHistory result = specService.save(1L, "새 스펙", "새 변경");

        assertThat(result.getVersion()).isEqualTo(4);
    }

    @Test
    @DisplayName("스펙 이력 전체를 버전 내림차순으로 반환한다")
    void findAll_returnsAllSpecs() {
        given(specHistoryRepository.findAllByProductIdOrderByVersionDesc(1L)).willReturn(List.of(
                new SpecHistory(1L, 2, "스펙2", "변경2"),
                new SpecHistory(1L, 1, "스펙1", "변경1")
        ));

        List<SpecHistory> result = specService.findAll(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getVersion()).isEqualTo(2);
    }

    @Test
    @DisplayName("스펙이 없을 때 최신 버전 조회 시 BusinessException을 던진다")
    void findLatest_notFound_throwsBusinessException() {
        given(specHistoryRepository.findTopByProductIdOrderByVersionDesc(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> specService.findLatest(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 버전 조회 시 BusinessException을 던진다")
    void findByVersion_notFound_throwsBusinessException() {
        given(specHistoryRepository.findByProductIdAndVersion(1L, 99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> specService.findByVersion(1L, 99))
                .isInstanceOf(BusinessException.class);
    }
}