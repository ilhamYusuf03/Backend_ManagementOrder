## Order Management System

# Folder ini berisi dokumentasi teknis lengkap untuk sistem Secure Order Management Backend yang dibangun menggunakan Java 21, Spring Boot 3.x, Keycloak, PostgreSQL, dan Midtrans Payment Gateway.

# Daftar Dokumen

- API-Spec.md — Spesifikasi lengkap REST API mencakup semua endpoint, format request/response, role yang diperlukan, dan format error.
- Architecture.md — Desain arsitektur sistem mencakup struktur layer (Controller, Service, Repository), alur integrasi Midtrans, dan komponen pendukung.
- Async-Processing.md — Desain pemrosesan pembayaran asynchronous, penanganan idempotency, dan mapping status Midtrans.
- DB-Schema.md — Skema database tabel orders dan order_items mencakup semua kolom, index, dan kolom terkait Midtrans.
- Observability.md — Strategi logging dengan Correlation ID, endpoint health check, dan pendekatan error tracing.
- Performance-test.md — Setup load testing menggunakan k6 dan hasil validasi NFR terhadap target performa yang telah ditentukan.
- Security.md — Konfigurasi keamanan menggunakan Keycloak OAuth2, role-based access control (USER, PAYMENT, ADMIN), dan penanganan JWT token.
- System-overview.md — Gambaran umum sistem mencakup technology stack dan functional requirements.

# Technology Stack

- Java 21 (OpenJDK)
- Spring Boot 3.x
- Keycloak (OAuth2 / OIDC)
- PostgreSQL + Hibernate / JPA + HikariCP
- Midtrans Payment Gateway (Snap API + Core API)
- Springdoc OpenAPI (Swagger UI)
- Maven 3

# Role
- USER - order, query order, Midtrans charge & cek status
- PAYMENTInternal - payment callback
- ADMIN - Semua akses USER + query semua order lintas customer
- Public - Health check, Swagger, webhook notifikasi Midtrans

# Cara Menjalankan

1. Jalankan PostgreSQL & Keycloak
   Pastikan PostgreSQL dan Keycloak sudah berjalan sebelum menjalankan aplikasi.
2. Jalankan Aplikasi
   bashmvn spring-boot:run -Dspring-boot.run.profiles=dev
3. Dokumentasi API
   http://localhost:8080/swagger-ui/index.html
4. Health Check
   http://localhost:8080/actuator/health

# Status Implementasi
Semua functional dan non-functional requirements telah diimplementasikan dan divalidasi, termasuk integrasi Midtrans Payment Gateway.
RequirementStatusFR-01 Buat Order✅ SelesaiFR-02 Query Order✅ SelesaiFR-03 Payment Processing (Async + Midtrans)✅ SelesaiFR-04 Keamanan (Keycloak + Role-based)✅ SelesaiFR-05 Error Handling + Correlation ID✅ SelesaiNFR Latency < 100ms✅ ~14ms rata-rataNFR Throughput ≥ 10K req/menit✅ Terpenuhi
