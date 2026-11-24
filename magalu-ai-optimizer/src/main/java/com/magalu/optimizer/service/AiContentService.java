package com.magalu.optimizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magalu.optimizer.model.Product;
import com.magalu.optimizer.model.SearchTrend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiContentService {

    @Value("${ai.api.key:}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeneratedContent generateOptimizedCopy(Product product, List<SearchTrend> trends) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackRuleBasedGeneration(product, trends);
        }

        String keywords = trends.stream()
                .map(t -> t.getKeyword() + " (" + t.getSource() + ")")
                .collect(Collectors.joining(", "));

        String prompt = """
            Você é um especialista em SEO e conversão para o marketplace Magalu.
            Reescreva o título e a descrição do produto abaixo para aumentar CTR e vendas.

            REGRAS OBRIGATÓRIAS (COMPLIANCE):
            1. Use EXCLUSIVAMENTE dados presentes na Ficha Técnica. NUNCA invente voltagem, medidas ou recursos.
            2. O título deve ter no máximo 95 caracteres, claro, sem caixa alta excessiva e sem termos promocionais proibidos (ex: "promoção", "frete grátis", "melhor preço").
            3. Incorpore naturalmente as palavras-chave em alta quando fizerem sentido técnico.
            4. Responda EXATAMENTE no formato JSON: {"title": "...", "description": "..."}

            DADOS DO PRODUTO:
            - Título atual: %s
            - Descrição atual: %s
            - Ficha Técnica (Fonte da Verdade): %s
            - Palavras-chave emergentes: %s
            """.formatted(
                product.getCurrentTitle(),
                product.getCurrentDescription(),
                product.getTechnicalSpecs(),
                keywords
            );

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
            );

            String responseJson = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseJson);
            String rawText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            return parseAndSanitize(rawText, product, trends);
        } catch (Exception e) {
            return fallbackRuleBasedGeneration(product, trends);
        }
    }

    private GeneratedContent parseAndSanitize(String rawText, Product product, List<SearchTrend> trends) {
        try {
            String clean = rawText.replaceAll("```json|```", "").trim();
            JsonNode node = objectMapper.readTree(clean);
            String title = node.path("title").asText(product.getCurrentTitle());
            String desc = node.path("description").asText(product.getCurrentDescription());

            if (title.length() > 100) {
                title = title.substring(0, 97) + "...";
            }
            if (desc.length() > 4000) {
                desc = desc.substring(0, 3997) + "...";
            }
            return new GeneratedContent(title, desc);
        } catch (Exception e) {
            return fallbackRuleBasedGeneration(product, trends);
        }
    }

    private GeneratedContent fallbackRuleBasedGeneration(Product p, List<SearchTrend> trends) {
        String topKeyword = trends.isEmpty() ? "" : " - " + capitalize(trends.get(0).getKeyword());
        String newTitle = p.getCurrentTitle() + topKeyword;
        if (newTitle.length() > 95) {
            newTitle = newTitle.substring(0, 95);
        }

        String keywordSummary = trends.stream()
                .map(SearchTrend::getKeyword)
                .collect(Collectors.joining(", "));

        String newDesc = p.getCurrentDescription()
                + "\n\nDestaques de Busca e Uso: " + keywordSummary
                + "\n\nEspecificações Técnicas Oficiais:\n" + p.getTechnicalSpecs()
                + "\n\nConteúdo validado automaticamente com base na ficha técnica do fabricante.";

        return new GeneratedContent(newTitle, newDesc);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public record GeneratedContent(String title, String description) {}
}
