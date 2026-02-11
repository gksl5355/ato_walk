alter table users
    add column point_balance bigint not null default 0,
    add constraint users_point_balance_check check (point_balance >= 0);

create table point_transactions (
    id bigserial primary key,
    user_id bigint not null references users (id),
    order_id bigint references orders (id),
    type varchar(32) not null,
    amount bigint not null,
    balance_after bigint not null,
    created_at timestamptz not null,
    constraint point_transactions_type_check check (type in ('CHARGE', 'SPEND', 'REFUND')),
    constraint point_transactions_amount_check check (amount > 0),
    constraint point_transactions_balance_after_check check (balance_after >= 0)
);

create index point_transactions_user_id_created_at_desc_idx
    on point_transactions (user_id, created_at desc);

update users
set point_balance = 200000
where email = 'ato@ato.com';
