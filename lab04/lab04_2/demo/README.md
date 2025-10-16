# Lab 3.4 - Meals Booking Backend API


**Universidade de Aveiro**
**Autor:** Daniel Simbe
**Data:** Outubro 2025

##  Obejetivo

Backend REST em Spring Boot para sistema de reserva de refeições em cantina escolar. Reutiliza lógica implementada no Lab 1 (Java puro) e aplica arquitetura em camadas (como Lab 3.3).

**Desenvolvido em:** Java , Spring Boot, Spring Data JPA, Hibernate, PostgreSQL, Maven

---

##  Requisitos Cumpridos

### Parte a) Planeamento da Arquitetura

**Arquitetura em camadas:**

```
HTTP Request
    ↓
MealBookingRestController (@RestController) - boundary
    ↓
MealBookingServiceImpl (@Service) - lógica de negócio
    ↓
MealBookingRepository (JpaRepository) - acesso dados
    ↓
MealBooking (@Entity) ↔ PostgreSQL BD
```

**Reutilização do Lab 1:**
- **Reservation.java** → `MealBooking.java` (entity JPA com `@Entity`, `@Table`, etc.)
- **MealsBookingService.java** → `MealBookingServiceImpl.java`
  - Validações de entrada (studentId, serviceShift)
  - Verificação de capacidade (máx 100 por turno)
  - Estados (used, cancelled)
  - Métodos: bookMeal, checkIn, cancelReservation

- **MealBookingRequest.java** → Não necessário (Spring recebe `@RequestParam`)

**Componentes Spring utilizados:**
1. `@Entity MealBooking` - Mapeia para tabela `meal_bookings`
2. `@Repository MealBookingRepository extends JpaRepository` - CRUD + queries customizadas
3. `@Service MealBookingServiceImpl` - Lógica de negócio
4. `@RestController MealBookingRestController` - Endpoints HTTP

### Parte b) Criar projeto Spring Boot

**Dependências incluídas:**
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- postgresql (runtime)
- lombok

### Parte c) Implementar API REST

**Endpoints implementados e testados:**

 Método                 Endpoint                     Descrição                                  Status 

 POST  `/bookings?studentId=S001&serviceShift=lunch`  Criar nova reserva                      201 Created
 GET  `/bookings/{token}`                            Consultar reserva por token               200 OK / 404
 PATCH  `/bookings/{token}/checkin`                         Fazer check-in                     200 OK
 PATCH  `/bookings/{token}/cancel`                         Cancelar reserva                    200 OK



### Parte d) Configurar PostgreSQL com Docker

**Comandos executados:**

```bash
# Iniciar PostgreSQL
docker run --name postgresdb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=meals_db -p 5432:5432 -d postgres:latest

# Verificar conexão
nc -zv 127.0.0.1 5432
# Connection to 127.0.0.1 5432 port [tcp/postgresql] succeeded!
```

**Configuração em application.properties:**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meals_db
spring.datasource.username=admin
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update
```



---



## Testes com Postman

### 1. Criar Reserva

**Request:**
```
POST http://localhost:8080/bookings?studentId=S001&serviceShift=lunch
```

**Response (201 Created):**
```json
{
  "id": 1,
  "token": "4122a51c",
  "studentId": "S001",
  "serviceShift": "lunch",
  "reservationTime": "2025-10-11T21:50:02.820668",
  "used": false,
  "cancelled": false
}
```

### 2. Consultar Reserva

**Request:**
```
GET http://localhost:8080/bookings/4122a51c
```

**Response (200 OK):**
```json
{
  "id": 1,
  "token": "4122a51c",
  "studentId": "S001",
  "serviceShift": "lunch",
  "reservationTime": "2025-10-11T21:50:02.820668",
  "used": false,
  "cancelled": false
}
```

### 3. Fazer Check-in

**Request:**
```
PATCH http://localhost:8080/bookings/4122a51c/checkin
```

**Response (200 OK):**
```json
{
  "message": "Check-in successful"
}
```

**Após check-in, campo `used` fica true:**
```
GET http://localhost:8080/bookings/4122a51c
```

```json
{
  "id": 1,
  "token": "4122a51c",
  "studentId": "S001",
  "serviceShift": "lunch",
  "reservationTime": "2025-10-11T21:50:02.820668",
  "used": true,        ← Mudou para true
  "cancelled": false
}
```

### 4. Cancelar Reserva

**Request:**
```
PATCH http://localhost:8080/bookings/4122a51c/cancel
```

**Response (200 OK):**
```json
{
  "message": "Reservation cancelled successfully"
}
```

### 5. Testar Validações

**Erro: Estudante já tem reserva neste turno**

```
POST http://localhost:8080/bookings?studentId=S001&serviceShift=lunch
```

(Repetir depois de ter criado a primeira)

**Response (400 Bad Request):**
```json
{
  "error": "Student already has a reservation for this shift"
}
```

**Erro: StudentId vazio**

```
POST http://localhost:8080/bookings?studentId=&serviceShift=lunch
```

**Response (400 Bad Request):**
```json
{
  "error": "Student ID is required"
}
```

**Erro: Turno vazio**

```
POST http://localhost:8080/bookings?studentId=S001&serviceShift=
```

**Response (400 Bad Request):**
```json
{
  "error": "Service shift is required"
}
```

**Erro: Turno cheio (após 100 reservas)**

Quando `currentBookings >= 100`:

**Response (400 Bad Request):**
```json
{
  "error": "No available spots for this shift"
}
```

**Erro: Reserva não encontrada**

```
GET http://localhost:8080/bookings/invalidtoken
```

**Response (404 Not Found):**
```json
{
  "error": "Booking not found"
}
```

---

## Conceitos Aprendidos

 **Reutilizar código Lab 1** em contexto production-ready (Spring Boot)
 **Adaptar classes puras (POJO)** para entidades JPA com anotações
 **Spring Data JPA** e repositories (`extends JpaRepository`)
 **Arquitetura em camadas:**
  - Entity (dados)
  - Repository (persistência)
  - Service (lógica de negócio)
  - Controller (API HTTP)

 **Validação de negócio** em `@Service` (não no controller)
 **HTTP semantics:** POST 201, GET 200, PATCH, DELETE
 **ResponseEntity** para controle fino de status HTTP
 **Docker** para infraestrutura de BD
 **PostgreSQL** como BD relacional
 **Dependency Injection** via `@Autowired`
 **Lombok** para reduzir boilerplate (`@Data`, `@NoArgsConstructor`, etc.)
 **Spring Boot auto-configuration** e component scanning

---

## Comparação: Lab 1 vs Lab 3.4

 Aspecto                    Lab 1                                  Lab 3.4 

  Persistência              `ConcurrentHashMap` (memória)           PostgreSQL (disco) 
  Acesso dados               Manual em `MealsBookingService`       `MealBookingRepository` (Spring Data) 
  Estrutura                     Classe única                        4 camadas (entity, repository, service, controller)
  API                           Nenhuma (console)                   REST endpoints HTTP 
  Deployment                    JAR local                           Docker-ready
  Escalabilidade                Memória limitada                    BD relacional escalável

---


## Dependências do Projeto

```xml
<!-- Spring Boot Starters -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation

<!-- Database -->
org.postgresql:postgresql (runtime)

<!-- Tools -->
org.projectlombok:lombok

<!-- Tests -->
spring-boot-starter-test
```

---



## Conclusão

Este lab consolidou os aprendizados de Lab 1 (lógica de negócio pura) e Lab 3.3 (arquitetura Spring Boot) num projeto backend real e funcional.

**Principais contribuições:**
1. Reutilização de código Lab 1 em contexto production-ready
2. Persistência em BD relacional (PostgreSQL)
3. API REST semântica com HTTP semantics corretos
4. Validações robustas de negócio
5. Infraestrutura dockerizada
6. Código organizado em camadas


---

