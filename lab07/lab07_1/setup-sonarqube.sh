#!/bin/bash

# Script para Lab 7.1 - SonarQube Local Analysis Setup

set -e

echo "🔍 Lab 7.1 - SonarQube Local Analysis"
echo "======================================"
echo ""

# Verificar Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não encontrado!"
    echo "Instale Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

echo "✅ Docker encontrado: $(docker --version)"
echo ""

# Parte b) Configurar SonarQube local
echo "📦 Parte b) Iniciando SonarQube com Docker..."
echo ""

# Parar e remover container antigo se existir
if docker ps -a | grep -q sonarqube-local; then
    echo "⚠️  Container SonarQube existente encontrado. Removendo..."
    docker stop sonarqube-local 2>/dev/null || true
    docker rm sonarqube-local 2>/dev/null || true
fi

# Iniciar SonarQube
echo "🐳 Iniciando container SonarQube..."
docker run -d \
    --name sonarqube-local \
    -p 9000:9000 \
    sonarqube:latest

echo ""
echo "⏳ Aguardando SonarQube inicializar (pode demorar 1-2 minutos)..."
echo "   Verificando http://127.0.0.1:9000 ..."

# Aguardar SonarQube estar pronto
MAX_ATTEMPTS=60
ATTEMPT=0
until curl -s http://127.0.0.1:9000/api/system/status | grep -q '"status":"UP"' || [ $ATTEMPT -eq $MAX_ATTEMPTS ]; do
    printf "."
    sleep 2
    ATTEMPT=$((ATTEMPT + 1))
done

echo ""

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "❌ Timeout esperando SonarQube iniciar"
    echo "💡 Verifique logs: docker logs sonarqube-local"
    exit 1
fi

echo ""
echo "✅ SonarQube está pronto!"
echo ""
echo "📊 Dashboard disponível em: http://127.0.0.1:9000"
echo "🔑 Credenciais padrão:"
echo "   Username: admin"
echo "   Password: admin"
echo ""
echo "⚠️  IMPORTANTE: Você será forçado a mudar a senha no primeiro login!"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📝 Próximos passos (Parte c, d, e):"
echo ""
echo "1. Acesse http://127.0.0.1:9000"
echo "2. Login com admin/admin e mude a senha"
echo "3. Clique em 'Create a local project'"
echo "4. Dê um nome ao projeto (ex: 'my-java-project')"
echo "5. Escolha 'Use the global setting' para baseline"
echo "6. Clique 'Create project'"
echo "7. Selecione 'Locally' como Analysis method"
echo "8. Gere um Access Token (guarde bem!)"
echo "9. Escolha 'Maven' como build tool"
echo "10. Copie o comando Maven gerado"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📋 Template de comando Maven para análise:"
echo ""
echo "# No diretório do seu projeto Maven:"
echo "mvn clean install"
echo ""
echo "mvn clean verify sonar:sonar \\"
echo "  -Dsonar.projectKey=SEU_PROJECT_KEY \\"
echo "  -Dsonar.projectName='Seu Nome do Projeto' \\"
echo "  -Dsonar.host.url=http://127.0.0.1:9000 \\"
echo "  -Dsonar.token=SEU_TOKEN_AQUI"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🛠️  Comandos úteis:"
echo ""
echo "# Ver logs do SonarQube"
echo "docker logs -f sonarqube-local"
echo ""
echo "# Parar SonarQube"
echo "docker stop sonarqube-local"
echo ""
echo "# Reiniciar SonarQube"
echo "docker start sonarqube-local"
echo ""
echo "# Remover SonarQube completamente"
echo "docker stop sonarqube-local && docker rm sonarqube-local"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"