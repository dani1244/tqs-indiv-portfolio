# SonarQube/SonarCloud Setup Guide

## Configuração do Projeto

Este projeto já está configurado com integração do SonarQube. Os ficheiros de configuração incluem:

- **pom.xml**: Contém o plugin do SonarQube e propriedades de configuração
- **sonar-project.properties**: Ficheiro de configuração do SonarQube

### Análise: SonarQube Local

Para análise local sem enviar dados para a cloud.

#### Passos para configurar:

1. **Instalar SonarQube com Docker**
   ```bash
   docker run -d --name sonarqube \
     -p 9000:9000 \
     sonarqube:latest
   ```

2. **Aceder ao SonarQube**
   - URL: http://localhost:9000
   - Credenciais padrão: admin/admin

3. **Criar projeto e token**
   - Criar novo projeto no SonarQube
   - Gerar token de autenticação

4. **Executar análise local**
   ```bash
   mvn clean verify sonar:sonar \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.login=your-token-here
   ```

## Métricas Analisadas

O SonarQube analisa:

- **Bugs**: Problemas de código que podem causar erros
- **Vulnerabilities**: Problemas de segurança
- **Code Smells**: Problemas de manutenibilidade
- **Coverage**: Cobertura de testes (via JaCoCo)
- **Duplications**: Código duplicado
- **Complexity**: Complexidade ciclomática

## Relatório de Cobertura Local


```bash
# Gerar relatório
mvn clean verify

# Abrir no browser
open target/site/jacoco/index.html
```

## Exclusões Configuradas

Os seguintes pacotes estão excluídos da análise:
- `**/dto/**` - Data Transfer Objects (apenas getters/setters)
- `**/model/**` - Entidades JPA
- `**/config/**` - Configurações Spring
- `**/exception/**` - Classes de exceção

## Verificação de Cobertura

O projeto está configurado para exigir no mínimo 50% de cobertura de código.
Esta verificação é executada automaticamente durante `mvn verify`.

## Estrutura de Testes

O projeto contém:

- **Unit Tests** (24 testes): `BookingServiceTest`, `MunicipalityServiceTest`
- **Integration Tests** (21 testes): `BookingControllerIT`, `StaffControllerIT`
- **BDD Tests** (9 scenarios): Cucumber tests
- **Performance Tests** (4 testes): Testes de carga

Total: **58 testes** 

## Integração Contínua

Para integrar com CI/CD (GitHub Actions, GitLab CI, etc.):

### GitHub Actions Example

```yaml
name: Build and Analyze

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Build and analyze
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: |
        mvn clean verify sonar:sonar \
          -Dsonar.organization=your-org \
          -Dsonar.host.url=https://sonarcloud.io \
          -Dsonar.login=$SONAR_TOKEN
```

## Recursos Úteis

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [SonarQube Documentation](https://docs.sonarqube.org/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
