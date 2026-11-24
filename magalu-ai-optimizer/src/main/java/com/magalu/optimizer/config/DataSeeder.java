package com.magalu.optimizer.config;

import com.magalu.optimizer.model.Product;
import com.magalu.optimizer.model.SearchTrend;
import com.magalu.optimizer.repository.ProductRepository;
import com.magalu.optimizer.repository.SearchTrendRepository;
import com.magalu.optimizer.service.DiagnosticService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final SearchTrendRepository trendRepository;
    private final DiagnosticService diagnosticService;

    @Override
    public void run(String... args) {
        if (trendRepository.count() == 0) {
            List<SearchTrend> trends = List.of(
                    trend("air fryer oven 12l", "Eletroportáteis", "Google Trends", "+84%"),
                    trend("sem óleo antiaderente", "Eletroportáteis", "Busca Magalu", "+62%"),
                    trend("notebook leve ssd 512gb", "Informática", "Google Trends", "+51%"),
                    trend("bateria longa duração", "Informática", "Busca Magalu", "+47%"),
                    trend("cafeteira expresso cápsula", "Eletroportáteis", "Google Trends", "+39%"),
                    trend("19 bar pressão", "Eletroportáteis", "Busca Magalu", "+33%")
            );
            trendRepository.saveAll(trends);
        }

        if (productRepository.count() == 0) {
            List<SearchTrend> allTrends = trendRepository.findAll();

            List<Product> products = List.of(
                    buildProduct(
                            "MGL-AF-102",
                            "Eletroportáteis",
                            "Fritadeira Elétrica",
                            "Fritadeira elétrica preta para cozinha.",
                            "Capacidade: 12L; Potência: 1800W; Tensão: 127V; Cesto: antiaderente removível; Timer: até 90 min; Função: assar, fritar sem óleo e reaquecer.",
                            1.4,
                            0.8
                    ),
                    buildProduct(
                            "MGL-NB-309",
                            "Informática",
                            "Notebook 15 polegadas",
                            "Notebook para trabalho e estudos do dia a dia.",
                            "Processador: Intel Core i5 12ª geração; RAM: 8GB; Armazenamento: SSD 512GB NVMe; Tela: 15.6 Full HD antirreflexo; Peso: 1.65 kg; Bateria: até 8h.",
                            1.9,
                            1.0
                    ),
                    buildProduct(
                            "MGL-CF-215",
                            "Eletroportáteis",
                            "Cafeteira Cápsula",
                            "Faz café rápido de manhã.",
                            "Pressão: 19 bar; Reservatório de água: 800ml removível; Compatibilidade: cápsulas padrão Nespresso; Potência: 1450W; Desligamento automático.",
                            1.2,
                            0.7
                    ),
                    buildProduct(
                            "MGL-AF-188",
                            "Eletroportáteis",
                            "Fritadeira Air Fryer Oven 12L Digital Antiaderente",
                            "Fritadeira elétrica com painel digital, cesto antiaderente e preparo sem óleo para receitas variadas.",
                            "Capacidade: 12L; Potência: 1800W; Tensão: 220V; Painel: touch digital com 8 receitas pré-programadas; Acessórios: 2 grelhas e bandeja coletora.",
                            3.6,
                            2.4
                    )
            );

            for (Product p : products) {
                List<SearchTrend> catTrends = allTrends.stream()
                        .filter(t -> t.getCategory().equalsIgnoreCase(p.getCategory()))
                        .toList();
                diagnosticService.evaluateProduct(p, catTrends);
            }

            productRepository.saveAll(products);
        }
    }

    private SearchTrend trend(String keyword, String category, String source, String growth) {
        SearchTrend t = new SearchTrend();
        t.setKeyword(keyword);
        t.setCategory(category);
        t.setSource(source);
        t.setGrowth(growth);
        return t;
    }

    private Product buildProduct(
            String sku,
            String category,
            String title,
            String description,
            String specs,
            double ctr,
            double conversionRate
    ) {
        Product p = new Product();
        p.setSku(sku);
        p.setCategory(category);
        p.setCurrentTitle(title);
        p.setCurrentDescription(description);
        p.setTechnicalSpecs(specs);
        p.setCtr(ctr);
        p.setConversionRate(conversionRate);
        p.setStatus("PENDING");
        return p;
    }
}
