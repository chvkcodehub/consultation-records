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
9. [Frontend Application](#9-frontend-application)

---

## 1. Project Overview

**Consultation Records** is a Spring Boot REST API, paired with a React + Vite + TypeScript single-page frontend (`frontend/`), for managing medical consultation records. It supports:

- Dual-role JWT authentication: `ADMIN` (healthcare administrator) and `CONSULTEE` (patient/self-service portal)
- Password reset via a token-based flow
- Full CRUD management of `Consultation` records linking consultants to patients, with a typed `ConsultationType` and a `ConsultationStatus` lifecycle (`BOOKED` → `COMPLETED`/`CANCELLED`)
- Full CRUD management of `Consultant` records representing medical professionals
- Full CRUD management of `Consultee` records representing patients receiving care
- Full CRUD management of `Goal` records representing health goals for patients
- Consultee self-service: registration, browsing consultants, booking a consultation, and reviewing their own consultation history (`/portal/**`)
- Consolidated reporting for administrators: total sessions per consultee, and a consultant summary broken down by consultation type (`/reports/**`)

The backend is built following **Hexagonal Architecture** (also known as Ports & Adapters), ensuring that the core business logic is completely independent of frameworks, databases, and transport protocols. The frontend is a separate Vite SPA that consumes this API exclusively over JSON/HTTPS with JWT Bearer tokens.

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
│  Controller   │  │  GoalAdapter     │  │                  │
│               │  │                  │  │                  │
│  Goal-        │  │  UserAdapter     │  │                  │
│  Controller   │  │                  │  │                  │
│               │  │                  │  │                  │
│  Report-      │  │                  │  │                  │
│  Controller   │  │                  │  │                  │
│               │  │                  │  │                  │
│  Consultee-   │  │                  │  │                  │
│  Portal-      │  │                  │  │                  │
│  Controller   │  │                  │  │                  │
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
│       ConsulteeUseCase   GoalUseCase   ReportUseCase             │    │
│       CreateConsultationCommand   UpdateConsultationCommand      │    │
│       BookConsultationCommand     RegisterConsulteeCommand       │    │
│       CreateConsultantCommand     UpdateConsultantCommand        │    │
│       CreateConsulteeCommand      UpdateConsulteeCommand         │    │
│       CreateGoalCommand           UpdateGoalCommand               │    │
│       AuthResult                                                 │    │
│                                                                  │    │
│  OUT: UserPort   ConsultationPort   ConsultantPort               │    │
│       ConsulteePort   GoalPort   TokenPort                       │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                  DOMAIN SERVICES                                 │    │
│  │                                                                  │    │
│  │   AuthService   ConsultationService   ConsultantService          │    │
│  │   ConsulteeService   GoalService   ReportService                 │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                  DOMAIN MODELS                                   │    │
│  │                                                                  │    │
│  │   Consultation(+type, status)   Consultant   Consultee           │    │
│  │   Goal   User(+role, consulteeCode)   Role                       │    │
│  │   ConsulteeSessionsReport   ConsultantSummaryReport               │    │
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
│   │       ├── GoalController.java              # Inbound: HTTP goal endpoints
│   │       ├── ReportController.java            # Inbound: HTTP reporting endpoints (admin-only)
│   │       ├── ConsulteePortalController.java   # Inbound: HTTP consultee self-service endpoints
│   │       └── dto/
│   │           ├── AuthResponse.java            # now carries { token, role, consulteeCode }
│   │           ├── LoginRequest.java
│   │           ├── RegisterRequest.java
│   │           ├── RegisterConsulteeRequest.java
│   │           ├── ForgotPasswordRequest.java
│   │           ├── ResetPasswordRequest.java
│   │           ├── ConsultationResponse.java
│   │           ├── CreateConsultationRequest.java
│   │           ├── UpdateConsultationRequest.java
│   │           ├── BookConsultationRequest.java
│   │           ├── ConsultantResponse.java
│   │           ├── CreateConsultantRequest.java
│   │           ├── UpdateConsultantRequest.java
│   │           ├── ConsulteeResponse.java
│   │           ├── CreateConsulteeRequest.java
│   │           ├── UpdateConsulteeRequest.java
│   │           ├── GoalResponse.java
│   │           ├── CreateGoalRequest.java
│   │           ├── UpdateGoalRequest.java
│   │           ├── ConsulteeSessionsReportResponse.java
│   │           ├── ConsulteeSessionBreakdownDto.java
│   │           ├── ConsultantSummaryReportResponse.java
│   │           ├── ConsultantSummaryBreakdownDto.java
│   │           └── ConsultationTypeCountDto.java
│   └── out/
│       └── persistence/
│           ├── ConsultationAdapter.java         # Outbound: implements ConsultationPort
│           ├── ConsultationRepository.java      # Spring Data MongoDB repository
│           ├── ConsultantAdapter.java           # Outbound: implements ConsultantPort
│           ├── ConsultantRepository.java        # Spring Data MongoDB repository
│           ├── ConsulteeAdapter.java            # Outbound: implements ConsulteePort
│           ├── ConsulteeRepository.java         # Spring Data MongoDB repository
│           ├── GoalAdapter.java                 # Outbound: implements GoalPort
│           ├── GoalRepository.java              # Spring Data MongoDB repository
│           ├── UserAdapter.java                 # Outbound: implements UserPort
│           ├── UserRepository.java              # Spring Data MongoDB repository
│           ├── entity/
│           │   ├── ConsultationDocument.java    # @Document persistence entity
│           │   ├── ConsultantDocument.java
│           │   ├── ConsulteeDocument.java
│           │   ├── GoalDocument.java
│           │   └── UserDocument.java
│           └── mapper/
│               ├── ConsultationMapper.java      # Domain ↔ Document translation
│               ├── ConsultantMapper.java
│               ├── ConsulteeMapper.java
│               ├── GoalMapper.java
│               └── UserMapper.java
│
├── application/
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Consultation.java                # Pure domain model (+ type, status)
│   │   │   ├── ConsultationType.java             # Enum: fixed set of consultation types
│   │   │   ├── ConsultationStatus.java           # Enum: BOOKED, COMPLETED, CANCELLED
│   │   │   ├── Consultant.java
│   │   │   ├── Consultee.java
│   │   │   ├── Goal.java
│   │   │   ├── User.java                         # (+ role, consulteeCode)
│   │   │   ├── Role.java                         # Enum: ADMIN, CONSULTEE
│   │   │   ├── ConsulteeSessionsReport.java       # Report aggregate: total + per-consultee breakdown
│   │   │   ├── ConsulteeSessionBreakdown.java
│   │   │   ├── ConsultantSummaryReport.java       # Report aggregate: per-consultant + by-type breakdown
│   │   │   ├── ConsultantSummaryBreakdown.java
│   │   │   └── ConsultationTypeCount.java
│   │   └── service/
│   │       ├── AuthService.java                 # Business logic: authentication (admin + consultee)
│   │       ├── ConsultationService.java         # Business logic: consultations + consultee booking
│   │       ├── ConsultantService.java           # Business logic: consultants
│   │       ├── ConsulteeService.java            # Business logic: consultees
│   │       ├── GoalService.java                 # Business logic: goals
│   │       └── ReportService.java               # Business logic: session/consultant reporting
│   └── port/
│       ├── in/
│       │   ├── AuthUseCase.java                 # Inbound port: auth contract
│       │   ├── AuthResult.java                  # { token, role, consulteeCode }
│       │   ├── RegisterConsulteeCommand.java     # Command value object
│       │   ├── ConsultationUseCase.java         # Inbound port: consultation + booking contract
│       │   ├── CreateConsultationCommand.java   # Command value object
│       │   ├── UpdateConsultationCommand.java   # Command value object
│       │   ├── BookConsultationCommand.java     # Command value object (consultee self-booking)
│       │   ├── ConsultantUseCase.java           # Inbound port: consultant contract
│       │   ├── CreateConsultantCommand.java     # Command value object
│       │   ├── UpdateConsultantCommand.java     # Command value object
│       │   ├── ConsulteeUseCase.java            # Inbound port: consultee contract
│       │   ├── CreateConsulteeCommand.java      # Command value object
│       │   ├── UpdateConsulteeCommand.java      # Command value object
│       │   ├── GoalUseCase.java                 # Inbound port: goal contract
│       │   ├── CreateGoalCommand.java           # Command value object
│       │   ├── UpdateGoalCommand.java           # Command value object
│       │   └── ReportUseCase.java               # Inbound port: reporting contract
│       └── out/
│           ├── UserPort.java                    # Outbound port: user persistence
│           ├── ConsultationPort.java            # Outbound port: consultation persistence
│           ├── ConsultantPort.java              # Outbound port: consultant persistence
│           ├── ConsulteePort.java               # Outbound port: consultee persistence (+ findByEmail)
│           ├── GoalPort.java                    # Outbound port: goal persistence
│           └── TokenPort.java                   # Outbound port: token generation (subject, role, consulteeCode)
│
└── security/
    ├── AuthenticatedUser.java                   # Security principal: { email, role, consulteeCode }
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
| `Consultation` | Represents a single medical consultation/booking event. Fields: `id`, `code`, `type` (`ConsultationType`), `status` (`ConsultationStatus`), `consultantCode`, `patientCode`, `diagnosis`, `prescription`, `comments`, `consultationDate`, `followUpDate`, `updatedDate`, `createdBy`, `fee` |
| `ConsultationType` | Enum of the fixed consultation types: `INITIAL_CONSULTATION`, `CHILD_DEVELOPMENT`, `FOLLOW_UP`, `THERAPY_SESSION`, `PARENT_CONSULTATION`, `EMERGENCY`, `ROUTINE_CHECKUP`. Replaced a free-text `String` so reporting "by type" is reliable. |
| `ConsultationStatus` | Enum of the booking lifecycle: `BOOKED`, `COMPLETED`, `CANCELLED`. Defaults to `BOOKED` on create/booking. |
| `Consultant` | Represents a medical professional. Fields: `id`, `code`, `name`, `speciality`, `qualification`, `experienceYears`, `fee` |
| `Consultee` | Represents a patient receiving care. Fields: `id`, `code`, `name`, `gender`, `dob`, `condition`, `address`, `phone`, `email`, `startDate`, `recoveryStatus` |
| `Goal` | Represents a health goal for a patient. Fields: `id`, `code`, `name`, `description`, `importance`, `difficulty`, `achievingAgeYears`, `achievingAgeMonths`, `remarks`, `periodInMonths`, `createdDate`, `updatedDate`, `status` |
| `User` | Represents a system account, either a healthcare administrator or a consultee. Fields: `id`, `email`, `passwordHash`, `role` (`Role`), `consulteeCode` (set only for `CONSULTEE` accounts, links to `Consultee.code`), `createdAt`, `resetToken`, `resetTokenExpiry` |
| `Role` | Enum: `ADMIN`, `CONSULTEE`. Drives JWT authority grants and `SecurityConfig` authorization rules. |
| `ConsulteeSessionsReport` / `ConsulteeSessionBreakdown` | Report aggregate for "total sessions across all patients" + per-consultee session counts. |
| `ConsultantSummaryReport` / `ConsultantSummaryBreakdown` / `ConsultationTypeCount` | Report aggregate for per-consultant session counts, further broken down by `ConsultationType`. |

**Why this matters:** In a naïve implementation, domain models carry `@Document`, `@Id`, and `@Indexed` annotations from Spring Data MongoDB. This creates a hidden coupling: the domain model *is* the persistence entity. Changing the database collection name, adding a MongoDB index, or switching databases requires modifying a business concept class. By keeping domain models annotation-free, these concerns are completely separated.

---

### 5.3 Inbound Ports (`application/port/in`)

Inbound ports define *what the application can do* — the use cases that the outside world (HTTP, CLI, message queue) can invoke. They are plain Java interfaces.

#### `AuthUseCase`
Defines the authentication contract:
- `register(email, password)` → `AuthResult` — admin registration, `role = ADMIN`
- `login(email, password)` → `AuthResult` — works for both `ADMIN` and `CONSULTEE` accounts; the returned `role`/`consulteeCode` tell the caller which
- `registerConsultee(RegisterConsulteeCommand)` → `AuthResult` — consultee self-registration (see `AuthService` below)
- `forgotPassword(email)` — generates a password-reset token
- `resetPassword(email, resetToken, newPassword)` — validates token and updates password

#### `AuthResult`
A `record(token, role, consulteeCode)` — replaced a raw `String` token return type so the frontend can route/guard by role and know a consultee's `consulteeCode` immediately after login, without decoding the JWT client-side.

#### `RegisterConsulteeCommand`
A `record(email, password, name, gender, dob, address, phone)` carrying what a consultee provides at self-registration. No `code` — the service either links to a pre-existing `Consultee` (matched by email, if an admin already created one) or generates a new one.

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

`ConsultationUseCase` additionally defines the consultee self-service booking contract:
- `bookConsultation(BookConsultationCommand)` — creates a `Consultation` with `status = BOOKED`, looks up the consultant to copy their `fee` onto the record, and generates the `code` server-side
- `findConsultationForPatient(id, patientCode)` — ownership-checked lookup; throws `403 Forbidden` if the consultation's `patientCode` does not match the caller, so a consultee can never view another consultee's record by guessing an id

#### `BookConsultationCommand`
An immutable Java `record` carrying only what a consultee provides at booking time: `patientCode`, `consultantCode`, `type`, `consultationDate`, `comments`. `patientCode` is never taken from the request body — `ConsulteePortalController` fills it in from the authenticated JWT's `consulteeCode` claim, so a consultee cannot book on behalf of someone else.

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

#### `GoalUseCase`
Defines the health goal management contract:
- `findGoals()` — list all
- `findGoalById(id)` — find by MongoDB id
- `findGoalByCode(code)` — find by business code
- `createGoal(CreateGoalCommand)` — create new record
- `updateGoal(id, UpdateGoalCommand)` — update existing record
- `deleteGoal(id)` — delete by id

#### `CreateGoalCommand`
An immutable Java `record` carrying the data needed to create a goal: `code`, `name`, `description`, `importance`, `difficulty`, `achievingAgeYears`, `achievingAgeMonths`, `remarks`, `periodInMonths`, `createdDate`, `status`. No `id` field — the application core assigns the identity.

#### `UpdateGoalCommand`
Same fields as `CreateGoalCommand`, replacing `createdDate` with `updatedDate` for update operations.

#### `ReportUseCase`
Defines the admin-only consolidated reporting contract:
- `getConsulteeSessionsReport()` — total sessions across all patients, plus a per-consultee session-count breakdown
- `getConsultantSummaryReport()` — total consultants and total sessions, plus a per-consultant breakdown that is itself broken down by `ConsultationType`

Unlike the other inbound ports, `ReportService` (its implementation) declares **no new outbound port** — it depends directly on the existing `ConsultationPort`, `ConsulteePort`, and `ConsultantPort` and aggregates with Java streams in memory. This is a deliberate choice given the codebase's current scale and its documented preference for simplicity/testability without a database; if data volume ever requires it, a Mongo aggregation pipeline can replace the in-service grouping behind this same interface with no change to `ReportController` or its DTOs.

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
Optional<Consultee> findByEmail(String email);
Consultee save(Consultee consultee);
boolean existsById(String id);
void deleteById(String id);
```
The full persistence contract for consultees, expressed in domain terms. `findByEmail` was added to support consultee self-registration: linking a new `User` account to a pre-existing `Consultee` record an admin already created, matched by email.

#### `GoalPort`
```java
List<Goal> findAll();
Optional<Goal> findById(String id);
Optional<Goal> findByCode(String code);
Goal save(Goal goal);
boolean existsById(String id);
void deleteById(String id);
```
The full persistence contract for goals, expressed in domain terms.

#### `TokenPort`
```java
String generateToken(String subject, String role, String consulteeCode);
```
The domain service needs to generate authentication tokens after a successful login or registration. It does not need to know that the implementation uses JJWT, HMAC-SHA256, or any specific algorithm. `TokenPort` abstracts this to a single method. `role` and `consulteeCode` (nullable) are embedded as JWT claims so `JwtAuthenticationFilter` can reconstruct the caller's authorization on every request without a database lookup.

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
**Depends on:** `UserPort`, `ConsulteePort`, `PasswordEncoder`, `TokenPort`

| Method | Business logic |
|---|---|
| `register` | Checks email uniqueness via `UserPort`, hashes the password, persists the user with `role = ADMIN`, returns an `AuthResult` via `TokenPort` |
| `login` | Finds the user by email, verifies the BCrypt hash matches, returns an `AuthResult` built from the stored `role`/`consulteeCode` — works identically for `ADMIN` and `CONSULTEE` accounts |
| `registerConsultee` | Checks email uniqueness; links to a pre-existing `Consultee` (matched by email via `ConsulteePort.findByEmail`) if an admin already created one, otherwise auto-creates one with a generated code; creates a `User` with `role = CONSULTEE` and the linked `consulteeCode`; returns an `AuthResult` |
| `forgotPassword` | Finds user, generates a UUID reset token, sets a 1-hour expiry, persists via `UserPort` |
| `resetPassword` | Validates the reset token exists and has not expired, updates the password hash, clears the reset token |

> Note: `PasswordEncoder` is a Spring Security interface, not an implementation class — accepting an interface is a valid abstraction. The concrete `BCryptPasswordEncoder` is wired in `SecurityConfig`, not in this service.

#### `ConsultationService`
**Implements:** `ConsultationUseCase`  
**Depends on:** `ConsultationPort`, `ConsultantPort`

| Method | Business logic |
|---|---|
| `createConsultation` | Maps `CreateConsultationCommand` → `Consultation` domain object, defaults `status` to `BOOKED` when not supplied, sets `updatedDate` server-side, delegates to `ConsultationPort.saveConsultation` |
| `updateConsultation` | Verifies record exists via `existsById`, maps `UpdateConsultationCommand` → `Consultation` with the target `id` set, delegates to `saveConsultation` |
| `deleteConsultation` | Verifies record exists, delegates to `deleteById` |
| `bookConsultation` | Looks up the consultant via `ConsultantPort` (404 if unknown), generates a `code`, sets `status = BOOKED`, copies the consultant's `fee` onto the record, delegates to `saveConsultation` |
| `findConsultationForPatient` | Loads by id, throws `403 Forbidden` if `patientCode` doesn't match the caller — the ownership check lives here (not the controller) so it stays covered by the same Mockito unit-test convention as the rest of the service |
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

#### `GoalService`
**Implements:** `GoalUseCase`  
**Depends on:** `GoalPort`

| Method | Business logic |
|---|---|
| `createGoal` | Maps `CreateGoalCommand` → `Goal` domain object, sets `updatedDate` server-side, delegates to `GoalPort.save` |
| `updateGoal` | Verifies record exists via `existsById`, maps `UpdateGoalCommand` → `Goal` with the target `id` set, delegates to `save` |
| `deleteGoal` | Verifies record exists, delegates to `deleteById` |
| Query methods | Pass-through to `GoalPort` |

#### `ReportService`
**Implements:** `ReportUseCase`  
**Depends on:** `ConsultationPort`, `ConsulteePort`, `ConsultantPort`

| Method | Business logic |
|---|---|
| `getConsulteeSessionsReport` | Loads all consultations, groups by `patientCode` with `Collectors.groupingBy(...counting())`, joins against `ConsulteePort.findAll()` for names, returns total + per-consultee breakdown |
| `getConsultantSummaryReport` | Loads all consultations, groups by `consultantCode`, nests a second `groupingBy(Consultation::getType, counting())` for the `byType` breakdown, joins against `ConsultantPort.findAll()` for names and the total consultant count |

**Why this matters:** Domain services are the most important classes for unit testing. Because they have no framework dependencies, testing `ConsultationService.updateConsultation` is as simple as providing a mock `ConsultationPort` — no Spring context, no MongoDB container, no network required. Tests run in milliseconds and test only the business rules.

---

### 5.6 Inbound Web Adapters (`adapter/in/web`)

Inbound adapters are the entry points into the application. They translate HTTP requests into use case invocations and translate use case results back into HTTP responses.

#### `AuthController`
**Base path:** `/auth`  
**Depends on:** `AuthUseCase` (the inbound port — never the service class directly)

| Endpoint | Method | Description |
|---|---|---|
| `/auth/register` | `POST` | Accepts `RegisterRequest`, returns `AuthResponse` (`token`, `role`, `consulteeCode`) |
| `/auth/login` | `POST` | Accepts `LoginRequest`, returns `AuthResponse` — same endpoint for admin and consultee logins |
| `/auth/register-consultee` | `POST` | Accepts `RegisterConsulteeRequest`, returns `AuthResponse` with `201 Created` |
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

All `/consultations/**` endpoints require a valid JWT Bearer token with the `ADMIN` role (see §5.11). Consultee self-service booking goes through `ConsulteePortalController` instead.

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

`GET /consultants/**` requires either `ADMIN` or `CONSULTEE` (consultees need to browse consultants to book); write operations (`POST`/`PUT`/`DELETE`) require `ADMIN` only.

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

#### `GoalController`
**Base path:** `/goals`  
**Depends on:** `GoalUseCase`, command objects from `port/in`

| Endpoint | Method | Description |
|---|---|---|
| `/goals` | `GET` | Returns all goals as `List<GoalResponse>` |
| `/goals/id/{id}` | `GET` | Returns single `GoalResponse` by MongoDB id |
| `/goals/code/{code}` | `GET` | Returns single `GoalResponse` by business code |
| `/goals` | `POST` | Accepts `CreateGoalRequest`, builds `CreateGoalCommand`, returns `GoalResponse` with `201 Created` |
| `/goals/id/{id}` | `PUT` | Accepts `UpdateGoalRequest`, builds `UpdateGoalCommand`, returns `GoalResponse` |
| `/goals/id/{id}` | `DELETE` | Deletes by id, returns `204 No Content` |

All `/goals/**` endpoints require a valid JWT Bearer token with the `ADMIN` role.

#### `ReportController`
**Depends on:** `ReportUseCase`

| Endpoint | Method | Description |
|---|---|---|
| `/reports/consultees/sessions` | `GET` | Returns `ConsulteeSessionsReportResponse` — total sessions across all patients + per-consultee breakdown |
| `/reports/consultants/summary` | `GET` | Returns `ConsultantSummaryReportResponse` — total consultants/sessions + per-consultant breakdown, further split by `ConsultationType` |

All `/reports/**` endpoints require `ADMIN`.

#### `ConsulteePortalController`
**Base path:** `/portal`  
**Depends on:** `ConsultationUseCase`, `ConsulteeUseCase`

| Endpoint | Method | Description |
|---|---|---|
| `/portal/consultations` | `POST` | Reads `AuthenticatedUser` via `@AuthenticationPrincipal`, builds `BookConsultationCommand` with `patientCode` from the JWT (never the request body), returns `ConsultationResponse` with `201 Created` |
| `/portal/consultations` | `GET` | Returns the caller's own consultations (`ConsultationUseCase.findConsultationByPatient`) |
| `/portal/consultations/{id}` | `GET` | Returns a single consultation, `403` if it doesn't belong to the caller |
| `/portal/me` | `GET` | Returns the caller's own `ConsulteeResponse` |

All `/portal/**` endpoints require `CONSULTEE`.

**Why this matters:** Controllers are intentionally thin. They contain no business logic — only HTTP-to-domain translation. This means the HTTP layer can be replaced (e.g., with a gRPC adapter or a message consumer) without touching any business rules. Because controllers depend on the `*UseCase` interface and not on the concrete service class, the controller tests can mock the interface without booting a Spring context.

---

### 5.7 Web DTOs (`adapter/in/web/dto`)

DTOs (Data Transfer Objects) are `record` types that represent the shape of JSON payloads at the HTTP boundary. They are completely separate from domain models.

| Class | Direction | Purpose |
|---|---|---|
| `RegisterRequest` | Inbound | `{ email, password }` for admin registration |
| `LoginRequest` | Inbound | `{ email, password }` for login (admin or consultee) |
| `RegisterConsulteeRequest` | Inbound | `{ email, password, name, gender, dob, address, phone }` for consultee self-registration |
| `ForgotPasswordRequest` | Inbound | `{ email }` for password reset initiation |
| `ResetPasswordRequest` | Inbound | `{ email, resetToken, newPassword }` |
| `AuthResponse` | Outbound | `{ token, role, consulteeCode }` returned after login/register; has a static `from(AuthResult)` factory |
| `CreateConsultationRequest` | Inbound | All fields needed to create a consultation, including `type`/`status` (no `id`) |
| `UpdateConsultationRequest` | Inbound | All fields needed to update a consultation (no `id`) |
| `BookConsultationRequest` | Inbound | `{ consultantCode, type, consultationDate, comments }` for consultee self-booking (no `patientCode` — server-derived) |
| `ConsultationResponse` | Outbound | Full consultation data including `id`, `type`, `status`; has a static `from(Consultation)` factory |
| `CreateConsultantRequest` | Inbound | `{ code, name, speciality, qualification, experienceYears, fee }` (no `id`) |
| `UpdateConsultantRequest` | Inbound | Same fields as `CreateConsultantRequest` |
| `ConsultantResponse` | Outbound | Full consultant data including `id`; has a static `from(Consultant)` factory |
| `CreateConsulteeRequest` | Inbound | `{ code, name, gender, dob, condition, address, phone, email, startDate }` (no `id`) |
| `UpdateConsulteeRequest` | Inbound | Same fields as `CreateConsulteeRequest` |
| `ConsulteeResponse` | Outbound | Full consultee data including `id`; has a static `from(Consultee)` factory |
| `CreateGoalRequest` | Inbound | `{ code, name, description, importance, difficulty, achievingAgeYears, achievingAgeMonths, remarks, periodInMonths, createdDate, status }` (no `id`) |
| `UpdateGoalRequest` | Inbound | Same fields as `CreateGoalRequest`, replacing `createdDate` with `updatedDate` |
| `GoalResponse` | Outbound | Full goal data including `id`; has a static `from(Goal)` factory |
| `ConsulteeSessionsReportResponse` / `ConsulteeSessionBreakdownDto` | Outbound | Mirror `ConsulteeSessionsReport`/`ConsulteeSessionBreakdown`, each with a static `from(...)` factory |
| `ConsultantSummaryReportResponse` / `ConsultantSummaryBreakdownDto` / `ConsultationTypeCountDto` | Outbound | Mirror `ConsultantSummaryReport`/`ConsultantSummaryBreakdown`/`ConsultationTypeCount`, each with a static `from(...)` factory |

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

#### `GoalAdapter`
**Implements:** `GoalPort`  
**Depends on:** `GoalRepository`, `GoalMapper`

Same translation pattern: `Goal` ↔ `GoalDocument` via `GoalMapper`.

#### `ConsultationRepository`
Extends `MongoRepository<ConsultationDocument, String>`. Provides Spring Data auto-generated queries plus three custom `@Query` methods for filtering by `code`, `consultantCode`, and `patientCode`. Uses `ConsultationDocument` — never the domain `Consultation` class.

#### `ConsultantRepository`
Extends `MongoRepository<ConsultantDocument, String>`. Provides one custom `@Query` method: `findByCode(String code)`. Uses `ConsultantDocument`.

#### `ConsulteeRepository`
Extends `MongoRepository<ConsulteeDocument, String>`. Provides two custom `@Query` methods: `findByCode(String code)` and `findByEmail(String email)` (the latter backs consultee self-registration linking). Uses `ConsulteeDocument`.

#### `GoalRepository`
Extends `MongoRepository<GoalDocument, String>`. Provides one custom `@Query` method: `findByCode(String code)`. Uses `GoalDocument`.

#### `UserRepository`
Extends `MongoRepository<UserDocument, String>`. Provides `findByEmail` and `existsByEmail`. Uses `UserDocument`.

**Why this matters:** The repository interfaces only know about persistence entity classes (`*Document`). This means Spring Data's query derivation, `@Document` collection names, and MongoDB-specific annotations are all confined to the adapter layer. The domain core is completely unaware that MongoDB is being used.

---

### 5.9 Persistence Entities (`adapter/out/persistence/entity`)

Persistence entities are the MongoDB-specific counterparts of the domain models. They carry all Spring Data and MongoDB annotations.

| Class | Collection | Key annotations |
|---|---|---|
| `ConsultationDocument` | `Consultation` | `@Document`, `@Id`; `type`/`status` stored as `ConsultationType`/`ConsultationStatus` enums (Spring Data serializes by constant name) |
| `UserDocument` | `users` | `@Document`, `@Id`, `@Indexed(unique=true)` on `email`; `role` stored as `String` (enum name), plus `consulteeCode` |
| `ConsultantDocument` | `Consultant` | `@Document`, `@Id` |
| `ConsulteeDocument` | `Consultee` | `@Document`, `@Id` |
| `GoalDocument` | `Goal` | `@Document`, `@Id` |

> **Migration note:** `ConsultationDocument.type` moved from a free-text `String` to the `ConsultationType` enum, and `UserDocument.roles` (a `Set<String>`) was replaced by a single `role` `String`. Any pre-existing documents with values outside the new enum, or with the old `roles` field, will fail to deserialize (or read as `null`) until migrated — see the note in §5.2 and the git history for the one-time migration this project needed when it shipped.

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

#### `GoalMapper`
- `toDomain(GoalDocument) → Goal`
- `toDocument(Goal) → GoalDocument`

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

- `generateToken(subject, role, consulteeCode)` — builds and signs a JWT with the email as subject, a `role` claim (always present), a `consulteeCode` claim (present only when non-null), current time as issued-at, and configured expiry
- `extractEmail(token)` / `extractRole(token)` / `extractConsulteeCode(token)` — parse the token and return the respective claim
- `isValid(token)` — returns `true` if the token can be parsed and verified without exception

By implementing `TokenPort`, `JwtUtil` participates in the ports-and-adapters structure: the domain service depends on the port interface, and Spring injects this concrete implementation. Swapping to a different JWT library or a token introspection endpoint requires only a new implementation of `TokenPort`.

#### `AuthenticatedUser`
A `record(email, role, consulteeCode)` that serves as the Spring Security **principal** — set as the authentication's principal object by `JwtAuthenticationFilter` instead of a raw email string. Controllers read it via `@AuthenticationPrincipal AuthenticatedUser user`, most notably in `ConsulteePortalController` to derive `patientCode` server-side rather than trusting client input.

#### `JwtAuthenticationFilter`
Extends `OncePerRequestFilter`. Runs on every request before the security chain:
1. Reads the `Authorization: Bearer <token>` header
2. Calls `JwtUtil.isValid()` and extracts `email`/`role`/`consulteeCode`
3. If valid, builds an `AuthenticatedUser` principal, grants a single `ROLE_<role>` `SimpleGrantedAuthority` (e.g. `ROLE_ADMIN`, `ROLE_CONSULTEE`), and sets a `UsernamePasswordAuthenticationToken` in the `SecurityContextHolder`

This filter is what makes secured endpoints enforce JWT authentication *and* role-based authorization without any annotation on the controller.

#### `SecurityConfig`
Configures the Spring Security filter chain:
- Session management: `STATELESS` — no server-side session state; every request must carry its JWT
- CSRF: disabled — appropriate for a stateless REST API consumed by a known SPA
- `JwtAuthenticationFilter` is inserted before `UsernamePasswordAuthenticationFilter`
- Exposes a `BCryptPasswordEncoder` bean used by `AuthService`
- **Role-based authorization rules**, evaluated in order (first match wins — specific matchers are listed before general ones):

  ```java
  .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
  .requestMatchers("/auth/**").permitAll()
  .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
  .requestMatchers(HttpMethod.GET, "/consultants/**").hasAnyRole("ADMIN", "CONSULTEE")
  .requestMatchers("/consultants/**").hasRole("ADMIN")
  .requestMatchers("/portal/**").hasRole("CONSULTEE")
  .requestMatchers("/reports/**").hasRole("ADMIN")
  .requestMatchers("/consultees/**").hasRole("ADMIN")
  .requestMatchers("/consultations/**").hasRole("ADMIN")
  .requestMatchers("/goals/**").hasRole("ADMIN")
  .anyRequest().authenticated()
  ```

  In short: `/auth/**` is public; consultees can browse (`GET`) consultants and use their own `/portal/**` self-service endpoints; everything else (CRUD on consultants/consultees/consultations/goals, plus `/reports/**`) is `ADMIN`-only.

**Why this matters:** Security is a cross-cutting infrastructure concern. By keeping it in its own package and having it interact with the core only through `TokenPort`, the business logic remains clean. The domain service `AuthService` does not need to know how tokens are generated — it just calls `tokenPort.generateToken(email, role, consulteeCode)`. Security policy changes (token expiry, algorithm, key rotation, new roles) are entirely localised to this package.

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
    ConsultantPort consultantPort = Mockito.mock(ConsultantPort.class);
    ConsultationService service = new ConsultationService(mockPort, consultantPort);

    CreateConsultationCommand command = new CreateConsultationCommand(
        "C001", ConsultationType.INITIAL_CONSULTATION, ConsultationStatus.BOOKED, "DR001", "PT001",
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

See `ReportServiceTest` and `ConsultationServiceTest` (`src/test/java/.../application/domain/service/`) for real examples of this pattern applied to the booking and reporting logic.

---

## 9. Frontend Application

**Location:** `frontend/` (sibling to `src/`, a separate Vite project — not part of the Maven build)  
**Stack:** React + Vite + TypeScript, `react-router-dom` v6, native `fetch` (no axios), plain CSS (no component library)

The frontend is the "Frontend Web Application" anticipated in `src/main/resources/design/system-context.mermaid` — it now exists and runs at `http://localhost:5173`, calling this API at `http://localhost:8080` over JSON/HTTPS with a JWT Bearer token. Every backend controller is `@CrossOrigin`-annotated for this origin.

```
frontend/src/
  api/          client.ts (fetch wrapper: base URL, Bearer header, 401 → logout) + one file per resource
  auth/         AuthContext.tsx (token/role/email/consulteeCode, persisted to localStorage), useAuth.ts, RequireRole.tsx (route guard)
  router.tsx    react-router-dom route tree
  types/        TypeScript mirrors of the backend DTOs
  components/   Layout.tsx (role-aware nav), EntityManager.tsx (generic list+form CRUD, powers the four admin CRUD pages)
  pages/admin/  AdminLoginPage, AdminRegisterPage, DashboardPage, Consultants/Consultees/Consultations/GoalsPage, two report pages
  pages/consultee/  ConsulteeLoginPage, ConsulteeRegisterPage, ConsultantsBrowsePage, BookConsultationPage, MyConsultationsPage, ConsultationDetailPage, MyProfilePage
```

**Routing & guards:** `/`, `/admin/login`, `/admin/register`, `/consultee/login`, `/consultee/register` are public. `/admin/*` and `/consultee/*` are each wrapped in a `RequireRole` route guard keyed off the `role` claim returned at login — an authenticated user of the wrong role is redirected to their own section instead of the other one; an unauthenticated user is redirected to the matching login page.

**`EntityManager<T, TInput>`:** the four admin CRUD screens (Consultants, Consultees, Consultations, Goals) are genuinely identical in shape — list, add, edit, delete — so they share one generic component parameterized by column/field config and the resource's typed API client, rather than four near-duplicate implementations.

**Why a separate app instead of `spring-boot:run` serving static resources:** matches the architecture the project already documented (a standalone SPA origin, not server-rendered templates), keeps the frontend's build tooling (Vite/TypeScript/npm) fully decoupled from the Maven backend build, and mirrors the "new inbound adapter without touching existing code" extension pattern in §8 — the SPA is just another consumer of the same JSON API a CLI or mobile client could also use.

---

*Generated: April 2026 · Updated: August 2026 (consultee self-service portal, booking, reporting, and the React frontend) | Project: consultation-records | Architecture: Hexagonal (Ports & Adapters)*
