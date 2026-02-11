create table products (
    id bigserial primary key,
    name varchar(255) not null,
    category varchar(32) not null,
    price bigint not null,
    stock_quantity integer not null,
    status varchar(32) not null,
    description varchar(1000),
    created_at timestamptz not null,
    constraint products_category_check check (category in ('FEED', 'SNACK', 'TOY')),
    constraint products_status_check check (status in ('ON_SALE', 'SOLD_OUT', 'HIDDEN')),
    constraint products_price_check check (price >= 0),
    constraint products_stock_quantity_check check (stock_quantity >= 0)
);

create table cart_items (
    id bigserial primary key,
    user_id bigint not null references users (id),
    product_id bigint not null references products (id),
    quantity integer not null,
    created_at timestamptz not null,
    constraint cart_items_quantity_check check (quantity between 1 and 99),
    constraint cart_items_user_product_unique unique (user_id, product_id)
);

create index cart_items_user_id_idx on cart_items (user_id);
create index cart_items_product_id_idx on cart_items (product_id);

create table orders (
    id bigserial primary key,
    user_id bigint not null references users (id),
    status varchar(32) not null,
    total_price bigint not null,
    created_at timestamptz not null,
    constraint orders_status_check check (status in ('CREATED', 'CANCELED')),
    constraint orders_total_price_check check (total_price >= 0)
);

create index orders_user_id_idx on orders (user_id);

create table order_items (
    id bigserial primary key,
    order_id bigint not null references orders (id),
    product_id bigint not null references products (id),
    quantity integer not null,
    unit_price bigint not null,
    subtotal_price bigint not null,
    constraint order_items_quantity_check check (quantity between 1 and 99),
    constraint order_items_unit_price_check check (unit_price >= 0),
    constraint order_items_subtotal_price_check check (subtotal_price >= 0)
);

create index order_items_order_id_idx on order_items (order_id);
create index order_items_product_id_idx on order_items (product_id);
