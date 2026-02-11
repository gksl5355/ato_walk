## 0. Purpose

본 문서는 commerce scope의 데이터 모델 기준을 정의한다.

---

## 1. Tables

### 1.1 products

- `id` BIGINT PK
- `name` VARCHAR(255) NOT NULL
- `category` VARCHAR(32) NOT NULL
- `price` BIGINT NOT NULL
- `stock_quantity` INTEGER NOT NULL
- `status` VARCHAR(32) NOT NULL
- `description` VARCHAR(1000)
- `created_at` TIMESTAMPTZ NOT NULL

제약:
- `category` in (`FEED`, `SNACK`, `TOY`)
- `status` in (`ON_SALE`, `SOLD_OUT`, `HIDDEN`)
- `price` >= 0
- `stock_quantity` >= 0

### 1.2 cart_items

- `id` BIGINT PK
- `user_id` BIGINT NOT NULL (FK -> users.id)
- `product_id` BIGINT NOT NULL (FK -> products.id)
- `quantity` INTEGER NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL

제약:
- `quantity` between 1 and 99
- unique (`user_id`, `product_id`)

### 1.3 orders

- `id` BIGINT PK
- `user_id` BIGINT NOT NULL (FK -> users.id)
- `status` VARCHAR(32) NOT NULL
- `total_price` BIGINT NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL

제약:
- `status` in (`CREATED`, `CANCELED`)
- `total_price` >= 0

### 1.4 order_items

- `id` BIGINT PK
- `order_id` BIGINT NOT NULL (FK -> orders.id)
- `product_id` BIGINT NOT NULL (FK -> products.id)
- `quantity` INTEGER NOT NULL
- `unit_price` BIGINT NOT NULL
- `subtotal_price` BIGINT NOT NULL

제약:
- `quantity` between 1 and 99
- `unit_price` >= 0
- `subtotal_price` >= 0

---

## 2. Relationship Summary

- `users` 1:N `cart_items`
- `users` 1:N `orders`
- `products` 1:N `cart_items`
- `orders` 1:N `order_items`
- `products` 1:N `order_items`

---

## 3. Naming / PK / Time

- snake_case, plural table names
- PK는 `id` (BIGINT auto increment)
- 시간 타입은 `TIMESTAMPTZ`
