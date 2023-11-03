create table company
(
    id       bigserial primary key,
    name     text not null unique,
    bm_pay_id int8 unique
)
