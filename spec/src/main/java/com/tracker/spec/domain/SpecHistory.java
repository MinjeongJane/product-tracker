package com.tracker.spec.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "spec_history")
public class SpecHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private String specContent;

    @Column(nullable = false)
    private String changeReason;

    protected SpecHistory() {}

    public SpecHistory(Long productId, int version, String specContent, String changeReason) {
        this.productId = productId;
        this.version = version;
        this.specContent = specContent;
        this.changeReason = changeReason;
    }
}