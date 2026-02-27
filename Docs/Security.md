
# Security Design
## Authentication
Autentikasi menggunakan OAuth2 / OpenID Connect dengan Keycloak sebagai Authorization Server. Access token berformat JWT diterbitkan oleh Keycloak dan divalidasi oleh Spring Security OAuth2 Resource Server di setiap request.

## Authorization
- Role-based access control menggunakan 3 role yang didefinisikan di Keycloak Realm order-realm:
- ROLE_USER — Dapat membuat order, query order milik sendiri, melakukan charge ke Midtrans, dan cek status transaksi.
- ROLE_PAYMENT — Khusus untuk internal payment callback processor.
- ROLE_ADMIN — Memiliki semua akses USER ditambah kemampuan query semua order lintas customer.

## Endpoint Security
- /api/orders/** → USER, ADMIN
- /api/orders/admin → ADMIN only
- /api/payments/callback → PAYMENT
- /api/payments/midtrans/charge/** → USER, ADMIN
- /api/payments/midtrans/status/** → USER, ADMIN
- /api/payments/midtrans/notification → Public (Midtrans webhook)
- /actuator/health → Public
- /swagger-ui/**, /v3/api-docs/** → Public

## JWT Role Extraction
Role di-extract dari JWT claim realm_access.roles dan di-mapping ke Spring Security GrantedAuthority dengan prefix ROLE_ via JwtAuthenticationConverter di SecurityConfig.java.

## Security Goals
Memastikan hanya user dengan role yang sesuai dapat mengakses endpoint tertentu. Webhook Midtrans dapat diakses secara public namun diverifikasi ke Midtrans Core API sebelum diproses untuk mencegah manipulasi notifikasi palsu.