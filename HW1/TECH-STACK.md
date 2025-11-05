# ZeroMonos - Stack Tecnológico

## Sobre o Produto

**Nome do Projeto:** ZeroMonos - Sistema de Recolha de Resíduos Volumosos

**Descrição:** Sistema web para gestão e agendamento de recolha de resíduos volumosos (monos) em municípios portugueses, permitindo aos cidadãos criar pedidos de recolha e ao staff gerir as operações.

---

## Tecnologias Utilizadas

### Backend
- **Framework:** Spring Boot 3.2.0
- **Java:** JDK 17
- **Build Tool:** Maven 3.9+
- **Persistência:**
  - Spring Data JPA
  - Hibernate ORM 6.3.1
- **Validação:** Jakarta Validation (Bean Validation)
- **Logging:** SLF4J + Logback

### Frontend
- **Framework:** React 18 (via CDN)
- **Transpilador:** Babel Standalone (para JSX)
- **Linguagem:** JavaScript (ES6+)
- **Estilo:** CSS3 personalizado com:
  - Animações CSS
  - Gradientes modernos
  - Glassmorphism effects
  - Design responsivo
- **Ícones:** Font Awesome 6.4.0

### Base de Dados
- **Desenvolvimento/Testes:** H2 Database (in-memory)
- **Configuração:**
  - H2 Console disponível em `/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`

### APIs Externas
- **API de Municípios:** [GeoAPI Portugal](https://json.geoapi.pt/municipios)
  - Fornece lista completa dos 308 municípios portugueses
  - Integração via RestClient do Spring
  - Caching em memória para otimização

### Qualidade e Testes
- **Testes Unitários:** JUnit 5
- **Testes de Integração:**
  - Spring Boot Test
  - MockMvc
  - Testcontainers
- **BDD:** Cucumber com Selenium WebDriver
- **Cobertura de Código:** JaCoCo
- **Análise Estática:** SonarCloud
- **Mocking:** Mockito

### DevOps e CI/CD
- **Controlo de Versão:** Git + GitHub
- **CI/CD:** GitHub Actions
- **Análise Contínua:** SonarCloud integration
- **Containerização:** Suporte para Docker (opcional)

---

## Arquitetura

### Padrões Arquiteturais
- **MVC (Model-View-Controller)**
- **Repository Pattern**
- **Service Layer Pattern**
- **DTO (Data Transfer Objects)**

### Estrutura do Projeto
```
src/
├── main/
│   ├── java/com/zeromones/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── repository/     # Data Access Layer
│   │   ├── model/          # Domain Entities
│   │   ├── dto/            # Data Transfer Objects
│   │   └── exception/      # Custom Exceptions
│   └── resources/
│       ├── static/         # React Frontend
│       │   ├── css/        # Stylesheets
│       │   └── js/         # JavaScript/React
│       └── application.properties
└── test/                   # Testes automatizados
```

---

## Esquema de Cores Moderno

O sistema utiliza um esquema de cores contemporâneo:

- **Background:** Gradiente azul escuro/roxo (`#0a0e27` → `#1a1f3a` → `#2d1b3d`)
- **Primário (Verde):** `#10b981` - Elementos principais e destaques
- **Secundário (Índigo/Roxo):** `#6366f1`, `#8b5cf6` - Botões e elementos interativos
- **Acento (Laranja):** `#f59e0b`, `#ef4444` - Área do staff
- **Efeitos:** Glassmorphism com `backdrop-filter: blur()`

---

## Funcionalidades Principais

### Área do Cidadão (React SPA)
- ✅ Formulário multi-passo para criar pedidos
- ✅ Validação em tempo real
- ✅ Consulta de pedidos por token
- ✅ Cancelamento de pedidos

### Área do Staff
- ✅ Dashboard de gestão
- ✅ Filtros por município e estado
- ✅ Atualização de estados de pedidos
- ✅ Gestão de colaboradores

### API REST
- ✅ Endpoints RESTful completos
- ✅ Validação de dados
- ✅ Tratamento global de exceções
- ✅ Logging estruturado

---

## Configuração e Execução

### Requisitos
- Java 17+
- Maven 3.9+
- Navegador moderno com suporte a ES6+

### Comandos
```bash
# Build
mvn clean package

# Run
java -jar target/waste-collection-system-1.0.0.jar

# Testes
mvn test

# Análise de Qualidade
mvn sonar:sonar
```

### Acesso
- **Aplicação:** http://localhost:8080
- **H2 Console:** http://localhost:8080/h2-console
- **API Docs:** Endpoints disponíveis em `/api/*`

---

## Credenciais de Acesso (Staff)

```
Username: admin
Password: admin123

Username: manager
Password: manager123

Username: operator
Password: operator123
```

---

---

## Limitações Conhecidas

Na fase atual, o ZeroMonos apresenta várias limitações conhecidas que surgem da sua fase de desenvolvimento. Estas limitações incluem:

### Não Implementação em Produção
O ZeroMonos ainda não foi implementado num ambiente de produção real. Como resultado, não está acessível publicamente online, executando apenas localmente com base de dados H2 em memória. Todos os dados são perdidos ao reiniciar a aplicação, não havendo persistência entre sessões. Não existem configurações separadas para ambientes de desenvolvimento, teste e produção.

### Falta de Documentação Automatizada da API
A aplicação carece de documentação automatizada (Swagger/OpenAPI), tornando desafiador para desenvolvedores e partes interessadas aceder a informações abrangentes e atualizadas sobre as funcionalidades, endpoints e uso da API. Os endpoints também não estão versionados (ex: `/api/v1/bookings`).

### Testes Funcionais BDD Não Implementados
Por limitações de tempo, os testes E2E com Selenium WebDriver e Cucumber não foram implementados. Optou-se por priorizar testes unitários, de integração e de performance, que cobrem a maior parte da lógica de negócio e API. Os testes BDD requerem configuração adicional de WebDriver, gestão de browsers headless, e são mais complexos de manter, tendo sido deixados para uma fase futura do projeto.

### Dashboard de Operações Básico
A área do staff possui funcionalidades básicas de gestão (listar pedidos, filtrar por município/estado, atualizar estados), mas não possui visualizações gráficas avançadas como charts, mapas de calor, ou dashboards analíticos. A implementação de um dashboard completo com bibliotecas como Chart.js ou D3.js requereria tempo adicional significativo e foi considerada uma funcionalidade "nice-to-have" para além do scope mínimo do projeto.

### Cobertura de Testes Limitada
Embora existam testes unitários, de integração, performance e segurança implementados, a cobertura ainda está em 41% (linhas) e 26% (branches), abaixo dos 80% recomendados para produção. Isto deve-se à priorização de testes para funcionalidades críticas em detrimento de cobertura total, falta de testes para métodos auxiliares e DTOs, e casos de erro não testados exaustivamente.

### Vulnerabilidades de Segurança Identificadas
Os testes de segurança implementados (`BasicSecurityTest.java`) identificaram corretamente 3 vulnerabilidades que demonstram a eficácia dos testes: (1) Sensitive Data Exposure - JSON inválido retorna 500 em vez de 400; (2) Path Traversal - tentativas retornam 400 em vez de 404; (3) Information Disclosure - endpoints inexistentes retornam 500 em vez de 404. A correção destas issues requereria configuração adicional do error handling global da aplicação.

---

**Desenvolvido para:** TQS (Testes e Qualidade de Software) - Universidade de Aveiro
**Ano Letivo:** 2024/2025
