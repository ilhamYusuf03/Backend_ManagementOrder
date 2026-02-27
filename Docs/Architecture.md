## ARCHITECTURE
- Client → Keycloak → Order API → Service Layer → Repository → PostgreSQL
- Untuk payment flow:
- Client → Keycloak → Order API → Midtrans Snap API → Client (redirect ke payment page)
- Midtrans Server → Order API (webhook notification) → Service Layer → Repository → PostgreSQL

## LAYERED DESIGN
- Controller Layer — Handles HTTP requests and responses. Terdiri dari OrderController dan PaymentController.
- Service Layer — Contains business logic and transactional boundaries. Terdiri dari OrderService, PaymentService, dan MidtransService    untuk integrasi payment gateway.
- Repository Layer — Abstracts database access using JPA. Terdiri dari OrderRepository dan OrderItemRepository.

## SUPPORTING COMPONENTS
- Spring Security — OAuth2 Resource Server terintegrasi dengan Keycloak. Role-based access control: USER, PAYMENT, ADMIN.
- Async Executor — Memproses payment callback secara asynchronous menggunakan dedicated thread pool (paymentExecutor).
- Midtrans Integration — Snap API untuk create transaction, Core API untuk check status dan verifikasi webhook notification.
- Ngrok — Digunakan di environment dev/sandbox untuk mengekspos localhost ke publik agar Midtrans webhook bisa hit endpoint notifikasi.
- Structured Logging — Correlation ID di setiap request untuk tracing.
- Health Checks — Via Spring Actuator di /actuator/health.

