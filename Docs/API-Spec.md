### Order Management System + Midtrans Payment Gateway
# Base URL: http://localhost:8080
# Auth: Bearer Token (Keycloak JWT)

## ORDER ENDPOINTS

- POST /api/orders → Buat order baru | Role: USER, ADMIN
- GET /api/orders/{orderId} → Get order by ID | Role: USER, ADMIN
- GET /api/orders → Query orders dengan filter customerId, status, pagination & sorting | Role: USER, ADMIN
- GET /api/orders/admin → Query semua orders lintas customer | Role: ADMIN only

## PAYMENT ENDPOINTS

- POST /api/payments/callback → Internal async payment callback | Role: PAYMENT
- POST /api/payments/midtrans/charge/{orderId} → Buat transaksi Midtrans, dapat snap token & redirect URL | Role: USER, ADMIN
- POST /api/payments/midtrans/notification → Webhook notifikasi dari server Midtrans | Public (no auth)
- GET /api/payments/midtrans/status/{orderId} → Cek status transaksi Midtrans | Role: USER, ADMIN

## PUBLIC ENDPOINTS (no auth)
- GET /actuator/health, 
- /v3/api-docs/**, /swagger-ui/**,
- /swagger-ui.html