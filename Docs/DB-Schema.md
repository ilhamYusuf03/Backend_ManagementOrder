## DATABASE SCHEMA

# orders table
- id (PK, UUID)
- customer_id (INDEX)
- status (INDEX) — nilai: PENDING, PAID, FAILED
- total_amount
- created_at
- paid_at — diisi saat status berubah ke PAID
- payment_reference — referensi ID dari payment gateway
- payment_type — metode pembayaran: qris, gopay, bca_va, dll
- snap_token — token Midtrans Snap untuk redirect ke payment page

# order_items table
- id (PK, UUID)
- order_id (FK → orders.id)
- product_id
- quantity
- price

# INDEX STRATEGY
Index pada customer_id untuk query order by customer. Index pada status untuk filtering berdasarkan status order. Kombinasi keduanya didukung via query findByCustomerIdAndStatus di OrderRepository.

