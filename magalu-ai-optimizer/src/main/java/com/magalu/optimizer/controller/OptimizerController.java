package com.magalu.optimizer.controller;

import com.magalu.optimizer.model.OptimizationLog;
import com.magalu.optimizer.model.Product;
import com.magalu.optimizer.model.SearchTrend;
import com.magalu.optimizer.repository.OptimizationLogRepository;
import com.magalu.optimizer.repository.ProductRepository;
import com.magalu.optimizer.repository.SearchTrendRepository;
import com.magalu.optimizer.service.AiContentService;
import com.magalu.optimizer.service.OptimizationLoopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OptimizerController {

    private final ProductRepository productRepository;
    private final SearchTrendRepository trendRepository;
    private final OptimizationLogRepository logRepository;
    private final AiContentService aiContentService;
    private final OptimizationLoopService optimizationLoopService;

    @GetMapping("/trends")
    public List<SearchTrend> listTrends() {
        return trendRepository.findAll();
    }

    @GetMapping("/products")
    public List<Product> listProducts() {
        return productRepository.findAllByOrderByQualityScoreAsc();
    }

    @GetMapping("/optimizations")
    public List<OptimizationLog> listOptimizations() {
        return logRepository.findAllByOrderByIdDesc();
    }

    @PostMapping("/products/{id}/optimize")
    public ResponseEntity<OptimizationLog> optimizeProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow();

        List<SearchTrend> categoryTrends = trendRepository.findAll().stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(product.getCategory()))
                .toList();

        AiContentService.GeneratedContent generated =
                aiContentService.generateOptimizedCopy(product, categoryTrends);

        OptimizationLog log = new OptimizationLog();
        log.setProduct(product);
        log.setOriginalTitle(product.getCurrentTitle());
        log.setOriginalDescription(product.getCurrentDescription());
        log.setSuggestedTitle(generated.title());
        log.setSuggestedDescription(generated.description());
        log.setAppliedKeywords(
                categoryTrends.stream().map(SearchTrend::getKeyword).toList().toString()
        );
        log.setDecision("PENDING_REVIEW");
        log.setCreatedAt(LocalDateTime.now());
        log.setUpdatedAt(LocalDateTime.now());

        OptimizationLog savedLog = logRepository.save(log);

        product.setStatus("OPTIMIZED");
        productRepository.save(product);

        return ResponseEntity.ok(savedLog);
    }

    @PostMapping("/optimizations/{logId}/approve")
    public ResponseEntity<OptimizationLog> approveOptimization(@PathVariable Long logId) {
        return ResponseEntity.ok(optimizationLoopService.approveAndSimulateLearning(logId));
    }

    @PostMapping("/optimizations/{logId}/reject")
    public ResponseEntity<Map<String, String>> rejectOptimization(@PathVariable Long logId) {
        OptimizationLog log = logRepository.findById(logId).orElseThrow();
        log.setDecision("REJECTED");
        log.setUpdatedAt(LocalDateTime.now());
        logRepository.save(log);

        Product product = log.getProduct();
        product.setStatus("PENDING");
        productRepository.save(product);

        return ResponseEntity.ok(Map.of("status", "REJECTED"));
    }
}
