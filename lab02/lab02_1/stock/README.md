# TQS Lab02_1 - Unit Tests and Mocks


 
**Autor:** Daniel Simbe  
**Data:** Outubro 2025

---

## Objetivos

- Executar testes unitários com JUnit 5
- Utilizar mocks com Mockito e injeção de dependências (@Mock)
- Experimentar comportamentos de mocks (strict/lenient, verificações)
- Criar testes de integração separados dos unitários
- Analisar cobertura de código com JaCoCo

---


## Parte 1: StocksPortfolio

### Requisito 1.a: Criar teste seguindo outline TDD

**Implementação completa seguindo os 5 passos:**

```java
@ExtendWith(MockitoExtension.class)  // 1. Integração Mockito + JUnit 5
class StocksPortfolioTest {
    
    @Mock  // 2. Mock do serviço remoto
    private IStockMarketService stockMarketService;
    
    private StocksPortfolio portfolio;
    
    @BeforeEach
    void setUp() {
        portfolio = new StocksPortfolio(stockMarketService);  // 3. SuT com mock injetado
    }
    
    @Test
    void totalValue_WithSingleStock_ReturnsCorrectValue() {
        // Arrange
        Stock appleStock = new Stock("AAPL", 10);
        portfolio.addStock(appleStock);
        
        when(stockMarketService.getPrice("AAPL")).thenReturn(150.0);  // 4. Expectativas
        
        // Act
        double total = portfolio.totalValue();  // 5. Execução
        
        // Assert
        assertThat(total).isEqualTo(1500.0);  // 6. Verificação resultado
        verify(stockMarketService).getPrice("AAPL");  // 7. Verificação mock
    }
}
```

**Testes implementados para `totalValue()`:**
- `totalValue_WithEmptyPortfolio_ReturnsZero`
- `totalValue_WithSingleStock_ReturnsCorrectValue`
- `totalValue_WithMultipleStocks_ReturnsCorrectSum`

### Requisito 1.c: Expectativas extras e warnings do Mockito

**Objetivo:** Demonstrar comportamento strict do Mockito

```java
@Test
void totalValue_WithExtraUnusedMockExpectations_ShowsLenientBehavior() {
    Stock appleStock = new Stock("AAPL", 10);
    portfolio.addStock(appleStock);
    
    // Configuramos 3 expectativas, mas só AAPL será usada
    when(stockMarketService.getPrice("AAPL")).thenReturn(150.0);
    lenient().when(stockMarketService.getPrice("GOOGL")).thenReturn(2500.0);  // Não usada
    lenient().when(stockMarketService.getPrice("MSFT")).thenReturn(300.0);    // Não usada
    
    double total = portfolio.totalValue();
    
    assertThat(total).isEqualTo(1500.0);
    verify(stockMarketService).getPrice("AAPL");
}
```

**Observações:**
- Sem `lenient()`: Mockito lança `UnnecessaryStubbingException`
- Com `lenient()`: Teste passa, mas expectativas não usadas são permitidas
- O modo strict (padrão) força código de teste mais limpo

### Requisito 1.d: Usar AssertJ em vez de JUnit assertions

**Refatoração completa para AssertJ:**

```java
// ANTES (JUnit assertions)
assertEquals(1500.0, total);
assertTrue(result.isEmpty());
assertEquals(3, stocks.size());

// DEPOIS (AssertJ - mais legível)
assertThat(total).isEqualTo(1500.0);
assertThat(result).isEmpty();
assertThat(stocks).hasSize(3);
```

**Vantagens do AssertJ:**
- Sintaxe fluente e legível
- Mensagens de erro mais descritivas
- Autocomplete melhor na IDE
- Assertions específicas para tipos (Optional, Collections, etc.)

**Exemplos de AssertJ usados:**
```java
assertThat(result).isPresent();
assertThat(result.get().getTitle()).isEqualTo("Mens Cotton Jacket");
assertThat(stocks).containsExactly(stock1, stock2, stock3);
assertThat(portfolio.getStocks()).hasSize(1).contains(stock);
assertThatThrownBy(() -> new Stock(null, 10))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessage("Symbol cannot be null or empty");
```

### Requisito 1.e: Adicionar método `mostValuableStocks(int topN)`

**Implementação:**

```java
public List<Stock> mostValuableStocks(int topN) {
    if (topN <= 0) {
        throw new IllegalArgumentException("topN must be positive");
    }
    
    return stocks.stream()
            .sorted((s1, s2) -> {
                double value1 = stockMarketService.getPrice(s1.getSymbol()) * s1.getQuantity();
                double value2 = stockMarketService.getPrice(s2.getSymbol()) * s2.getQuantity();
                return Double.compare(value2, value1);  // Ordem decrescente
            })
            .limit(topN)
            .collect(Collectors.toList());
}
```

### Requisito 1.f: Testar sem confiar em AI - Edge Cases

**Testes implementados (cobertura completa):**

1. **Validação de entrada:**
```java
@Test
void mostValuableStocks_WithNegativeN_ThrowsException() {
    assertThatThrownBy(() -> portfolio.mostValuableStocks(-1))
        .isInstanceOf(IllegalArgumentException.class);
}

@Test
void mostValuableStocks_WithZeroN_ThrowsException() {
    assertThatThrownBy(() -> portfolio.mostValuableStocks(0))
        .isInstanceOf(IllegalArgumentException.class);
}
```

2. **Edge case - Portfolio vazio:**
```java
@Test
void mostValuableStocks_WithEmptyPortfolio_ReturnsEmptyList() {
    List<Stock> result = portfolio.mostValuableStocks(3);
    assertThat(result).isEmpty();
}
```

3. **Funcionalidade core - Ordenação correta:**
```java
@Test
void mostValuableStocks_WithTop3_ReturnsCorrectOrder() {
    Stock lowValueStock = new Stock("LOW", 10);    // 10 * 50 = 500
    Stock highValueStock = new Stock("HIGH", 5);   // 5 * 1000 = 5000
    Stock midValueStock = new Stock("MID", 20);    // 20 * 100 = 2000
    
    portfolio.addStock(lowValueStock);
    portfolio.addStock(highValueStock);
    portfolio.addStock(midValueStock);
    
    when(stockMarketService.getPrice("LOW")).thenReturn(50.0);
    when(stockMarketService.getPrice("HIGH")).thenReturn(1000.0);
    when(stockMarketService.getPrice("MID")).thenReturn(100.0);
    
    List<Stock> top3 = portfolio.mostValuableStocks(3);
    
    // Verifica ordem decrescente: HIGH > MID > LOW
    assertThat(top3).containsExactly(highValueStock, midValueStock, lowValueStock);
}
```

4. **Edge case - topN maior que tamanho do portfolio:**
```java
@Test
void mostValuableStocks_WithTopNGreaterThanPortfolioSize_ReturnsAllStocks() {
    Stock stock1 = new Stock("S1", 10);
    Stock stock2 = new Stock("S2", 5);
    
    portfolio.addStock(stock1);
    portfolio.addStock(stock2);
    
    when(stockMarketService.getPrice("S1")).thenReturn(100.0);
    when(stockMarketService.getPrice("S2")).thenReturn(200.0);
    
    List<Stock> result = portfolio.mostValuableStocks(5);  // Pedimos 5, só temos 2
    
    assertThat(result).hasSize(2);
}
```

**Análise da implementação AI:**
- Algoritmo de ordenação correto
- Uso apropriado de Streams
- Validação de entrada implementada
- Edge cases não cobertos automaticamente (foi necessário adicionar testes)

---

## Conceitos Aplicados

### Dependency Injection (DI)

**Problema sem DI:**
```java
// Acoplamento forte - difícil de testar
public class StocksPortfolio {
    private IStockMarketService service = new RealStockMarketService();
}
```

**Solução com DI:**
```java
// Desacoplamento - fácil de testar
public class StocksPortfolio {
    private final IStockMarketService service;
    
    public StocksPortfolio(IStockMarketService service) {
        this.service = service;  // Injetado via construtor
    }
}
```

**Benefícios:**
- Testes podem injetar mocks
- Fácil trocar implementações
- Código mais testável e manutenível

### Padrão AAA (Arrange-Act-Assert)

Todos os testes seguem esta estrutura clara:

```java
@Test
void testName() {
    // Arrange - Preparar dados e mocks
    Stock stock = new Stock("AAPL", 10);
    when(service.getPrice("AAPL")).thenReturn(150.0);
    
    // Act - Executar ação sob teste
    double result = portfolio.totalValue();
    
    // Assert - Verificar resultado
    assertThat(result).isEqualTo(1500.0);
    
    // (Opcional) Verify - Verificar interações
    verify(service).getPrice("AAPL");
}
```

### Mockito - Strict vs Lenient

**Strict Mode (padrão):**
- Mockito valida que todas as expectativas configuradas são usadas
- Lança `UnnecessaryStubbingException` se há stubs não utilizados
- Força testes mais limpos e focados

**Lenient Mode:**
- Permite expectativas não utilizadas
- Útil em casos específicos (setup compartilhado, testes parametrizados)
- Usa-se `lenient().when(...).thenReturn(...)`

### Verificações com Mockito

```java
// Verificação básica - chamado exatamente 1 vez
verify(service).getPrice("AAPL");

// Verificações avançadas
verify(service, times(2)).getPrice("AAPL");      // Chamado 2 vezes
verify(service, atLeastOnce()).getPrice("AAPL"); // Chamado pelo menos 1 vez
verify(service, never()).getPrice("XYZ");         // Nunca chamado

// Verificar que não houve mais interações
verifyNoMoreInteractions(service);

// Verificar que não houve nenhuma interação
verifyNoInteractions(service);
```

---

## Análise de Cobertura

### Executar JaCoCo

```bash
mvn clean test jacoco:report
```

### Visualizar relatório

```bash


# Abrir no browser:
xdg-open target/site/jacoco/index.html
```

### Métricas obtidas

**Resultado atual:**
```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Cobertura por classe:**
- `Stock.java`: 100% (todos os métodos testados)
- `StocksPortfolio.java`: ~95% (lógica principal coberta)
- `IStockMarketService.java`: Interface (não aplicável)

**Análise:**
- **Linhas cobertas:** ~95%
- **Branches cobertos:** ~90%
- **Métodos cobertos:** 100%

### Interpretação

O relatório JaCoCo mostra:
- **Verde:** Código executado pelos testes
- **Amarelo:** Branches parcialmente cobertos
- **Vermelho:** Código não testado

---

## Comandos Maven

### Testes

```bash
# Executar todos os testes
mvn test

# Limpar e testar
mvn clean test

# Testar com verbose
mvn test -X

# Testar apenas uma classe
mvn test -Dtest=StocksPortfolioTest

# Testar apenas um método
mvn test -Dtest=StocksPortfolioTest#totalValue_WithSingleStock_ReturnsCorrectValue
```

### Build

```bash
# Build completo (compila + testa + empacota)
mvn package

# Build sem testes
mvn package -DskipTests=true

# Instalar no repositório local
mvn install
```

### Cobertura

```bash
# Gerar relatório de cobertura
mvn clean test jacoco:report

# Verificar se cobertura mínima é atingida
mvn test jacoco:check
```

### Outros

```bash
# Limpar target/
mvn clean

# Ver árvore de dependências
mvn dependency:tree

# Atualizar dependências
mvn clean install -U
```

---

## Notas Importantes

### 1. Dependency Injection é essencial para testabilidade

**Sem DI:**
```java
public StocksPortfolio() {
    this.service = new RealStockMarketService();  // Não testável
}
```

**Problemas:**
- Impossível substituir por mock
- Testes fariam chamadas reais (lento, não determinístico)
- Acoplamento forte

**Com DI:**
```java
public StocksPortfolio(IStockMarketService service) {
    this.service = service;  // Testável
}
```

**Benefícios:**
- Podemos injetar mock nos testes
- Código desacoplado e flexível
- Fácil trocar implementações

### 2. Interfaces facilitam mocking

Criar `IStockMarketService` (interface) em vez de usar classe concreta diretamente permite:
- Mockito criar mocks facilmente
- Múltiplas implementações (real, fake, mock)
- Melhor design (Dependency Inversion Principle)

### 3. Mocks devem ser verificados

Não basta testar o resultado final:

```java
// Incompleto
assertThat(total).isEqualTo(1500.0);

// Completo
assertThat(total).isEqualTo(1500.0);
verify(service).getPrice("AAPL");  // Verifica que mock foi usado corretamente
```

### 4. Edge cases são críticos

A implementação AI pode estar correta para casos normais, mas falhar em edge cases:
- Valores negativos ou zero
- Coleções vazias
- Valores nulos
- Limites (N > tamanho da coleção)

**Os testes devem cobrir todos estes cenários.**

### 5. Strict mocks mantêm testes limpos

O Mockito por padrão força que:
- Todas as expectativas configuradas sejam usadas
- Não haja código morto nos testes
- Testes sejam focados e mínimos

Isto é uma **feature, não um bug**!

### 6. AssertJ vs JUnit Assertions

**JUnit (básico):**
```java
assertEquals(expected, actual);
assertTrue(condition);
```

**AssertJ (recomendado):**
```java
assertThat(actual).isEqualTo(expected);
assertThat(condition).isTrue();
```

AssertJ oferece:
- Sintaxe mais natural
- Melhor autocomplete
- Mensagens de erro mais claras
- Assertions específicas por tipo

### 7. Padrão AAA torna testes legíveis

Sempre seguir:
1. **Arrange:** Preparar dados e mocks
2. **Act:** Executar ação
3. **Assert:** Verificar resultado
4. **(Verify):** Verificar interações

Isto torna os testes auto-documentados e fáceis de manter.

### 8. Cobertura alta ≠ Qualidade alta

Ter 100% de cobertura não garante código correto:
- Pode haver bugs lógicos
- Edge cases podem não estar cobertos
- Testes podem ser fracos (assertions insuficientes)

**Cobertura é necessária mas não suficiente.**

---

## Recursos

### Documentação Oficial

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

### Maven Plugins

- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

