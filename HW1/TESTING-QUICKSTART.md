# Quick Start - Testing Guide

## Iniciar Aplicação

```bash
mvn spring-boot:run
```

Aguardar até ver: `Started ZeroMonosApplication`

---

## Opção 2: Comandos curl Manuais

Ver ficheiro detalhado: **[MANUAL-TESTING.md](MANUAL-TESTING.md)**

### Exemplos Rápidos

**Criar Booking:**
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "municipality": "Aveiro",
    "itemDescription": "Sofá velho de 3 lugares",
    "collectionDate": "2025-11-10",
    "timeSlot": "MORNING",
    "address": "Rua das Flores, 123",
    "contactEmail": "teste@example.com",
    "contactPhone": "912345678",
    "numberOfItems": 2
  }'
```

**Listar Bookings:**
```bash
curl http://localhost:8080/api/staff/bookings
```

**Criar Funcionário:**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@zeromones.com",
    "phone": "912345678",
    "role": "DRIVER",
    "municipality": "Aveiro"
  }'
```

---

## Ver Base de Dados (H2 Console)

1. Abrir: http://localhost:8080/h2-console
2. **JDBC URL:** `jdbc:h2:mem:testdb`
3. **Username:** `sa`
4. **Password:** (vazio)
5. Clicar "Connect"

### Queries Úteis:

```sql
-- Ver todos os bookings
SELECT * FROM service_requests;

-- Ver funcionários
SELECT * FROM employees;

-- Ver listas de trabalho
SELECT * FROM work_lists;

-- Ver histórico de estados
SELECT * FROM status_history ORDER BY timestamp DESC;

-- Ver atribuições worklist-booking
SELECT * FROM work_list_requests;

-- Ver bookings com funcionário atribuído
SELECT sr.id, sr.municipality, sr.current_status, e.name as employee_name
FROM service_requests sr
LEFT JOIN employees e ON sr.assigned_employee_id = e.id;
```

---

## Endpoints Disponíveis

### Citizen (Público)
- `POST /api/bookings` - Criar pedido
- `GET /api/bookings/{token}` - Consultar pedido
- `DELETE /api/bookings/{token}` - Cancelar pedido

### Staff - Bookings
- `GET /api/staff/bookings` - Listar todos
- `GET /api/staff/bookings?status=RECEIVED` - Filtrar por status
- `GET /api/staff/bookings?municipality=Aveiro` - Filtrar por município
- `PUT /api/staff/bookings/{id}/status` - Atualizar status

### Staff - Employees (Ponto G)
- `POST /api/employees` - Criar funcionário
- `GET /api/employees` - Listar todos
- `GET /api/employees?municipality=Aveiro` - Filtrar por município
- `GET /api/employees?role=DRIVER` - Filtrar por função
- `GET /api/employees/{id}` - Obter por ID
- `PUT /api/employees/{id}` - Atualizar
- `PATCH /api/employees/{id}/activate` - Ativar
- `PATCH /api/employees/{id}/deactivate` - Desativar

### Staff - WorkLists (Ponto G)
- `POST /api/worklists` - Criar lista de trabalho
- `GET /api/worklists` - Listar (hoje por default)
- `GET /api/worklists?date=2025-11-10` - Filtrar por data
- `GET /api/worklists?municipality=Aveiro` - Filtrar por município
- `GET /api/worklists?employeeId=1` - Filtrar por funcionário
- `GET /api/worklists/{id}` - Obter por ID
- `POST /api/worklists/{id}/requests/{bookingId}` - Atribuir booking
- `PATCH /api/worklists/{id}/start` - Iniciar trabalho
- `PATCH /api/worklists/{id}/complete` - Completar trabalho

---

## Checklist de Testes Manuais

### Funcionalidades Base (A-E)
- [ ] Criar booking válido
- [ ] Consultar booking com token
- [ ] Cancelar booking
- [ ] Listar todos os bookings (staff)
- [ ] Filtrar bookings por status (RECEIVED, IN_PROGRESS, COMPLETED)
- [ ] Filtrar bookings por município
- [ ] Atualizar status: RECEIVED → ASSIGNED
- [ ] Atualizar status: ASSIGNED → IN_PROGRESS
- [ ] Atualizar status: IN_PROGRESS → COMPLETED
- [ ] Testar validação: data no passado (deve falhar)
- [ ] Testar validação: município inválido (deve falhar)
- [ ] Testar validação: descrição curta < 10 chars (deve falhar)
- [ ] Testar validação: mais de 5 items (deve falhar)
- [ ] Testar transição inválida: RECEIVED → COMPLETED (deve falhar)

### Gestão de Funcionários (G)
- [ ] Criar funcionário DRIVER
- [ ] Criar funcionário COLLECTOR
- [ ] Criar funcionário SUPERVISOR
- [ ] Criar funcionário COORDINATOR
- [ ] Listar todos os funcionários
- [ ] Filtrar funcionários por município
- [ ] Filtrar funcionários por role
- [ ] Atualizar dados de funcionário
- [ ] Desativar funcionário
- [ ] Ativar funcionário
- [ ] Tentar criar funcionário com email duplicado (deve falhar)

### Gestão de Listas de Trabalho (G)
- [ ] Criar worklist para funcionário
- [ ] Listar worklists (hoje por default)
- [ ] Filtrar worklists por data
- [ ] Filtrar worklists por município
- [ ] Filtrar worklists por funcionário
- [ ] Atribuir booking a worklist
- [ ] Ver worklist atualizada com booking
- [ ] Iniciar trabalho (PENDING → IN_PROGRESS)
- [ ] Completar trabalho (IN_PROGRESS → COMPLETED)
- [ ] Tentar atribuir booking de município diferente (deve falhar)
- [ ] Tentar criar worklist duplicada (mesmo employee, mesma data) (deve falhar)

### Fluxo Completo (Integração)
- [ ] Criar employee → booking → worklist → atribuir → completar (fluxo completo)
- [ ] Verificar que booking fica com employee atribuído
- [ ] Verificar histórico de estados do booking
- [ ] Verificar que worklist mostra todos os requests atribuídos
- [ ] Verificar estatísticas: totalRequests, totalItems na worklist

---

## 🔍 Debugging

**Ver logs da aplicação:**
```bash
# Na terminal onde o mvn spring-boot:run está a correr
```

**Ver últimos IDs criados (se usar o script):**
```bash
cat /tmp/last_booking_id.txt
cat /tmp/last_access_token.txt
cat /tmp/last_employee_id.txt
cat /tmp/last_worklist_id.txt
```

**Verificar saúde da aplicação:**
```bash
curl http://localhost:8080/actuator/health
```

---

## Documentação Completa

- **[MANUAL-TESTING.md](MANUAL-TESTING.md)** - Todos os comandos curl detalhados
- **[SONARQUBE-SETUP.md](SONARQUBE-SETUP.md)** - Configuração SonarQube

---

## Testes Automatizados

**Executar todos os testes:**
```bash
mvn verify
```

**Apenas unit tests:**
```bash
mvn test
```

**Ver cobertura:**
```bash
mvn verify
open target/site/jacoco/index.html
```

**Análise SonarQube:**
```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=YOUR_TOKEN
```

---

