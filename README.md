# ecommerce-service

Small ecommerce backend: categories, products (+images), cart, checkout/orders,
payments (COD / KHQR / ABA PayWay), reviews. Spring Boot 3.3.5, MySQL, Flyway,
JWT auth — same architecture as `health-blog-platform` (feature packages,
MapStruct, `shared/api` response envelope, `shared/metadata` current-user
injection), but with BIGINT auto-increment IDs to match your schema.

## Roles

Two roles: `CUSTOMER` (default on signup) and `ADMIN` (seeded on first boot —
see below). Enforced in `SecurityConfiguration`:

| Area                          | CUSTOMER (logged in)          | ADMIN                              |
|--------------------------------|--------------------------------|-------------------------------------|
| Browse categories/products     | ✅ public, active only          | ✅                                   |
| View a single product/its images | ✅ public, active products only | ✅ (also sees inactive ones)        |
| List categories incl. disabled  | ❌                              | ✅ `GET /api/admin/categories`       |
| Manage categories/products     | ❌                              | ✅ `POST/PUT/DELETE /api/products,/categories` |
| Cart, addresses, checkout       | ✅ own data only                | ✅ (as a customer would)             |
| View/cancel own orders          | ✅ `/api/orders/**`             | ✅                                   |
| View **all** orders, change status | ❌                          | ✅ `/api/admin/orders/**`            |
| Confirm a payment (simulated webhook) | ❌                        | ✅ `/api/admin/payments/{id}/confirm`|
| List all products incl. inactive | ❌                            | ✅ `/api/admin/products`             |
| List/manage user roles          | ❌                              | ✅ `/api/admin/users/**`             |
| Reviews (post/delete own)       | ✅                              | ✅                                   |

Every "own data" endpoint (`/api/cart`, `/api/addresses`, `/api/orders`,
`/api/payments`, `/api/reviews`) resolves the current user from the JWT via
`Metadata`/`@MetadataHandler` — a customer can never read or modify another
customer's cart, address, or order, even by guessing an ID.

Default admin account seeded on first run: `admin@shop.local` / `admin123`
(see `AdminSeeder`). Change or delete it in production.

### Granting / revoking ADMIN (`/api/admin/users`)

ADMIN only (`AdminUserController`). Lets an admin see every account and flip
someone between `CUSTOMER` and `ADMIN`:

- `GET /api/admin/users?page=0&size=20` - paginated list of every user.
- `PATCH /api/admin/users/{id}/role` with `{"role": "ADMIN"}` or
  `{"role": "CUSTOMER"}` - grants or revokes admin for that user.

Two guardrails in `AdminUserServiceImpl` so you can't lock yourself out:
- An admin can't change **their own** role.
- You can't demote the **last remaining** admin.

### Enabling/disabling products and categories

`products.is_active` / `categories.is_active` are plain boolean columns in the
DB, but the API speaks in a `Status` enum (`ACTIVE` / `INACTIVE`) instead of a
raw boolean - same reasoning as `OrderStatus`/`PaymentStatus` already in this
codebase, and it leaves room to add more states later (e.g. `DRAFT`) without
another field rename. `CategoryMapper`/`ProductMapper` convert between the two
automatically.

```
POST /api/products   {"name": "...", "price": 9.99, "status": "ACTIVE"}
PUT  /api/products/5  {"status": "INACTIVE"}   // disables it, hides it from browse()/view()
```

Same shape for `/api/categories`. Leaving `status` out of a request keeps the
existing value on update, and defaults to `ACTIVE` on create.

## Payments: KHQR + ABA PayWay

`POST /api/payments/orders/{orderId}/initiate` with `{"method": "COD" | "KHQR" | "ABA_PAYWAY"}`:

- **COD** — marks the payment `PENDING`, no gateway call, settled on delivery.
- **KHQR** — `KhqrService` builds a Cambodia NBC-standard EMV QR payload
  (merchant tag 29, CRC16-CCITT checksum, USD amount) locally. Response
  includes `khqrQrString` — render it as a QR code on the frontend for the
  customer to scan with any KHQR-compatible banking app.
- **ABA_PAYWAY** — `AbaPayWayService` builds the signed (`HMAC-SHA512`) form
  fields ABA's hosted checkout expects. Response includes `abaPayWayFields`
  (POST these) and `abaPayWayCheckoutUrl` (POST them here) — the frontend
  auto-submits an HTML form to redirect the customer to ABA's payment page.

Neither service calls a live bank API (this project's network access doesn't
reach `payway.com.kh` or Bakong, and you need real merchant credentials
anyway). `POST /api/admin/payments/{id}/confirm` stands in for the real
webhook/callback and marks the order `PAID` — wire up the actual
`continue_success_url` / Bakong webhook to call the same logic once you have
real credentials.

Configure via env vars (see `application.yml`):

```
KHQR_MERCHANT_ID=your_name@aclb
KHQR_MERCHANT_NAME=YOUR SHOP
KHQR_MERCHANT_CITY=PHNOM PENH

ABAPAY_MERCHANT_ID=...
ABAPAY_API_KEY=...
ABAPAY_CHECKOUT_URL=https://checkout-sandbox.payway.com.kh/api/payment-gateway/v1/payments/purchase
ABAPAY_CONTINUE_SUCCESS_URL=https://your-api.com/api/payments/aba-payway/callback
ABAPAY_RETURN_URL=https://your-frontend.com/checkout/complete
```

## Running it

```bash
# MySQL running locally on 3306 with a database named ecommerce_db (or set SPRING_DATASOURCE_URL)
mvn spring-boot:run
```

Flyway runs `V1__init_schema.sql` (your schema, plus `refresh_tokens` for
JWT refresh and `reference_id` on `payments` for gateway reconciliation) and
`V2__seed_init_data.sql` (two starter categories) automatically.

## Checkout flow

1. `POST /api/cart/items` to add items.
2. `POST /api/addresses` (first one becomes default automatically).
3. `POST /api/orders/checkout {"addressId": ...}` → validates stock, decrements
   it, snapshots prices into `order_items`, clears the cart, order starts `PENDING`.
4. `POST /api/payments/orders/{orderId}/initiate {"method": "KHQR"}` (etc).
5. `POST /api/admin/payments/{id}/confirm` (ADMIN, simulating the gateway
   callback) → payment `PAID`, order `PAID`.
6. `PATCH /api/admin/orders/{id}/status {"status": "SHIPPED"}` → `DELIVERED`.

Cancelling a `PENDING` order (`POST /api/orders/{id}/cancel`, or ADMIN setting
status to `CANCELLED`) restores stock.

## Error status codes

`GlobalExceptionHandler` maps exceptions to HTTP status consistently across
every module:

| Thrown as                     | HTTP status | When                                              |
|--------------------------------|-------------|----------------------------------------------------|
| `NotFoundException`             | 404         | The resource (or the caller's own copy of it) doesn't exist - address, order, product, category, review, payment, user, etc. |
| `IllegalArgumentException`      | 400         | Bad input - validation, duplicate name, unsupported enum value |
| `IllegalStateException`         | 409         | Valid resource, wrong state - "not enough stock," "order already cancelled" |
| `AuthenticationException`       | 401         | Missing/invalid JWT |
| `AccessDeniedException`         | 403         | Logged in, but role doesn't allow it |

Every "X not found" across every service module throws `NotFoundException`
specifically (not a generic `IllegalArgumentException`) so a missing resource
always comes back as 404, not 400.

## Running with Docker

```
docker compose up --build
```

This builds the app image (multi-stage `Dockerfile`: Maven build stage, then
a slim JRE runtime stage running as a non-root user) and starts it alongside
a MySQL 8 container. The app waits for MySQL's healthcheck before starting.
The API is then available at `http://localhost:8080`.

Override any env var in `docker-compose.yml` (or with an `.env` file) before
deploying for real - at minimum change `SECURITY_JWT_SECRET` and the MySQL
credentials, and set the `KHQR_*`/`ABAPAY_*` vars if you're taking payments.

To build the image standalone (e.g. to push to a registry):
```
docker build -t ecommerce-service .
```
