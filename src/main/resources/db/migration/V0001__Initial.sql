create table customer
(
    id         integer primary key auto_increment,
    email      varchar(255) unique not null,
    last_name  varchar(255)        not null,
    first_name varchar(255)        not null
);

create table phone
(
    id          integer primary key auto_increment,
    number      varchar(255) not null,
    type        varchar(255) not null,
    customer_id integer      not null,

    CONSTRAINT fk_phone_customer
        FOREIGN KEY (customer_id) REFERENCES customer (id)
);
