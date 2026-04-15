package com.tracker.product.service;

import com.tracker.common.exception.BusinessException;
import com.tracker.product.domain.Product;
import com.tracker.product.domain.ProductStatus;
import com.tracker.product.domain.ProductStatusTransitionValidator;
import com.tracker.product.repository.ProductRepository;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStatusTransitionValidator transitionValidator;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품을 생성하면 저장 후 반환한다")
    void create_validProduct_returnsSaved() {
        Product product = new Product("상품명", "설명", "hong");
        given(productRepository.save(any())).willReturn(product);

        Product result = productService.create(product);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("전체 상품 목록을 반환한다")
    void findAll_returnsAllProducts() {
        given(productRepository.findAll()).willReturn(List.of(
                new Product("상품1", "설명1", "hong"),
                new Product("상품2", "설명2", "lee")
        ));

        List<Product> result = productService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 BusinessException을 던진다")
    void findById_notFound_throwsBusinessException() {
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("허용된 상태 전이 시 상태가 변경된다")
    void updateStatus_validTransition_changesStatus() {
        Product product = new Product("상품명", "설명", "hong");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        willDoNothing().given(transitionValidator).validate(any(), any());

        productService.updateStatus(1L, ProductStatus.SPEC_DONE);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.SPEC_DONE);
    }

    @Test
    @DisplayName("허용되지 않은 상태 전이 시 BusinessException을 던진다")
    void updateStatus_invalidTransition_throwsBusinessException() {
        Product product = new Product("상품명", "설명", "hong");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        willThrow(BusinessException.class).given(transitionValidator).validate(any(), any());

        assertThatThrownBy(() -> productService.updateStatus(1L, ProductStatus.DEV))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("IDEA 상태 상품은 삭제된다")
    void delete_ideaStatus_deletesProduct() {
        Product product = new Product("상품명", "설명", "hong");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("IDEA 상태가 아닌 상품 삭제 시 BusinessException을 던진다")
    void delete_nonIdeaStatus_throwsBusinessException() {
        Product product = new Product("상품명", "설명", "hong");
        product.changeStatus(ProductStatus.SPEC_DONE);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(BusinessException.class);
    }
}