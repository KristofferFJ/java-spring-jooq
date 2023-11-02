create table tenancy
(
    id         bigserial primary key,
    name       text not null unique,
    company_id int8 references company (id) not null
)
