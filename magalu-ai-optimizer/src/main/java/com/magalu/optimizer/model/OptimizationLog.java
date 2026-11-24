package com.magalu.optimizer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OptimizationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private String originalTitle;

    @Column(length = 4000)
    private String originalDescription;

    private String suggestedTitle;

    @Column(length = 4000)
    private String suggestedDescription;

    private String appliedKeywords;
    private String decision;

    private double ctrBefore;
    private double ctrAfter;
    private double conversionBefore;
    private double conversionAfter;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
