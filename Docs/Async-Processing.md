## ASYNCHRONOUS PROCESSING

# Payment Processing
- Payment callbacks diproses secara asynchronous menggunakan dedicated thread pool paymentExecutor yang dikonfigurasi di AsyncConfig.java. Request thread tidak diblokir — setelah callback diterima, controller langsung return 202 Accepted dan processing dilanjutkan di background thread.
- Untuk Midtrans, notifikasi webhook diterima di POST /api/payments/midtrans/notification, lalu diverifikasi langsung ke Midtrans Core API untuk memastikan keaslian notifikasi sebelum mengupdate status order.

# Idempotency
- Duplikat callback ditangani dengan pengecekan kondisi sebelum update:
    Jika paymentReference sudah ada di order → skip
    Jika status order sudah bukan PENDING → skip
(memastikan status order tetap konsisten meskipun Midtrans mengirim notifikasi lebih dari satu kali)

# Status Transitions
- PENDING → PAID    (settlement / capture + fraud accept)
- PENDING → FAILED  (deny / cancel / expire / failure)

# Midtrans status mapping:

- settlement → PAID
- capture + fraud accept → PAID
- deny, cancel, expire, failure → FAILED
