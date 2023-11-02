create table company
(
    id   bigserial primary key,
    name text not null unique
)