# ZeroMonos Waste Collection System

Sistema de gestão de recolha de resíduos volumosos para municípios portugueses.

## Descrição

Aplicação Spring Boot que permite a cidadãos agendar recolhas de resíduos volumosos e a equipas municipais gerir os pedidos.

## Tecnologias

- Java 17
- Spring Boot 3.2.0
- H2 Database
- JUnit 5
- Mockito
- Cucumber
- REST Assured
- JaCoCo

## Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## Testes Implementados

### Testes Unitários
Testes isolados da camada de serviço com uso de mocks:

- [BookingServiceTest.java](src/test/java/com/zeromones/unit/BookingServiceTest.java) - Testa a lógica de negócio para criação, consulta, cancelamento e atualização de pedidos
- [MunicipalityServiceTest.java](src/test/java/com/zeromones/unit/MunicipalityServiceTest.java) - Valida a integração com API externa de municípios

**Executar:**
```bash
mvn test
```

### Testes de Integração
Testes da API REST com base de dados real (H2):

- [BookingControllerIT.java](src/test/java/com/zeromones/integration/BookingControllerIT.java) - Testa endpoints públicos para cidadãos
- [StaffControllerIT.java](src/test/java/com/zeromones/integration/StaffControllerIT.java) - Testa endpoints de gestão para equipas

**Executar:**
```bash
mvn verify
```

### Testes Funcionais (BDD)
Testes de aceitação usando Cucumber:

- [create_booking.feature](src/test/resources/features/create_booking.feature) - Cenários de criação e gestão de pedidos
- [BookingStepDefinitions.java](src/test/java/com/zeromones/functional/BookingStepDefinitions.java) - Implementação dos steps para cidadãos
- [StaffStepDefinitions.java](src/test/java/com/zeromones/functional/StaffStepDefinitions.java) - Implementação dos steps para equipas

**Executar:**
```bash
mvn test -Dtest=CucumberIntegrationTest
```

### Testes de Performance
Validação de desempenho da API:

- [PerformanceTest.java](src/test/java/com/zeromones/performance/PerformanceTest.java) - Testes de carga e tempo de resposta

**Executar:**
```bash
mvn test -Dtest=PerformanceTest
```

### Testes de Segurança
Validação de vulnerabilidades básicas:

- [BasicSecurityTest.java](src/test/java/com/zeromones/security/BasicSecurityTest.java) - Testes de injeção SQL, XSS e headers de segurança

**Executar:**
```bash
mvn test -Dtest=BasicSecurityTest
```

## Cobertura de Código

**Gerar relatório:**
```bash
mvn clean verify
```

O relatório JaCoCo estará disponível em `target/site/jacoco/index.html`.

**Requisitos mínimos:**
- Cobertura de linhas: 40%
- Cobertura de branches: 25%

## Análise de Qualidade

**SonarCloud:**
```bash
mvn clean verify sonar:sonar
```

