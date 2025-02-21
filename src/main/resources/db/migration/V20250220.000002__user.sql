CREATE TABLE users
(
    id         bigint                              not null primary key,
    login      VARCHAR(50) UNIQUE                  NOT NULL,
    password   VARCHAR(255)                        NOT NULL,
    name       text                                NOT NULL,
    email      text UNIQUE                         NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);