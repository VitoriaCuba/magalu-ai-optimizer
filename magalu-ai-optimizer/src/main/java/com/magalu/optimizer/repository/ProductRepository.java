package com.magalu.optimizer.repository;

import com.magalu.optimizer.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByQualityScoreAsc();
}
