# Consultation Records — Architecture Documentation

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Why Hexagonal Architecture?](#2-why-hexagonal-architecture)
3. [Architecture Diagram](#3-architecture-diagram)
4. [Package Structure](#4-package-structure)
5. [Component Reference](#5-component-reference)
   - 5.1 [Application Entry Point](#51-application-entry-point)
   - 5.2 [Domain Models](#52-domain-models)
   - 5.3 [Inbound Ports](#53-inbound-ports-applicationportin)
   - 5.4 [Outbound Ports](#54-outbound-ports-applicationportout)
   - 5.5 [Domain Services](#55-domain-services-applicationdomainservice)
   - 5.6 [Inbound Web Adapters](#56-inbound-web-adapters-adapterinweb)
   - 5.7 [Web DTOs](#57-web-dtos-adapterinwebdto)
   - 5.8 [Outbound Persistence Adapters](#58-outbound-persistence-adapters-adapteroutpersistence)
   - 5.9 [Persistence Entities](#59-persistence-entities-adapteroutpersistenceentity)
   - 5.10 [Persistence Mappers](#510-persistence-mappers-adapteroutpersistencemapper)
   - 5.11 [Security Infrastructure](#511-security-infrastructure-security)
6. [Dependency Rules](#6-dependency-rules)
7. [Key Design Decisions and Their Benefits](#7-key-design-decisions-and-their-benefits)
8. [How to Extend the Application](#8-how-to-extend-the-application)

---

## 1. Project Overview

**Consultation Records** is a Spring Boot REST API for managing medical consultation records. It supports:

- User registration and authentication (JWT-based)
- Password reset via a token-based flow
- Full CRUD management of `Consultation` records linking consultants to patients
- Full CRUD management of `Consultant` records representing medical professionals
- Full CRUD management of `Consultee` records representing patients receiving care

The application is built following **Hexagonal Architecture** (also known as Ports & Adapters), ensuring that the core business logic is completely independent of frameworks, databases, and transport protocols.

---

## 2. Why Hexagonal Architecture?

Traditional layered architectures (Controller → Service → Repository) tend to collapse over time: services start importing repository classes directly, domain objects get annotated with JPA/MongoDB annotations, and HTTP-specific types like `ResponseStatusException` leak into business logic. The result is code that is hard to test in isolation, painful to refactor, and tightly coupled to technology choices made early in the project.

**Hexagonal Architecture** solves this by treating the application as a hexagon with:

- A **core** (domain models + services) that contains pure business logic with zero framework dependencies
- **Ports** (interfaces) that define *what* the core needs from the outside world, without knowing *how* it is provided
- **Adapters** that implement ports and translate between the core and the outside world (HTTP, MongoDB, JWT libraries, etc.)

### Concrete advantages achieved in this project

| Concern | Without Hexagonal | With Hexagonal |
|---|---|---|
| **Testability** | Services depend on `MongoRepository` — you need a running database to test business logic | Domain services only depend on port interfaces — they can be tested with simple in-memory mocks |
| **Technology independence** | Switching from MongoDB to PostgreSQL requires touching domain classes | Only the persistence adapter changes; domain models and services are untouched |
| **Framework independence** | Spring annotations scattered across the domain; migrating away from Spring requires rewriting everything | Domain models are plain Java classes (`@Data` only); the domain can be extracted and used without Spring |
| **Clear boundaries** | Any class can import any other; violations are invisible | The dependency rule is enforced structurally — domain packages have no imports from adapter packages |
| **Single Responsibility** | A `ConsultationService` that knows about HTTP status codes, MongoDB queries, and JWT tokens | Each class does one thing: services express business rules, adapters translate technology concerns |
| **Changeability** | Adding a new delivery channel (e.g., gRPC, CLI) requires modifying existing services | A new inbound adapter is added without touching any existing code |

---

## 3. Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           OUTSIDE WORLD                                  │
│                                                                          │
│   HTTP Client         MongoDB Atlas          JWT / Security              │
│       │                    │                       │                     │
└───────┼────────────────────┼───────────────────────┼─────────────────────┘
        │                    │                       │
        ▼                    ▼                       ▼
┌───────────────┐  ┌──────────────────┐  ┌─────────────────┐
│  INBOUND      │  │  OUTBOUND        │  │  OUTBOUND        │
│  ADAPTERS     │  │  PERSISTENCE     │  │  SECURITY        │
│               │  │  ADAPTERS        │  │  ADAPTER         │
│  Auth-        │  │                  │  │                  │
│  Controller   │  │  Consultation-   │  │  JwtUtil         │
│               │  │  Adapter         │  │  (implements     │
│  Consultation-│  │                  │  │   TokenPort)     │
│  Controller   │  │  Consultant-     │  │                  │
│               │  │  Adapter         │  │  JwtAuthentic-   │
│  Consultant-  │  │                  │  │  ationFilter     │
│  Controller   │  │  Consultee-      │  │                  │
│               │  │  Adapter         │  │  SecurityConfig  │
│  Consultee-   │  │                  │  │                  │
│  Controller   │  │  UserAdapter     │  │                  │
│               │  │                  │  │                  │
│  (Web DTOs)   │  │  (Persistence    │  │                  │
│               │  │   Entities +     │  │                  │
│               │  │   Mappers)       │  │                  │
└───────┬───────┘  └────────┬─────────┘  └────────┬────────┘
        │                   │                      │
        │    PORT/IN         │   PORT/OUT           │  PORT/OUT
        ▼                   ▼                      ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                         APPLICATION CORE                                  │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                      PORTS (interfaces)                          │    │
│  │                                                                  │    │
│  IN:  AuthUseCase   ConsultationUseCase   ConsultantUseCase      │    │
│       ConsulteeUseCase                                           │    │
│       CreateConsultationCommand   UpdateConsultationCommand      │    │
│       CreateConsultantCommand     UpdateConsultantCommand        │    │
│       CreateConsulteeCommand      UpdateConsulteeCommand         │    │
│                                                                  │    │
│  OUT: UserPort   ConsultationPort   ConsultantPort               │    │
│       ConsulteePort   TokenPort                                  │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                  DOMAIN SERVICES                                 │    │
│  │                                                                  │    │
│  │   AuthService   ConsultationService   ConsultantService          │    │
│  │   ConsulteeService                                               │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                  DOMAIN MODELS                                   │    │
│  │                                                                  │    │
│  │   Consultation   Consultant   Consultee   Goals   User           │    │
│  └─────────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────────┘
```

**The Dependency Rule:** arrows always point *inward*. Adapters depend on the core; the core never depends on adapters. This is the fundamental invariant that all other benefits flow from.

---

## 4. Package Structure

```
com.vkc.consultation.records
│
├── ConsultationRecordsApplication.java          # Bootstrap
│
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── AuthController.java              # Inbound: HTTP auth endpoints
│   │       ├── ConsultationController.java      # Inbound: HTTP consultation endpoints
│   │       ├── ConsultantController.java        # Inbound: HTTP consultant endpoints
│   │       ├── ConsulteeController.java         # Inbound: HTTP consultee endpoints
│   │       └── dto/
│   │           ├── AuthResponse.java
│   │           ├── LoginRequest.java
│   │           ├── RegisterRequest.java
│   │           ├── ForgotPasswordRequest.java
│   │           ├── ResetPasswordRequest.java
│   │           ├── ConsultationResponse.java
│   │           ├── CreateConsultationRequest.java
│   │           ├── UpdateConsultationRequest.java
│   │           ├── ConsultantResponse.java
│   │           ├── CreateConsultantRequest.java
│   │           ├── UpdateConsultantRequest.java
│   │           ├── ConsulteeResponse.java
│   │           ├── CreateConsulteeRequest.java
│   │           └── UpdateConsulteeRequest.java
│   └── out/
│       └── persistence/
│           ├── ConsultationAdapter.java         # Outbound: implements ConsultationPort
│           ├── ConsultationRepository.java      # Spring Data MongoDB repository
│           ├── ConsultantAdapter.java           # Outbound: implements ConsultantPort
│           ├── ConsultantRepository.java        # Spring Data MongoDB repository
│           ├── ConsulteeAdapter.java            # Outbound: implements ConsulteePort
│           ├── ConsulteeRepository.java         # Spring Data MongoDB repository
│           ├── UserAdapter.java                 # Outbound: implements UserPort
│           ├── UserRepository.java              # Spring Data MongoDB repository
│           ├── entity/
│           │   ├── ConsultationDocument.java    # @Document persistence entity
│           │   ├── ConsultantDocument.java
│           │   ├── ConsulteeDocument.java
│           │   ├── UserDocument.java
│           │   └── GoalsDocument.java
│           └── mapper/
│               ├── ConsultationMapper.java      # Domain ↔ Document translation
│               ├── ConsultantMapper.java
│               ├── ConsulteeMapper.java
│               └── UserMapper.java
│
├── application/
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Consultation.java                # Pure domain model
│   │   │   ├── Consultant.java
│   │   │   ├── Consultee.java
│   │   │   ├── Goals.java
│   │   │   └── User.java
│   │   └── service/
│   │       ├── AuthService.java                 # Business logic: authentication
│   │       ├── ConsultationService.java         # Business logic: consultations
│   │       ├── ConsultantService.java           # Business logic: consultants
│   │       └── ConsulteeService.java            # Business logic: consultees
│   └── port/
│       ├── in/
│       │   ├── AuthUseCase.java                 # Inbound port: auth contract
│       │   ├── ConsultationUseCase.java         # Inbound port: consultation contract
│       │   ├── ConsultantUseCase.java           # Inbound port: consultant contract
│       │   ├── ConsulteeUseCase.java            # Inbound port: consultee contract
│       │   ├── CreateConsultationCommand.java   # Command value object
│       │   ├── UpdateConsultationCommand.java   # Command value object
│       │   ├── CreateConsultantCommand.java     # Command value object
│       │   ├── UpdateConsultantCommand.java     # Command value object
│       │   ├── CreateConsulteeCommand.java      # Command value object
│       │   └── UpdateConsulteeCommand.java      # Command value object
│       └── out/
│           ├── UserPort.java                    # Outbound port: user persistence
│           ├── ConsultationPort.java            # Outbound port: consultation persistence
│           ├── ConsultantPort.java              # Outbound port: consultant persistence
│           ├── ConsulteePort.java               # Outbound port: consultee persistence
│           └── TokenPort.java                   # Outbound port: token generation
│
└── security/
    ├── JwtUtil.java                             # Infrastructure: JWT generation/validation
    ├── JwtAuthenticationFilter.java             # Infrastructure: per-request JWT filter
    └── SecurityConfig.java                      # Infrastructure: Spring Security config
```

---

## 5. Component Reference

### 5.1 Application Entry Point

#### `ConsultationRecordsApplication`
**Package:** `com.vkc.consultation.records`

The Spring Boot bootstrap class. Contains only `@SpringBootApplication` and `main()`. Its sole responsibility is to start the Spring application context. No business logic belongs here.

**Hexagonal relevance:** Kept intentionally minimal. The application core has no knowledge of how it is started; this class is purely infrastructure glue.

---

### 5.2 Domain Models

**Package:** `application.domain.model`

Domain models are the heart of the application. They represent the core business concepts and carry no framework annotations — only Lombok's `@Data` for boilerplate reduction (which is a code-generation tool, not a framework dependency that affects runtime behaviour).

| Class | Responsibility |
|---|---|
| `Consultation` | Represents a single medical consultation event. Fields: `id`, `code`, `type`, `consultantCode`, `patientCode`, `diagnosis`, `prescription`, `comments`, `consultationDate`, `followUpDate`, `updatedDate`, `createdBy`, `fee` |
| `Consultant` | Represents a medical professional. Fields: `id`, `code`, `name`, `speciality`, `qualification`, `experienceYears`, `fee` |
| `Consultee` | Represents a patient receiving care. Fields: `id`, `code`, `name`, `gender`, `dob`, `condition`, `address`, `phone`, `email`, `startDate`, `recoveryStatus` |
| `Goals` | Represents a health goal for a patient. Fields: `id`, `code`, `name`, `description`, `importance`, `difficulty`, `achievingAgeYears`, `achievingAgeMonths`, `remarks`, `periodInMonths`, `createdDate`, `updatedDate`, `status` |
| `User` | Represents a system user (healthcare administrator). Fields: `id`, `email`, `passwordHash`, `roles`, `createdAt`, `resetToken`, `resetTokenExpiry` |

**Why this matters:** In a naïve implementation, domain models carry `@Document`, `@Id`, and `@Indexed` annotations from Spring Data MongoDB. This creates a hidden coupling: the domain model *is* the persistence entity. Changing the database collection name, adding a MongoDB index, or switching databases requires modifying a business concept class. By keeping domain models annotation-free, these concerns are completely separated.

---

### 5.3 Inbound Ports (`application/port/in`)

Inbound ports define *what the application can do* — the use cases that the outside world (HTTP, CLI, message queue) can invoke. They are plain Java interfaces.

#### `AuthUseCase`
Defines the authentication contract:
- `register(email, password)` → JWT token
- `login(email, password)` → JWT token
- `forgotPassword(email)` — generates a password-reset token
- `resetPassword(email, resetToken, newPassword)` — validates token and updates password

#### `ConsultationUseCase`
Defines the consultation management contract:
- `findConsultations()` — list all
- `findConsultationById(id)` — find by MongoDB id
- `findConsultationByCode(code)` — find by business code
- `findConsultationByConsultant(consultantCode)` — filter by consultant
- `findConsultationByPatient(patientCode)` — filter by patient
- `createConsultation(CreateConsultationCommand)` — create new record
- `updateConsultation(id, UpdateConsultationCommand)` — update existing record
- `deleteConsultation(id)` — delete by id

#### `CreateConsultationCommand`
A Java `record` (immutable value object) carrying all data needed to create a consultation. Having a dedicated command object instead of passing the raw domain `Consultation` model provides two benefits:
1. The caller (controller) cannot accidentally set `id` or other fields the application core should control
2. The port contract is stable even if the domain model gains or loses internal fields

#### `UpdateConsultationCommand`
Same rationale as `CreateConsultationCommand`, scoped to update operations. Includes `updatedDate` since updates may carry an explicit timestamp.

#### `ConsultantUseCase`
Defines the consultant management contract:
- `findConsultants()` — list all
- `findConsultantById(id)` — find by MongoDB id
- `findConsultantByCode(code)` — find by business code
- `createConsultant(CreateConsultantCommand)` — create new record
- `updateConsultant(id, UpdateConsultantCommand)` — update existing record
- `deleteConsultant(id)` — delete by id

#### `CreateConsultantCommand`
An immutable Java `record` carrying the data needed to create a consultant: `code`, `name`, `speciality`, `qualification`, `experienceYears`, `fee`. No `id` field — the application core assigns the identity.

#### `UpdateConsultantCommand`
Same fields as `CreateConsultantCommand`, scoped to update operations.

#### `ConsulteeUseCase`
Defines the consultee (patient) management contract:
- `findConsultees()` — list all
- `findConsulteeById(id)` — find by MongoDB id
- `findConsulteeByCode(code)` — find by business code
- `createConsultee(CreateConsulteeCommand)` — create new record
- `updateConsultee(id, UpdateConsulteeCommand)` — update existing record
- `deleteConsultee(id)` — delete by id

#### `CreateConsulteeCommand`
An immutable Java `record` carrying the data needed to register a consultee: `code`, `name`, `gender`, `dob`, `condition`, `address`, `phone`, `email`, `startDate`. No `id` field — the application core assigns the identity.

#### `UpdateConsulteeCommand`
Same fields as `CreateConsulteeCommand`, scoped to update operations.

**Why this matters:** Inbound ports form a stable API surface. The HTTP controller, a future gRPC adapter, or a CLI tool all call the same interface. Business logic changes (e.g., adding validation to `createConsultation`) happen in one place — the service implementation — and all callers benefit automatically. Command objects make the contract explicit and protect domain invariants.

---

### 5.4 Outbound Ports (`application/port/out`)

Outbound ports define *what the application needs from the outside world*. The domain services declare their needs as interfaces; they never know (or care) how those needs are fulfilled.

#### `UserPort`
```java
Optional<User> findByEmail(String email);
User save(User user);
boolean existsByEmail(String email);
```
The domain service needs to look up and persist users. This interface says nothing about MongoDB, SQL, or any other storage mechanism.

#### `ConsultationPort`
```java
List<Consultation> findConsultations();
Consultation findConsultationById(String id);
Consultation findConsultationByCode(String code);
List<Consultation> findConsultationsByConsultant(String consultantCode);
List<Consultation> findConsultationsByPatient(String patientCode);
Consultation saveConsultation(Consultation consultation);
boolean existsById(String id);
void deleteById(String id);
```
The full persistence contract for consultations, expressed in domain terms.

#### `ConsultantPort`
```java
List<Consultant> findAll();
Optional<Consultant> findById(String id);
Optional<Consultant> findByCode(String code);
Consultant save(Consultant consultant);
boolean existsById(String id);
void deleteById(String id);
```
The full persistence contract for consultants, expressed in domain terms.

#### `ConsulteePort`
```java
List<Consultee> findAll();
Optional<Consultee> findById(String id);
Optional<Consultee> findByCode(String code);
Consultee save(Consultee consultee);
boolean existsById(String id);
void deleteById(String id);
```
The full persistence contract for consultees, expressed in domain terms.

#### `TokenPort`
```java
String generateToken(String subject);
```
The domain service needs to generate authentication tokens after a successful login or registration. It does not need to know that the implementation uses JJWT, HMAC-SHA256, or any specific algorithm. `TokenPort` abstracts this to a single method.

**Why this matters:** Outbound ports are the mechanism by which the Dependency Inversion Principle is applied. Without them, `AuthService` would `import com.vkc.consultation.records.security.JwtUtil` — a compile-time dependency from the application core into the infrastructure layer. With `TokenPort`, that dependency arrow is inverted: `JwtUtil` depends on the port interface, not the other way around. This means you can swap the token implementation (e.g., to an opaque token store or a different library) by writing a new adapter class without touching a single line of business logic.

---

### 5.5 Domain Services (`application/domain/service`)

Domain services implement the inbound port interfaces and contain all business logic. They depend *only* on:
- Other domain models
- Inbound port interfaces (their own contract)
- Outbound port interfaces (what they need from the outside)

They have zero imports from `adapter.*`, `security.*`, or any Spring Web / MongoDB packages.

#### `AuthService`
**Implements:** `AuthUseCase`  
**Depends on:** `UserPort`, `PasswordEncoder`, `TokenPort`

| Method | Business logic |
|---|---|
| `register` | Checks email uniqueness via `UserPort`, hashes the password, persists the user, returns a token via `TokenPort` |
| `login` | Finds the user by email, verifies the BCrypt hash matches, returns a token |
| `forgotPassword` | Finds user, generates a UUID reset token, sets a 1-hour expiry, persists via `UserPort` |
| `resetPassword` | Validates the reset token exists and has not expired, updates the password hash, clears the reset token |

> Note: `PasswordEncoder` is a Spring Security interface, not an implementation class — accepting an interface is a valid abstraction. The concrete `BCryptPasswordEncoder` is wired in `SecurityConfig`, not in this service.

#### `ConsultationService`
**Implements:** `ConsultationUseCase`  
**Depends on:** `ConsultationPort`

| Method | Business logic |
|---|---|
| `createConsultation` | Maps `CreateConsultationCommand` → `Consultation` domain object, sets `updatedDate` server-side, delegates to `ConsultationPort.saveConsultation` |
| `updateConsultation` | Verifies record exists via `existsById`, maps `UpdateConsultationCommand` → `Consultation` with the target `id` set, delegates to `saveConsultation` |
| `deleteConsultation` | Verifies record exists, delegates to `deleteById` |
| Query methods | Pass-through to `ConsultationPort` |

#### `ConsultantService`
**Implements:** `ConsultantUseCase`  
**Depends on:** `ConsultantPort`

| Method | Business logic |
|---|---|
| `createConsultant` | Maps `CreateConsultantCommand` → `Consultant` domain object, delegates to `ConsultantPort.save` |
| `updateConsultant` | Verifies record exists via `existsById`, maps `UpdateConsultantCommand` → `Consultant` with the target `id` set, delegates to `save` |
| `deleteConsultant` | Verifies record exists, delegates to `deleteById` |
| Query methods | Pass-through to `ConsultantPort` |

#### `ConsulteeService`
**Implements:** `ConsulteeUseCase`  
**Depends on:** `ConsulteePort`

| Method | Business logic |
|---|---|
| `createConsultee` | Maps `CreateConsulteeCommand` → `Consultee` domain object, delegates to `ConsulteePort.save` |
| `updateConsultee` | Verifies record exists via `existsById`, maps `UpdateConsulteeCommand` → `Consultee` with the target `id` set, delegates to `save` |
| `deleteConsultee` | Verifies record exists, delegates to `deleteById` |
| Query methods | Pass-through to `ConsulteePort` |

**Why this matters:** Domain services are the most important classes for unit testing. Because they have no framework dependencies, testing `ConsultationService.updateConsultation` is as simple as providing a mock `ConsultationPort` — no Spring context, no MongoDB container, no network required. Tests run in milliseconds and test only the business rules.

---

### 5.6 Inbound Web Adapters (`adapter/in/web`)

Inbound adapters are the entry points into the application. They translate HTTP requests into use case invocations and translate use case results back into HTTP responses.

#### `AuthController`
**Base path:** `/auth`  
**Depends on:** `AuthUseCase` (the inbound port — never the service class directly)

| Endpoint | Method | Description |
|---|---|---|
| `/auth/register` | `POST` | Accepts `RegisterRequest`, returns `AuthResponse` with JWT |
| `/auth/login` | `POST` | Accepts `LoginRequest`, returns `AuthResponse` with JWT |
| `/auth/forgot-password` | `POST` | Accepts `ForgotPasswordRequest`, returns `204 No Content` |
| `/auth/reset-password` | `POST` | Accepts `ResetPasswordRequest`, returns `204 No Content` |

All `/auth/**` endpoints are publicly accessible (no JWT required, configured in `SecurityConfig`).

#### `ConsultationController`
**Depends on:** `ConsultationUseCase`, command objects from `port/in`

| Endpoint | Method | Description |
|---|---|---|
| `/consultations` | `GET` | Returns all consultations as `List<ConsultationResponse>` |
| `/consultations/id/{id}` | `GET` | Returns single `ConsultationResponse` by MongoDB id |
| `/consultations/code/{code}` | `GET` | Returns single `ConsultationResponse` by business code |
| `/consultations/consultant/{code}` | `GET` | Returns list filtered by consultant code |
| `/consultations/patient/{code}` | `GET` | Returns list filtered by patient code |
| `/consultations` | `POST` | Accepts `CreateConsultationRequest`, builds `CreateConsultationCommand`, returns `ConsultationResponse` |
| `/consultations/id/{id}` | `PUT` | Accepts `UpdateConsultationRequest`, builds `UpdateConsultationCommand`, returns `ConsultationResponse` |
| `/consultations/id/{id}` | `DELETE` | Deletes by id, returns `204 No Content` |

All `/consultations/**` endpoints require a valid JWT Bearer token.

#### `ConsultantController`
**Base path:** `/consultants`  
**Depends on:** `ConsultantUseCase`, command objects from `port/in`

| Endpoint | Method | Description |
|---|---|---|
| `/consultants` | `GET` | Returns all consultants as `List<ConsultantResponse>` |
| `/consultants/id/{id}` | `GET` | Returns single `ConsultantResponse` by MongoDB id |
| `/consultants/code/{code}` | `GET` | Returns single `ConsultantResponse` by business code |
| `/consultants` | `POST` | Accepts `CreateConsultantRequest`, builds `CreateConsultantCommand`, returns `ConsultantResponse` with `201 Created` |
| `/consultants/id/{id}` | `PUT` | Accepts `UpdateConsultantRequest`, builds `UpdateConsultantCommand`, returns `ConsultantResponse` |
| `/consultants/id/{id}` | `DELETE` | Deletes by id, returns `204 No Content` |

All `/consultants/**` endpoints require a valid JWT Bearer token.

#### `ConsulteeController`
**Base path:** `/consultees`  
**Depends on:** `ConsulteeUseCase`, command objects from `port/in`

| Endpoint | Method | Description |
|---|---|---|
| `/consultees` | `GET` | Returns all consultees as `List<ConsulteeResponse>` |
| `/consultees/id/{id}` | `GET` | Returns single `ConsulteeResponse` by MongoDB id |
| `/consultees/code/{code}` | `GET` | Returns single `ConsulteeResponse` by business code |
| `/consultees` | `POST` | Accepts `CreateConsulteeRequest`, builds `CreateConsulteeCommand`, returns `ConsulteeResponse` with `201 Created` |
| `/consultees/id/{id}` | `PUT` | Accepts `UpdateConsulteeRequest`, builds `UpdateConsulteeCommand`, returns `ConsulteeResponse` |
| `/consultees/id/{id}` | `DELETE` | Deletes by id, returns `204 No Content` |

All `/consultees/**` endpoints require a valid JWT Bearer token.

**Why this matters:** Controllers are intentionally thin. They contain no business logic — only HTTP-to-domain translation. This means the HTTP layer can be replaced (e.g., with a gRPC adapter or a message consumer) without touching any business rules. Because controllers depend on the `*UseCase` interface and not on the concrete service class, the controller tests can mock the interface without booting a Spring context.

---

### 5.7 Web DTOs (`adapter/in/web/dto`)

DTOs (Data Transfer Objects) are `record` types that represent the shape of JSON payloads at the HTTP boundary. They are completely separate from domain models.

| Class | Direction | Purpose |
|---|---|---|
| `RegisterRequest` | Inbound | `{ email, password }` for registration |
| `LoginRequest` | Inbound | `{ email, password }` for login |
| `ForgotPasswordRequest` | Inbound | `{ email }` for password reset initiation |
| `ResetPasswordRequest` | Inbound | `{ email, resetToken, newPassword }` |
| `AuthResponse` | Outbound | `{ token }` returned after login/register |
| `CreateConsultationRequest` | Inbound | All fields needed to create a consultation (no `id`) |
| `UpdateConsultationRequest` | Inbound | All fields needed to update a consultation (no `id`) |
| `ConsultationResponse` | Outbound | Full consultation data including `id`; has a static `from(Consultation)` factory |
| `CreateConsultantRequest` | Inbound | `{ code, name, speciality, qualification, experienceYears, fee }` (no `id`) |
| `UpdateConsultantRequest` | Inbound | Same fields as `CreateConsultantRequest` |
| `ConsultantResponse` | Outbound | Full consultant data including `id`; has a static `from(Consultant)` factory |
| `CreateConsulteeRequest` | Inbound | `{ code, name, gender, dob, condition, address, phone, email, startDate }` (no `id`) |
| `UpdateConsulteeRequest` | Inbound | Same fields as `CreateConsulteeRequest` |
| `ConsulteeResponse` | Outbound | Full consultee data including `id`; has a static `from(Consultee)` factory |

**Why this matters:** Without dedicated DTOs, the domain model is serialised directly over HTTP. This means:
- Persistence metadata (`@Id`) is exposed in the API response
- Clients can submit an `id` field in a POST request (which should never be client-controlled)
- Any field added to the domain model for business reasons automatically appears in the API without a conscious decision

By isolating DTOs in the adapter layer, the API contract is explicitly designed and stable, independent of internal model evolution.

---

### 5.8 Outbound Persistence Adapters (`adapter/out/persistence`)

Outbound adapters implement the outbound ports and translate between the domain model and MongoDB.

#### `ConsultationAdapter`
**Implements:** `ConsultationPort`  
**Depends on:** `ConsultationRepository`, `ConsultationMapper`

Every method translates: domain objects to `ConsultationDocument` (via mapper) before writing to MongoDB, and `ConsultationDocument` back to domain objects (via mapper) after reading.

#### `UserAdapter`
**Implements:** `UserPort`  
**Depends on:** `UserRepository`, `UserMapper`

Same translation pattern: `User` ↔ `UserDocument` via `UserMapper`.

#### `ConsultantAdapter`
**Implements:** `ConsultantPort`  
**Depends on:** `ConsultantRepository`, `ConsultantMapper`

Same translation pattern: `Consultant` ↔ `ConsultantDocument` via `ConsultantMapper`.

#### `ConsulteeAdapter`
**Implements:** `ConsulteePort`  
**Depends on:** `ConsulteeRepository`, `ConsulteeMapper`

Same translation pattern: `Consultee` ↔ `ConsulteeDocument` via `ConsulteeMapper`.

#### `ConsultationRepository`
Extends `MongoRepository<ConsultationDocument, String>`. Provides Spring Data auto-generated queries plus three custom `@Query` methods for filtering by `code`, `consultantCode`, and `patientCode`. Uses `ConsultationDocument` — never the domain `Consultation` class.

#### `ConsultantRepository`
Extends `MongoRepository<ConsultantDocument, String>`. Provides one custom `@Query` method: `findByCode(String code)`. Uses `ConsultantDocument`.

#### `ConsulteeRepository`
Extends `MongoRepository<ConsulteeDocument, String>`. Provides one custom `@Query` method: `findByCode(String code)`. Uses `ConsulteeDocument`.

#### `UserRepository`
Extends `MongoRepository<UserDocument, String>`. Provides `findByEmail` and `existsByEmail`. Uses `UserDocument`.

**Why this matters:** The repository interfaces only know about persistence entity classes (`*Document`). This means Spring Data's query derivation, `@Document` collection names, and MongoDB-specific annotations are all confined to the adapter layer. The domain core is completely unaware that MongoDB is being used.

---

### 5.9 Persistence Entities (`adapter/out/persistence/entity`)

Persistence entities are the MongoDB-specific counterparts of the domain models. They carry all Spring Data and MongoDB annotations.

| Class | Collection | Key annotations |
|---|---|---|
| `ConsultationDocument` | `Consultation` | `@Document`, `@Id` |
| `UserDocument` | `users` | `@Document`, `@Id`, `@Indexed(unique=true)` on `email` |
| `ConsultantDocument` | `Consultant` | `@Document`, `@Id` |
| `ConsulteeDocument` | `Consultee` | `@Document`, `@Id` |
| `GoalsDocument` | `Goals` | `@Document`, `@Id` |

**Why this matters:** Before this separation existed, the domain model `Consultation` was both the business concept and the MongoDB document. The `@Document(collection = "Consultation")` annotation on a pure business class is a violation: it couples the business concept to the storage schema. Separate entity classes allow the MongoDB schema to evolve (renaming collections, adding indexes, changing field types) without changing the domain model, and vice versa.

---

### 5.10 Persistence Mappers (`adapter/out/persistence/mapper`)

Mappers are stateless utility classes (private constructors, all-static methods) that perform the bidirectional translation between domain models and persistence entities.

#### `ConsultationMapper`
- `toDomain(ConsultationDocument) → Consultation`
- `toDocument(Consultation) → ConsultationDocument`

#### `ConsultantMapper`
- `toDomain(ConsultantDocument) → Consultant`
- `toDocument(Consultant) → ConsultantDocument`

#### `ConsulteeMapper`
- `toDomain(ConsulteeDocument) → Consultee`
- `toDocument(Consultee) → ConsulteeDocument`

#### `UserMapper`
- `toDomain(UserDocument) → User`
- `toDocument(User) → UserDocument`

**Why this matters:** Mappers are the seam between two worlds. All field-by-field translation is in one place, making it easy to see what the mapping is and to change it. When the domain model and the persistence entity diverge (e.g., the entity stores a date as `String` for legacy reasons but the domain uses `Instant`), the mapper is the only place that needs to handle the conversion. This is far better than having the conversion scattered across service methods or embedded in constructors.

---

### 5.11 Security Infrastructure (`security`)

The `security` package contains infrastructure components for Spring Security and JWT token handling. These are intentionally outside the `adapter` package hierarchy because they cross-cut both inbound (filter chain) and outbound (token generation) concerns.

#### `JwtUtil`
**Implements:** `TokenPort`  
Uses JJWT 0.12.6 to generate and validate HMAC-SHA256-signed JWTs. Reads `jwt.secret` and `jwt.expiration-ms` from `application.properties`.

- `generateToken(subject)` — builds and signs a JWT with the email as subject, current time as issued-at, and configured expiry
- `extractEmail(token)` — parses the token and returns the subject claim
- `isValid(token)` — returns `true` if the token can be parsed and verified without exception

By implementing `TokenPort`, `JwtUtil` participates in the ports-and-adapters structure: the domain service depends on the port interface, and Spring injects this concrete implementation. Swapping to a different JWT library or a token introspection endpoint requires only a new implementation of `TokenPort`.

#### `JwtAuthenticationFilter`
Extends `OncePerRequestFilter`. Runs on every request before the security chain:
1. Reads the `Authorization: Bearer <token>` header
2. Calls `JwtUtil.isValid()` and `JwtUtil.extractEmail()`
3. If valid, creates a `UsernamePasswordAuthenticationToken` and sets it in the `SecurityContextHolder`

This filter is what makes secured endpoints enforce JWT authentication without any annotation on the controller.

#### `SecurityConfig`
Configures the Spring Security filter chain:
- Session management: `STATELESS` — no server-side session state; every request must carry its JWT
- CSRF: disabled — appropriate for a stateless REST API consumed by a known SPA
- Public paths: `/auth/**` and `OPTIONS /**` (for CORS preflight) are permitted without authentication
- All other paths require an authenticated request
- `JwtAuthenticationFilter` is inserted before `UsernamePasswordAuthenticationFilter`
- Exposes a `BCryptPasswordEncoder` bean used by `AuthService`

**Why this matters:** Security is a cross-cutting infrastructure concern. By keeping it in its own package and having it interact with the core only through `TokenPort`, the business logic remains clean. The domain service `AuthService` does not need to know how tokens are generated — it just calls `tokenPort.generateToken(email)`. Security policy changes (token expiry, algorithm, key rotation) are entirely localised to this package.

---

## 6. Dependency Rules

The fundamental rule of hexagonal architecture: **source code dependencies always point inward, toward the domain core. Nothing in the core references anything outside the core.**

```
adapter/in/web         →  application/port/in       ✓ allowed
adapter/out/persistence →  application/port/out      ✓ allowed
security/JwtUtil        →  application/port/out      ✓ allowed (implements TokenPort)
application/domain/service → application/port/in     ✓ allowed (implements UseCase)
application/domain/service → application/port/out    ✓ allowed (depends on port)
application/domain/service → adapter/**              ✗ FORBIDDEN
application/domain/model   → adapter/**              ✗ FORBIDDEN
application/domain/model   → security/**             ✗ FORBIDDEN
application/port/**        → adapter/**              ✗ FORBIDDEN
```

These rules are currently enforced by code review and architectural discipline. They could be enforced automatically with a tool like [ArchUnit](https://www.archunit.org/):

```java
// Example ArchUnit rule
noClasses().that().resideInAPackage("..application..")
    .should().dependOnClassesThat()
    .resideInAPackage("..adapter..");
```

---

## 7. Key Design Decisions and Their Benefits

### Decision 1: Domain models have zero framework imports
**Before:** `Consultation` carried `@Document(collection="Consultation")` and `@Id` from Spring Data MongoDB.  
**After:** `Consultation` has only Lombok `@Data`. Separate `ConsultationDocument` in the adapter layer carries the MongoDB annotations.  
**Benefit:** The domain concept of a "Consultation" is now truly independent. It can be serialised to XML, stored in PostgreSQL, or sent over a message queue by adding a new adapter — zero changes to the domain model.

### Decision 2: Ports as the only coupling point
**Before:** `ConsultationService` imported `ConsultationAdapter` directly (a concrete class from the infrastructure layer).  
**After:** `ConsultationService` imports only `ConsultationPort` (an interface in the application layer).  
**Benefit:** The service can be tested with any object that implements `ConsultationPort`. The adapter can be replaced without changing the service. The Dependency Inversion Principle is applied at the architectural level, not just the class level.

### Decision 3: `TokenPort` abstracts JWT generation
**Before:** `AuthService` imported `JwtUtil` from the `security` package.  
**After:** `AuthService` imports `TokenPort` from `application/port/out`. `JwtUtil` implements `TokenPort`.  
**Benefit:** `AuthService` can be unit tested without any JWT library on the classpath. Token generation strategy can be changed (e.g., opaque tokens, database-backed sessions) by implementing a new `TokenPort` adapter.

### Decision 4: Command objects in inbound ports
**Before:** `ConsultationUseCase.createConsultation(Consultation)` accepted the raw domain model.  
**After:** `ConsultationUseCase.createConsultation(CreateConsultationCommand)` accepts an immutable value object.  
**Benefit:** Command objects are the explicit, documented contract for what a use case needs. The controller builds the command, making the data flow visible. The domain model's `id` field cannot be set by a client because the command has no `id` field.

### Decision 5: Web DTOs isolated in the adapter layer
**Before:** `ConsultationController` serialised `Consultation` directly as HTTP response body.  
**After:** `ConsultationController` maps domain objects to `ConsultationResponse` before returning them.  
**Benefit:** The API contract is separate from the domain model. Renaming a domain field does not change the API. Adding a computed field to the response does not pollute the domain model.

---

## 8. How to Extend the Application

### Adding a new delivery channel (e.g., gRPC)
1. Create a new package `adapter/in/grpc`
2. Implement a gRPC service class that calls `ConsultationUseCase`, `ConsultantUseCase`, or `AuthUseCase`
3. No existing code changes

### Switching from MongoDB to PostgreSQL
1. Create new `@Entity`-annotated JPA entities in `adapter/out/persistence/entity/`
2. Create new Spring Data JPA repositories
3. Create new adapter classes implementing `ConsultationPort`, `ConsultantPort`, and `UserPort`
4. Update Spring wiring (swap `@Component` or use `@Primary`)
5. Domain models, services, and all ports remain completely unchanged

### Adding a new use case (e.g., searching consultations by date range)
1. Add a method to `ConsultationUseCase` interface
2. Implement the method in `ConsultationService`
3. Add the corresponding method to `ConsultationPort` if persistence access is needed
4. Implement the port method in `ConsultationAdapter`
5. Add a new endpoint in `ConsultationController`

### Unit testing a domain service
```java
@Test
void createConsultation_savesAndReturnsConsultation() {
    ConsultationPort mockPort = Mockito.mock(ConsultationPort.class);
    ConsultationService service = new ConsultationService(mockPort);

    CreateConsultationCommand command = new CreateConsultationCommand(
        "C001", "General", "DR001", "PT001",
        "Diagnosis", "Prescription", "Comments",
        new Date(), null, "admin", BigDecimal.TEN);

    Consultation saved = new Consultation();
    saved.setId("abc123");
    Mockito.when(mockPort.saveConsultation(Mockito.any())).thenReturn(saved);

    Consultation result = service.createConsultation(command);

    assertThat(result.getId()).isEqualTo("abc123");
    // No Spring context, no MongoDB, no network — pure business logic test
}
```

---

*Generated: April 2026 | Project: consultation-records | Architecture: Hexagonal (Ports & Adapters)*
