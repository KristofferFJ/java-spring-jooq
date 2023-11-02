create table bill
(
    id        bigserial primary key,
    tenant_id int8           references tenant (id) not null,
    amount    DECIMAL(10, 2) not null,
    state     text           not null,
    due       date           not null
)
