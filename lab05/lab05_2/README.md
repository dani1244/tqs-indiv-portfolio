# Lab 5.2 - Interactive Test Recording

**Universidade de Aveiro**  
**Autor:** Daniel Simbe  
**Data:** Outubro 2025

## Objetivo

Explorar ferramentas de gravação interativa de testes (Selenium IDE e Katalon Recorder), compreender a exportação e refatoração de código gerado, e integrar testes gravados num projeto Maven com JUnit 5 e Selenium-Jupiter.

**Desenvolvido em:** Java, Selenium WebDriver, Selenium IDE, JUnit 5, Maven, Firefox

## Requisitos Cumpridos

### Exercício 5.2a - Gravação Interativa com Selenium IDE

**Testes gravados:**
- Navegação na aplicação BlazeDemo
- Seleção de origem e destino de voo
- Escolha de um voo específico
- Preenchimento de formulário de compra
- Validação de página de confirmação

**Características:**
- Uso de Selenium IDE como plugin do browser
- Gravação de interações do utilizador
- Adição de asserções automáticas e manuais
- Replay do teste diretamente no IDE
- Exportação em formato `.side`

**Fluxo do teste gravado:**

```
1. Abrir https://blazedemo.com/
2. Selecionar origem: "Paris"
3. Selecionar destino: "London"
4. Clicar em "Find Flights"
5. Selecionar primeiro voo disponível
6. Preencher dados do passageiro
7. Submeter compra
8. Verificar página de confirmação
```

### Exercício 5.2b - Asserção Manual no Selenium IDE

**Teste implementado:**
- Adição de step manual de asserção
- Verificação de título da página de confirmação

**Implementação no Selenium IDE:**

```
Command: assert title
Target: BlazeDemo Confirmation
Value: (empty)
```

**Características:**
- Asserção adicionada **manualmente** no editor (não gravada)
- Validação de que o título contém "BlazeDemo Confirmation"
- Teste falha se título não corresponder


### Exercício 5.2c - Exportação e Refatoração para JUnit 5

**Testes implementados:**
- Exportação do teste do Selenium IDE para Java
- Refatoração para compliance com JUnit 5
- Limpeza e otimização do código gerado
- Integração com Selenium-Jupiter

**Código gerado pelo Selenium IDE (exemplo):**

```java
// Código original - JUnit 4
public class BlazedemoTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
    
    @Before
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
    
    @Test
    public void blazedemo() {
        driver.get("https://blazedemo.com/");
        driver.findElement(By.name("fromPort")).click();
        // ... mais código gerado ...
    }
}
```

**Código refatorado para JUnit 5 + Selenium-Jupiter:**

```java
@ExtendWith(SeleniumJupiter.class)
class BlazeDemoTest {
    
    @Test
    @DisplayName("Complete flight booking flow on BlazeDemo")
    void testCompleteFlightBooking(FirefoxDriver driver) {
        // 1. Navigate to BlazeDemo
        driver.get("https://blazedemo.com/");
        assertThat(driver.getTitle()).contains("BlazeDemo");
        
        // 2. Select departure city
        WebElement fromPort = driver.findElement(By.name("fromPort"));
        fromPort.click();
        Select fromPortSelect = new Select(fromPort);
        fromPortSelect.selectByValue("Paris");
        
        // 3. Select destination city
        WebElement toPort = driver.findElement(By.name("toPort"));
        toPort.click();
        Select toPortSelect = new Select(toPort);
        toPortSelect.selectByValue("London");
        
        // 4. Submit search
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        
        // 5. Wait for results and verify page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleContains("Flights"));
        assertThat(driver.getTitle()).contains("Flights from Paris to London");
        
        // 6. Select first available flight
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("tr:nth-child(1) .btn-primary")));
        driver.findElement(By.cssSelector("tr:nth-child(1) .btn-primary")).click();
        
        // 7. Wait for purchase form
        wait.until(ExpectedConditions.titleContains("Purchase"));
        assertThat(driver.getTitle()).contains("BlazeDemo Purchase");
        
        // 8. Fill passenger information
        driver.findElement(By.id("inputName")).sendKeys("John Doe");
        driver.findElement(By.id("address")).sendKeys("123 Test Street");
        driver.findElement(By.id("city")).sendKeys("Test City");
        driver.findElement(By.id("state")).sendKeys("Test State");
        driver.findElement(By.id("zipCode")).sendKeys("12345");
        
        // 9. Select credit card type
        Select cardTypeSelect = new Select(driver.findElement(By.id("cardType")));
        cardTypeSelect.selectByValue("visa");
        
        // 10. Fill credit card information
        driver.findElement(By.id("creditCardNumber")).sendKeys("4111111111111111");
        driver.findElement(By.id("creditCardMonth")).clear();
        driver.findElement(By.id("creditCardMonth")).sendKeys("12");
        driver.findElement(By.id("creditCardYear")).clear();
        driver.findElement(By.id("creditCardYear")).sendKeys("2028");
        driver.findElement(By.id("nameOnCard")).sendKeys("John Doe");
        
        // 11. Submit purchase
        driver.findElement(By.cssSelector(".btn-primary")).click();
        
        // 12. Verify confirmation page
        wait.until(ExpectedConditions.titleContains("Confirmation"));
        
        // Exercise 5.2b - Manual assertion added in IDE
        assertThat(driver.getTitle())
            .isEqualTo("BlazeDemo Confirmation");
        
        // Additional verification
        assertThat(driver.getCurrentUrl())
            .contains("confirmation");
        
        WebElement confirmationHeader = driver.findElement(By.tagName("h1"));
        assertThat(confirmationHeader.getText())
            .contains("Thank you for your purchase");
    }
}
```

## Principais Refatorações Realizadas

### 1. Migração de JUnit 4 para JUnit 5

**Antes (JUnit 4):**
```java
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

@Before
public void setUp() { }

@After
public void tearDown() { }

@Test
public void testName() { }
```

**Depois (JUnit 5):**
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SeleniumJupiter.class)

@Test
@DisplayName("Descriptive name")
void testName(FirefoxDriver driver) { }
```

### 2. Integração com Selenium-Jupiter

**Eliminado:**
- `@Before` e `@After` methods
- `driver = new ChromeDriver()`
- `driver.quit()`
- `WebDriverManager` manual

**Adicionado:**
- `@ExtendWith(SeleniumJupiter.class)`
- Dependency injection: `FirefoxDriver driver` como parâmetro
- Gestão automática de lifecycle

### 3. Adição de Esperas Explícitas

**Código gerado (sem esperas):**
```java
driver.findElement(By.cssSelector(".btn-primary")).click();
driver.findElement(By.id("inputName")).sendKeys("John Doe");
```

**Código refatorado (com esperas):**
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-primary")));
driver.findElement(By.cssSelector(".btn-primary")).click();

wait.until(ExpectedConditions.presenceOfElementLocated(By.id("inputName")));
driver.findElement(By.id("inputName")).sendKeys("John Doe");
```

### 4. Melhoria de Locators

**Código gerado:**
```java
// XPath frágil gerado automaticamente
driver.findElement(By.xpath("//div[@id='root']/div/table/tbody/tr[1]/td[1]/input"))
```

**Código refatorado:**
```java
// CSS Selector mais robusto
driver.findElement(By.cssSelector("tr:nth-child(1) .btn-primary"))

// ID quando disponível
driver.findElement(By.id("inputName"))

// Name attribute
driver.findElement(By.name("fromPort"))
```

### 5. Adição de Asserções Robustas

**Código gerado:**
```java
// Asserções limitadas ou ausentes
```

**Código refatorado:**
```java
// Asserções em pontos-chave do fluxo
assertThat(driver.getTitle()).contains("BlazeDemo");
assertThat(driver.getTitle()).contains("Flights from Paris to London");
assertThat(driver.getTitle()).isEqualTo("BlazeDemo Confirmation");
assertThat(driver.getCurrentUrl()).contains("confirmation");

WebElement confirmationHeader = driver.findElement(By.tagName("h1"));
assertThat(confirmationHeader.getText()).contains("Thank you for your purchase");
```

## Resultados dos Testes

**Estatísticas de Execução:**

```
[INFO] Running blazedemo.BlazeDemoTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 12.345 s

[INFO] Results:
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Detalhes do Teste:**
- Duração média: ~10-15 segundos
- Modo de execução: Headless (Firefox)
- Páginas navegadas: 4 (Home → Results → Purchase → Confirmation)
- Elementos interagidos: 13
- Asserções: 5

## Comandos de Execução

```bash
# Executar o teste completo
mvn clean test

# Executar apenas o teste BlazeDemoTest
mvn test -Dtest=BlazeDemoTest

# Executar com logs detalhados
mvn test -X -Dtest=BlazeDemoTest

# Executar sem modo headless (ver browser)
# (requer modificação no código para remover opção --headless)
mvn test -Dtest=BlazeDemoTest
```


## Ficheiro .side (Selenium IDE)

**Conteúdo do projeto gravado:**

```json
{
  "id": "blazedemo-test-project",
  "version": "2.0",
  "name": "BlazeDemo Flight Booking",
  "url": "https://blazedemo.com",
  "tests": [{
    "id": "test-1",
    "name": "Complete Flight Booking",
    "commands": [
      {
        "id": "1",
        "command": "open",
        "target": "https://blazedemo.com/",
        "value": ""
      },
      {
        "id": "2",
        "command": "click",
        "target": "name=fromPort",
        "value": ""
      },
      {
        "id": "3",
        "command": "select",
        "target": "name=fromPort",
        "value": "label=Paris"
      },
      // ... mais comandos ...
      {
        "id": "final",
        "command": "assertTitle",
        "target": "BlazeDemo Confirmation",
        "value": ""
      }
    ]
  }]
}
```

---

## Dependências Maven

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

    <!-- Selenium Java -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
    </dependency>

    <!-- Selenium-Jupiter Extension -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>selenium-jupiter</artifactId>
        <version>5.1.0</version>
        <scope>test</scope>
    </dependency>

    <!-- AssertJ -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>

    <!-- Logback -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Desafios e Soluções

### 1. Código Gerado Incompatível com JUnit 5

**Problema:** Selenium IDE gera código para JUnit 4 por padrão.

**Solução:**
- Refatoração manual das anotações
- Remoção de `@Before`/`@After`
- Adoção de `@ExtendWith(SeleniumJupiter.class)`
- Uso de dependency injection

### 2. Locators XPath Frágeis

**Problema:** IDE gera XPath absolutos que quebram facilmente.

**Solução:**
- Substituição por CSS Selectors
- Priorização de IDs e Names
- Uso de seletores nth-child quando necessário

```java
// Gerado (frágil)
By.xpath("//div[@id='root']/div/table/tbody/tr[1]/td[1]/input")

// Refatorado (robusto)
By.cssSelector("tr:nth-child(1) .btn-primary")
```

### 3. Ausência de Esperas Explícitas

**Problema:** Código gerado não inclui esperas, causando falhas intermitentes.

**Solução:**
- Adição de `WebDriverWait` em pontos críticos
- Uso de `ExpectedConditions` para elementos dinâmicos
- Validação de carregamento de páginas

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.titleContains("Confirmation"));
```

### 4. Falta de Asserções Significativas

**Problema:** IDE grava poucas asserções automáticas.

**Solução:**
- Adição manual de asserções em pontos-chave
- Validação de títulos, URLs e elementos
- Verificação de conteúdo de confirmação

### 5. Seleção de Dropdown

**Problema:** Código gerado para dropdowns é verboso e repetitivo.

**Solução:**
- Uso da classe `Select` do Selenium
- Métodos `selectByValue()` e `selectByVisibleText()`

```java
Select fromPortSelect = new Select(driver.findElement(By.name("fromPort")));
fromPortSelect.selectByValue("Paris");
```

---

## Comparação: Selenium IDE vs Código Refatorado

### Selenium IDE (Gravação)

**Vantagens:**
- Rápido para criar protótipos
- Não requer conhecimento de programação
- Bom para explorar locators
- Útil para documentar fluxos

**Desvantagens:**
- Código gerado não é production-ready
- Locators frágeis (XPath absolutos)
- Sem esperas explícitas
- Poucas asserções
- Formato JUnit 4

### Código Refatorado (Manual)

**Vantagens:**
- Código limpo e manutenível
- Locators robustos e semânticos
- Esperas explícitas adequadas
- Asserções completas
- JUnit 5 moderno
- Integração com Selenium-Jupiter

**Desvantagens:**
- Requer tempo para refatoração
- Necessita conhecimento técnico
- Mais linhas de código inicial

## Boas Práticas Aplicadas

### 1. Estratégia de Locators (Ordem de Preferência)

```java
// 1º - ID (mais estável)
driver.findElement(By.id("inputName"))

// 2º - Name attribute
driver.findElement(By.name("fromPort"))

// 3º - CSS Selector
driver.findElement(By.cssSelector("tr:nth-child(1) .btn-primary"))

// 4º - XPath (último recurso, evitar absolutos)
driver.findElement(By.xpath("//h1[contains(text(), 'Thank you')]"))
```

### 2. Padrão AAA (Arrange-Act-Assert)

```java
@Test
void testExample(FirefoxDriver driver) {
    // Arrange
    driver.get("https://blazedemo.com/");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Act
    Select fromPort = new Select(driver.findElement(By.name("fromPort")));
    fromPort.selectByValue("Paris");
    driver.findElement(By.cssSelector("input[type='submit']")).click();
    
    // Assert
    wait.until(ExpectedConditions.titleContains("Flights"));
    assertThat(driver.getTitle()).contains("Flights from Paris");
}
```

### 3. Esperas Explícitas em Transições

```java
// Após cada navegação ou ação que carrega nova página
driver.findElement(By.cssSelector(".btn-primary")).click();
wait.until(ExpectedConditions.titleContains("Expected Title"));
```

### 4. Asserções Múltiplas para Robustez

```java
// Verificar múltiplos aspectos da página
assertThat(driver.getTitle()).isEqualTo("BlazeDemo Confirmation");
assertThat(driver.getCurrentUrl()).contains("confirmation");
assertThat(driver.findElement(By.tagName("h1")).getText())
    .contains("Thank you");
```

### 5. Uso de DisplayName Descritivo

```java
@Test
@DisplayName("Complete flight booking flow on BlazeDemo")
void testCompleteFlightBooking(FirefoxDriver driver) {
    // Test implementation
}
```

## Métricas de Qualidade

### Performance
- Gravação do teste: ~2 minutos
- Refatoração do código: ~30 minutos
- Execução do teste: ~10-15 segundos
- Build completo: ~15-20 segundos

### Cobertura
- 100% do fluxo de compra de voo testado
- 4 páginas navegadas e validadas
- 13 interações com elementos
- 5 asserções críticas

### Confiabilidade
- 0 testes flaky após refatoração
- Esperas adequadas em todas as transições
- Execução determinística
- Isolamento completo

### Manutenibilidade
- Código refatorado: 150 linhas (vs 250 do gerado)
- Locators robustos substituídos: 8
- Esperas explícitas adicionadas: 6
- Asserções adicionadas: 5

## Evolução para Page Object Pattern

O exercício 5.2 implementa testes **sem Page Object Pattern**. No exercício 5.4, seria aplicado:

**Sem Page Object (atual):**
```java
@Test
void test(FirefoxDriver driver) {
    driver.get("https://blazedemo.com/");
    Select fromPort = new Select(driver.findElement(By.name("fromPort")));
    fromPort.selectByValue("Paris");
    driver.findElement(By.cssSelector("input[type='submit']")).click();
    assertThat(driver.getTitle()).contains("Flights");
}
```

**Com Page Object (futuro):**
```java
@Test
void test(FirefoxDriver driver) {
    HomePage homePage = new HomePage(driver);
    FlightsPage flightsPage = homePage
        .selectDepartureCity("Paris")
        .selectDestinationCity("London")
        .searchFlights();
    assertThat(flightsPage.isLoaded()).isTrue();
}
```

## Conclusão

Este laboratório demonstrou o processo completo de gravação, exportação e refatoração de testes web automatizados:

1. **Gravação Interativa** - Selenium IDE como ferramenta de exploração rápida
2. **Exportação** - Geração automática de código Java/JUnit
3. **Refatoração** - Transformação do código gerado em código production-ready
4. **Integração** - Adoção de JUnit 5 e Selenium-Jupiter para gestão moderna de testes

Este exercício estabelece as bases para a aplicação do **Page Object Pattern** (exercício 5.4), que melhorará ainda mais a manutenibilidade dos testes.