# TQS Lab02 - 2.2: Conversion Method Behavior 


**Autor:** Daniel Simbe  
**Data:** Outubro 2025

---


## Contexto

### Cenário de Negócio

Uma aplicação precisa consumir informações de um catálogo de produtos fornecido por um parceiro B2B. A API de produtos está documentada em [https://fakestoreapi.com/docs](https://fakestoreapi.com/docs).

### Desafios

- A equipa **ainda não decidiu** qual biblioteca HTTP usar
- Cada chamada à API **tem custo** - minimizar custos em desenvolvimento
- Queremos testar o **parsing de JSON → POJO** sem fazer chamadas reais

### Objetivo

Implementar `ProductFinderService#findProductDetails(id)` seguindo **TDD** e usando **mocks** para isolar a lógica de parsing.

---



## Requisito 2.a: Criar Skeleton (TDD)

### Abordagem TDD

1. **Criar classes vazias** que compilam
2. **Escrever testes** antes da implementação
3. **Implementar** apenas o necessário para testes passarem

### 1. ISimpleHttpClient.java (Interface)

```java
package product;

public interface ISimpleHttpClient {
    /**
     * Executa uma requisição HTTP GET
     * @param url URL para fazer a requisição
     * @return conteúdo da resposta como String (JSON)
     */
    String doHttpGet(String url);
}
```

**Razão para usar interface:**
- Permite múltiplas implementações (mock, real, fake)
- Facilita injeção de dependência
- Mockito consegue criar mocks facilmente

### 2. Product.java (POJO)

```java
package product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("price")
    private double price;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("image")
    private String image;
    
    // Construtor vazio (necessário para Jackson)
    public Product() {}
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, title='%s', price=%.2f}", id, title, price);
    }
}
```

**Anotações Jackson:**
- `@JsonIgnoreProperties(ignoreUnknown = true)` - ignora campos extras do JSON
- `@JsonProperty("campo")` - mapeia campo JSON para atributo Java

### 3. ProductFinderService.java (Skeleton inicial)

```java
package product;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

public class ProductFinderService {
    
    private static final String API_BASE_URL = "https://fakestoreapi.com/products/";
    
    private final ISimpleHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ProductFinderService(ISimpleHttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient cannot be null");
        }
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }
    
    public Optional<Product> findProductDetails(int id) {
        // TODO: Implementar após escrever testes
        return Optional.empty();  // Placeholder
    }
}
```

**Dependency Injection:**
- HTTP client é injetado via construtor
- Permite substituir por mock nos testes
- Segue princípio da inversão de dependência

---

## Requisito 2.b: Como usar Mocking


**Mockito seria usado para mockar o `ISimpleHttpClient` pelos seguintes motivos:**

#### 1. Equipa ainda não decidiu qual HTTP Client usar
- Não temos implementação real disponível
- Não queremos comprometer-nos com uma biblioteca específica
- Mocks permitem desenvolver e testar sem decisão final

#### 2. API calls são pagas - minimizar custos
- Cada chamada à API `https://fakestoreapi.com` tem custo
- Durante desenvolvimento, testes executam centenas de vezes
- Mocks eliminam custos durante desenvolvimento e CI/CD

#### 3. Foco no parsing JSON → POJO
- **Objetivo principal:** testar se conseguimos converter JSON em objetos `Product`
- **Não queremos testar:** conectividade de rede, disponibilidade da API, latência
- Mocks permitem focar **apenas** na lógica de parsing

#### Como Mocking Resolve o Problema

```java
@Mock
private ISimpleHttpClient httpClient;  // Mock substitui implementação real

@Test
void testParsing() {
    // Mock retorna JSON diretamente (sem HTTP real)
    when(httpClient.doHttpGet("https://fakestoreapi.com/products/3"))
            .thenReturn("{\"id\":3,\"title\":\"Mens Cotton Jacket\",...}");
    
    // Testamos apenas o parsing
    Optional<Product> result = service.findProductDetails(3);
    
    // Verificamos que o JSON foi parseado corretamente
    assertThat(result.get().getTitle()).isEqualTo("Mens Cotton Jacket");
}
```

#### Benefícios

 Aspecto                Sem Mock (HTTP Real)  Com Mock 

 **Velocidade**          ~500ms por teste     ~5ms por teste 
 **Custo**                $$$ (chamadas API)   Gratuito 
 **Determinismo**         Pode falhar (rede)   Sempre consistente 
 **Isolamento**           Testa HTTP + parsing  Testa apenas parsing 
 **Disponibilidade**      Requer internet       Funciona offline 

---

## Requisito 2.c: Implementar Testes

### Testes Obrigatórios do Guião

#### c.i) findProductDetails(3) retorna produto válido

```java
@Test
void findProductDetails_WithId3_ReturnsValidProduct() {
    // Arrange - Mock retorna JSON real da API
    String productJson = """
        {
            "id": 3,
            "title": "Mens Cotton Jacket",
            "price": 55.99,
            "description": "great outerwear jackets",
            "category": "men's clothing",
            "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg"
        }
        """;
    
    when(httpClient.doHttpGet("https://fakestoreapi.com/products/3"))
            .thenReturn(productJson);
    
    // Act
    Optional<Product> result = service.findProductDetails(3);
    
    // Assert - Verifica parsing correto
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(3);
    assertThat(result.get().getTitle()).isEqualTo("Mens Cotton Jacket");
    assertThat(result.get().getPrice()).isEqualTo(55.99);
    assertThat(result.get().getCategory()).isEqualTo("men's clothing");
    
    // Verify - HTTP client foi usado corretamente
    verify(httpClient).doHttpGet("https://fakestoreapi.com/products/3");
    verifyNoMoreInteractions(httpClient);
}
```

#### c.ii) findProductDetails(300) retorna empty

```java
@Test
void findProductDetails_WithId300_ReturnsEmpty() {
    // Arrange - Produto 300 não existe
    when(httpClient.doHttpGet("https://fakestoreapi.com/products/300"))
            .thenReturn("");  // API retorna vazio
    
    // Act
    Optional<Product> result = service.findProductDetails(300);
    
    // Assert
    assertThat(result).isEmpty();
    
    // Verify
    verify(httpClient).doHttpGet("https://fakestoreapi.com/products/300");
}
```

### Testes Adicionais (Edge Cases)

#### IDs inválidos não fazem HTTP call

```java
@Test
void findProductDetails_WithInvalidId_ReturnsEmpty() {
    // IDs ≤ 0 são inválidos
    assertThat(service.findProductDetails(0)).isEmpty();
    assertThat(service.findProductDetails(-1)).isEmpty();
    
    // Verificar que nenhuma chamada HTTP foi feita
    verifyNoInteractions(httpClient);
}
```

#### Erro de rede retorna empty

```java
@Test
void findProductDetails_WhenHttpThrowsException_ReturnsEmpty() {
    // Simular erro de rede
    when(httpClient.doHttpGet(anyString()))
            .thenThrow(new RuntimeException("Network error"));
    
    Optional<Product> result = service.findProductDetails(1);
    
    assertThat(result).isEmpty();
}
```

#### JSON malformado retorna empty

```java
@Test
void findProductDetails_WithMalformedJson_ReturnsEmpty() {
    when(httpClient.doHttpGet(anyString()))
            .thenReturn("{invalid json");
    
    Optional<Product> result = service.findProductDetails(5);
    
    assertThat(result).isEmpty();
}
```

---

## Requisito 2.d: Implementar Código

### Implementação Final do findProductDetails()

```java
public Optional<Product> findProductDetails(int id) {
    // 1. Validar ID
    if (id <= 0) {
        return Optional.empty();
    }
    
    try {
        // 2. Construir URL
        String url = API_BASE_URL + id;
        
        // 3. Fazer requisição HTTP GET
        String jsonResponse = httpClient.doHttpGet(url);
        
        // 4. Validar resposta
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // 5. Parse JSON → POJO usando Jackson
        Product product = objectMapper.readValue(jsonResponse, Product.class);
        
        return Optional.of(product);
        
    } catch (Exception e) {
        // 6. Qualquer erro retorna empty
        return Optional.empty();
    }
}
```

### Lógica Implementada

1. **Validação de entrada:** IDs ≤ 0 retornam empty imediatamente
2. **Construção de URL:** Concatena base URL com ID
3. **Chamada HTTP:** Delega ao httpClient (mockado em testes)
4. **Validação de resposta:** Respostas vazias/null retornam empty
5. **Parsing JSON:** Usa Jackson ObjectMapper para deserializar
6. **Tratamento de erros:** Try-catch genérico para qualquer exceção

---

## Ciclo TDD Completo

### Fase RED  (Testes Falham)

**Execução inicial:**
```bash
mvn test
```

**Resultado esperado:**
```
[ERROR] Tests run: 7, Failures: 2, Errors: 3, Skipped: 0

[ERROR] ProductFinderServiceTest.findProductDetails_WithId3_ReturnsValidProduct:64
Expecting Optional to contain a value but it was empty.

[ERROR] ProductFinderServiceTest.findProductDetails_WithId300_ReturnsEmpty:91
Wanted but not invoked:
httpClient.doHttpGet("https://fakestoreapi.com/products/300");
Actually, there were zero interactions with this mock.
```

**Análise:**
-  Testes **compilam** (skeleton está correto)
-  Testes **falham** (lógica não implementada)
-  Mensagens de erro claras (AssertJ)



### Fase GREEN  (Implementar código)

Após implementar `findProductDetails()`:

```bash
mvn clean test
```

**Resultado:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running ProductFinderServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.583 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

 **Todos os testes passam!**

### Fase REFACTOR  (Melhorar código)

Possíveis melhorias (já aplicadas):
- Extrair constante `API_BASE_URL`
- Usar `Optional` para retornos (evita nulls)
- Validar entrada antes de fazer HTTP call
- Try-catch genérico (simplicidade vs granularidade)



**Cobertura:** 7 testes, 100% de sucesso

---

## Comandos Úteis

```bash
# Executar testes
mvn test

# Limpar e testar
mvn clean test

# Ver relatório de cobertura
mvn clean test jacoco:report
open target/site/jacoco/index.html

# Testar apenas ProductFinderService
mvn test -Dtest=ProductFinderServiceTest

# Compilar sem testar
mvn compile
```

---

## Próximos Passos

- [ ] **Parte 2.3:** Implementar HTTP Client real
- [ ] **Parte 2.3:** Criar testes de integração (IT)
- [ ] **Parte 2.3:** Configurar failsafe plugin
- [ ] **Parte 2.3:** Comparar testes unitários vs integração
- [ ] Atualizar README principal com Parte 2

---

## Recursos

- [FakeStore API Documentation](https://fakestoreapi.com/docs)
- [Jackson Databind](https://github.com/FasterXML/jackson-databind)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Core](https://assertj.github.io/doc/)



