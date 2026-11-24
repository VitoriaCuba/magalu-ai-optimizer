package com.magalu.optimizer.service;

import com.magalu.optimizer.model.Product;
import com.magalu.optimizer.model.SearchTrend;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticService {

    public void evaluateProduct(Product product, List<SearchTrend> categoryTrends) {
        int score = 100;
        List<String> issues = new ArrayList<>();

        int titleLen = product.getCurrentTitle() != null ? product.getCurrentTitle().length() : 0;
        if (titleLen < 45) {
            score -= 25;
            issues.add("Título muito curto e genérico (" + titleLen + " chars)");
        }

        int descLen = product.getCurrentDescription() != null ? product.getCurrentDescription().length() : 0;
        if (descLen < 180) {
            score -= 35;
            issues.add("Descrição fraca/incompleta (" + descLen + " chars)");
        }

        String fullText = (product.getCurrentTitle() + " " + product.getCurrentDescription()).toLowerCase();
        boolean hasTrend = categoryTrends.stream()
                .anyMatch(t -> fullText.contains(t.getKeyword().toLowerCase()));

        if (!hasTrend && !categoryTrends.isEmpty()) {
            score -= 25;
            issues.add("Ausência de termos em alta da categoria");
        }

        if (product.getConversionRate() < 1.2) {
            score -= 15;
            issues.add("Taxa de conversão abaixo da média (" + product.getConversionRate() + "%)");
        }

        product.setQualityScore(Math.max(score, 10));
        product.setDiagnosticNotes(issues.isEmpty() ? "Anúncio saudável" : String.join(" | ", issues));
    }
}
