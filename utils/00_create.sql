CREATE TABLE persons
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    surname      VARCHAR(100) NOT NULL,
    age          INTEGER      NOT NULL,
    address      VARCHAR(255),
    phone_number VARCHAR(15)
);