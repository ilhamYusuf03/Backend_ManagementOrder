# System Overview
This document describes a backend-only Secure Order Management System built with Java 21, Spring Boot 3.x, PostgreSQL, Keycloak, and Midtrans Payment Gateway.
The system exposes REST APIs for order management, supports asynchronous payment processing via internal callback and Midtrans Payment Gateway integration, and enforces secure access using Keycloak OAuth2 / OIDC with role-based authorization.

## Core Features
Order Management — Create, query, and manage orders with pagination, filtering by customer and status, and atomic persistence.
Payment Processing — Asynchronous internal payment callback with idempotent handling, and full Midtrans Snap API integration supporting QRIS, GoPay, and Virtual Account (BCA, BNI, BRI, Mandiri).
Security — OAuth2 Resource Server integrated with Keycloak. Three roles defined: USER, PAYMENT, and ADMIN with endpoint-level authorization.
Observability — Structured logging with Correlation ID per request, centralized exception handling with standard error response format, and Spring Actuator health check.

## Technology Stack

Java 21 (OpenJDK)
Spring Boot 3.x
Keycloak (OAuth2 / OIDC)
PostgreSQL + Hibernate / JPA + HikariCP
Midtrans Payment Gateway (Snap API + Core API)
Springdoc OpenAPI (Swagger UI)
Maven 3


## Design Principles
Performance — Average latency ~14ms, throughput ≥ 10.000 requests/minute, G1GC with heap tuning, HikariCP connection pooling, and indexed database queries.
Security — All endpoints protected by JWT token validation and role-based access control. Midtrans webhook verified via Core API before processing.
Maintainability — Clean layered architecture (Controller → Service → Repository), centralized error handling, and structured logging with Correlation ID.
Scalability — Stateless REST API, async payment processing with dedicated thread pool, and connection pooling ready for horizontal scaling