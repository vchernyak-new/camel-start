DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);


INSERT INTO users (name)
VALUES ('Vano'),
       ('Sulico'),
       ('Vimino');


