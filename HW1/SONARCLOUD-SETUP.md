# SonarCloud Setup - HW1 Isolado

## Problema Original

O projeto HW1 está dentro do repositório `tqs-indiv-portfolio-dani1244`, que contém múltiplos projetos (lab01, lab02, lab03, etc.). Havia **dois problemas principais**:

1. **SonarCloud analisava todo o repositório** em vez de apenas a pasta `HW1`
2. **GitHub Actions workflow** estava em `HW1/.github/workflows/` (não reconhecido pelo GitHub) em vez de `.github/workflows/` na raiz do repositório

## Solução Implementada

### 1. Criação de Projeto SonarCloud Separado

Mudámos o `projectKey` para ser único:
- **Antes:** `dani1244_tqs-indiv-portfolio-dani1244` (repositório inteiro)
- **Depois:** `dani1244_tqs-hw1-zeromones` (apenas HW1)

### 2. Configuração Atualizada

#### pom.xml
```xml
<sonar.projectKey>dani1244_tqs-hw1-zeromones</sonar.projectKey>
<sonar.projectBaseDir>${project.basedir}</sonar.projectBaseDir>
<sonar.sources>src/main/java</sonar.sources>
<sonar.tests>src/test/java</sonar.tests>
<sonar.scm.disabled>true</sonar.scm.disabled>
```

#### sonar-project.properties
```properties
sonar.projectKey=dani1244_tqs-hw1-zeromones
sonar.projectBaseDir=.
sonar.scm.disabled=true
```

### 3. GitHub Actions Workflow na Raiz do Repositório

O workflow **DEVE** estar em `.github/workflows/` na **raiz do repositório**, não dentro de `HW1/`:

**Localização correta:** `tqs-indiv-portfolio-dani1244/.github/workflows/hw1-ci.yml`

```yaml
name: HW1 - CI & Quality Gate

on:
  push:
    paths:
      - 'HW1/**'  # Só executa quando HW1 muda

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: ./HW1  # Todos os comandos executam dentro de HW1
```

### 4. Passos para Configurar no SonarCloud

1. **Aceder a SonarCloud:** https://sonarcloud.io
2. **Criar Novo Projeto Manualmente:**
   - Click em "+" → "Analyze new project"
   - Escolher "Manually" (não usar automatic import)
   - Organization: `dani1244`
   - Project Key: `dani1244_tqs-hw1-zeromones`
   - Display Name: `HW1 - ZeroMonos Waste Collection`

3. **Configurar Análise:**
   - Choose: "With GitHub Actions"
   - Criar secret `SONAR_TOKEN` no GitHub repository
   - O workflow `.github/workflows/ci.yml` já está configurado

4. **Executar Análise Localmente (opcional):**
   ```bash
   cd HW1
   mvn clean verify jacoco:report
   mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN
   ```

### 4. Verificação

Após o setup, o SonarCloud irá analisar **apenas**:
- ✅ `HW1/src/main/java` (código fonte)
- ✅ `HW1/src/test/java` (testes)
- ✅ Cobertura de código do HW1
- ❌ **NÃO** analisa lab01, lab02, lab03, etc.

### 5. Dashboard SonarCloud

**URL do projeto:** https://sonarcloud.io/project/overview?id=dani1244_tqs-hw1-zeromones

## Configurações Importantes

### Exclusões
Excluímos da análise (para não afetar métricas):
- `**/dto/**` - Data Transfer Objects (simples POJOs)
- `**/model/**` - Entidades JPA (simples POJOs)
- `**/config/**` - Configurações Spring Boot
- `**/exception/**` - Classes de exceção customizadas

### SCM Disabled
```properties
sonar.scm.disabled=true
```
Desabilita o SCM (Git) provider para evitar conflitos ao analisar subfolder.

## Troubleshooting

### Problema: SonarCloud ainda analisa todo o repositório
**Solução:** Verificar se o `projectKey` é único e diferente do repositório pai.

### Problema: "Project not found"
**Solução:** Criar o projeto manualmente no SonarCloud antes de executar a análise.

### Problema: Git information not available
**Solução:** Já configurado `sonar.scm.disabled=true` para resolver isto.

## Comandos Úteis

```bash
# Executar análise local (dentro da pasta HW1)
cd HW1
mvn clean verify jacoco:report sonar:sonar

# Verificar configuração SonarCloud
mvn help:effective-pom | grep sonar

# Limpar análises antigas
rm -rf target/sonar
```

## Referências

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [Analyzing Maven Projects](https://docs.sonarcloud.io/advanced-setup/languages/java/)
- [GitHub Actions Integration](https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/github-actions/)
