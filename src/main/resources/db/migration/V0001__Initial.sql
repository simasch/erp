create table customer
(
  id         varchar(36)  primary key,
  email      varchar(255) unique not null,
  last_name  varchar(255)        not null,
  first_name varchar(255)        not null
);
