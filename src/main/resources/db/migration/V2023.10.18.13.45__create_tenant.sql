create table tenant
(
    id   bigserial primary key,
    name text not null,
    email text not null,
    tenancy_id int8 not null references tenancy(id) not null
)