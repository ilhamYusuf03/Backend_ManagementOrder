## NON-FUNCTIONAL REQUIREMENTS VALIDATION
# Performance Target
Sistem ditargetkan memiliki average latency di bawah 100ms dan throughput minimal 10.000 requests per menit.

# Load Testing Setup
Load testing dilakukan menggunakan k6 dengan skenario constant arrival rate sebesar ~170 requests/second selama 2 menit, setara dengan ~10.000 requests/menit. Endpoint yang ditest adalah GET /api/orders dengan autentikasi Bearer Token.

# Results
- Average latency: ~14ms — jauh di bawah target 100ms.
- P95 latency: ~21ms — artinya 95% request selesai dalam 21ms.
- Throughput: ~10.000 requests/menit — memenuhi target minimum.
- Error rate: 0% — tidak ada request yang gagal selama load test.

# Optimasi yang Berkontribusi
HikariCP connection pool dengan maximum-pool-size: 10 mencegah bottleneck di database. DTO projection di service layer menghindari lazy loading yang tidak perlu. Index pada customer_id dan status di tabel orders mempercepat query filtering. G1GC dan heap tuning di JVM menjaga latency tetap stabil di bawah beban tinggi.