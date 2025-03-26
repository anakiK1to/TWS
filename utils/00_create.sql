CREATE TABLE persons
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    surname      VARCHAR(100) NOT NULL,
    age          INTEGER      NOT NULL,
    patronymic   VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15)
);