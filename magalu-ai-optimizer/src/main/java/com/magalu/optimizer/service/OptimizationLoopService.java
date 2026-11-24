package com.magalu.optimizer.service;

import com.magalu.optimizer.model.OptimizationLog;
import com.magalu.optimizer.model.Product;
import com.magalu.optimizer.repository.OptimizationLogRepository;
import com.magalu.optimizer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OptimizationLoopService {

    private final ProductRepository productRepository;
    private final OptimizationLogRepository logRepository;

    public OptimizationLog approveAndSimulateLearning(Long logId) {
        OptimizationLog log = logRepository.findById(logId).orElseThrow();
        Product product = log.getProduct();

        double oldCtr = product.getCtr();
        double oldConv = product.getConversionRate();

        product.setCurrentTitle(log.getSuggestedTitle());
        product.setCurrentDescription(log.getSuggestedDescription());
        product.setStatus("APPROVED");

        double newCtr = Math.round((oldCtr * 1.32) * 100.0) / 100.0;
        double newConv = Math.round((oldConv * 1.25) * 100.0) / 100.0;

        log.setCtrBefore(oldCtr);
        log.setCtrAfter(newCtr);
        log.setConversionBefore(oldConv);
        log.setConversionAfter(newConv);
        log.setDecision("WINNER_APPLIED");
        log.setUpdatedAt(LocalDateTime.now());

        product.setCtr(newCtr);
        product.setConversionRate(newConv);
        product.setQualityScore(94);
        product.setDiagnosticNotes("Otimizado e validado pelo motor de aprendizado");

        productRepository.save(product);
        return logRepository.save(log);
    }
}
