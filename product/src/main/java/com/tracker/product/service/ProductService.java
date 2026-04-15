package com.tracker.product.service;

import com.tracker.common.exception.BusinessException;
import com.tracker.common.exception.ErrorCode;
import com.tracker.product.domain.Product;
import com.tracker.product.domain.ProductStatus;
import com.tracker.product.domain.ProductStatusTransitionValidator;
import com.tracker.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStatusTransitionValidator transitionValidator;

    public ProductService(ProductRepository productRepository,
                          ProductStatusTransitionValidator transitionValidator) {
        this.productRepository = productRepository;
        this.transitionValidator = transitionValidator;
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product updateStatus(Long id, ProductStatus next) {
        Product product = findById(id);
        transitionValidator.validate(product.getStatus(), next);
        product.changeStatus(next);
        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        if (product.getStatus() != ProductStatus.IDEA) {
            throw new BusinessException(ErrorCode.PRODUCT_DELETE_NOT_ALLOWED);
        }
        productRepository.delete(product);
    }
}
