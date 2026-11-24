# Magalu AI Catalog Optimizer (Protótipo Funcional)

Protótipo em Java 21 + Spring Boot 3 demonstrando os 4 pilares da solução:
1. **Identificação de Palavras-Chave** (Google Trends + Busca Magalu)
2. **Análise de Dados & Automação** (Quality Score e diagnóstico automático)
3. **IA Generativa para Conteúdo** (Geração ancorada na ficha técnica + guardrails de compliance)
4. **Monitoramento e Otimização Contínua** (Comparador Antes x Depois e simulação de ganho CTR/Conversão)

## Como executar

1. Certifique-se de ter **Java 21+** e **Maven** instalados.
2. (Opcional) Para usar geração real via Gemini API, configure a variável de ambiente:
   ```bash
   export AI_API_KEY="sua_chave_gemini"
   ```
   *Se não definir a chave, o sistema utiliza automaticamente o gerador local estruturado (fallback) para testes offline.*
3. Na raiz do projeto, execute:
   ```bash
   mvn spring-boot:run
   ```
4. Acesse no navegador:
   - **Painel Principal:** `http://localhost:8080`
   - **Console H2 Database:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/magaludb`, user: `sa`, senha vazia)
