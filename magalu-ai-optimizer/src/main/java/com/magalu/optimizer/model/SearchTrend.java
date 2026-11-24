package com.magalu.optimizer.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SearchTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private String category;
    private String source;
    private String growth;
}
