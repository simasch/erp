create table customer
(
    id         integer primary key auto_increment,
    email      varchar(255) unique not null,
    last_name  varchar(255)        not null,
    first_name varchar(255)        not null
);
