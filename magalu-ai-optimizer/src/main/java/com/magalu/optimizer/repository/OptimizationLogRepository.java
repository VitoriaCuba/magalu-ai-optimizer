package com.magalu.optimizer.repository;

import com.magalu.optimizer.model.OptimizationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptimizationLogRepository extends JpaRepository<OptimizationLog, Long> {
    List<OptimizationLog> findAllByOrderByIdDesc();
}
