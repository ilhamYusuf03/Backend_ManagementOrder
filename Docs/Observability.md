## OBSERVABILITY

# Logging
Setiap request dilacak menggunakan Correlation ID yang di-generate otomatis oleh CorrelationIdFilter.java dan disematkan ke setiap log entry. Correlation ID juga dikembalikan di response header X-Correlation-Id sehingga memudahkan tracing dari sisi client.
Contoh response header yang sudah terlihat saat testing:
X-Correlation-Id: 023de335-2032-44ce-b1ac-4c80caead265

# Health Checks
Spring Boot Actuator tersedia di endpoint /actuator/health dan bersifat public (tidak perlu auth). Digunakan untuk monitoring status aplikasi apakah UP atau DOWN.

# Error Tracing
Setiap error response menyertakan correlationId sehingga log bisa langsung ditelusuri berdasarkan ID tersebut:
json{
  "timestamp": "2026-02-23T11:27:24Z",
  "status": 404,
  "error": "ORDER_NOT_FOUND",
  "message": "Order not found",
  "correlationId": "023de335-2032-44ce-b1ac-4c80caead265"
}

# Purpose
Correlation ID memudahkan debugging ketika ada error di production — cukup cari log berdasarkan ID tersebut untuk melihat full request lifecycle. Health check memudahkan monitoring otomatis via tools seperti Prometheus, Grafana, atau uptime checker.