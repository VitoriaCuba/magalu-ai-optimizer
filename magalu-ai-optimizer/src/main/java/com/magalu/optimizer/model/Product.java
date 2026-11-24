package com.magalu.optimizer.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String category;
    private String currentTitle;

    @Column(length = 4000)
    private String currentDescription;

    @Column(length = 2000)
    private String technicalSpecs;

    private int qualityScore;
    private String diagnosticNotes;

    private double ctr;
    private double conversionRate;
    private String status;
}
