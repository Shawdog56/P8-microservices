-- CREATE DATABASE auth_service;
CREATE TABLE user_ent(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    name VARCHAR(50) NULL,
    middlename VARCHAR(50) NULL,
    lastname VARCHAR(50) NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE
);
CREATE TABLE rol(
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(50) UNIQUE NOT NULL
);
CREATE TABLE rol_user(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL
);

ALTER TABLE rol_user
    ADD CONSTRAINT fk_roluser_user
        FOREIGN KEY (user_id)
        REFERENCES user_ent (id);

ALTER TABLE rol_user
    ADD CONSTRAINT fk_roluser_rol
        FOREIGN KEY (rol_id)
        REFERENCES rol (id);

INSERT INTO rol (description) VALUES 
    ('ROLE_USER'),
    ('ROLE_ADMIN');
INSERT INTO user_ent 
    (username,
    password,
    email,
    name,
    middlename,
    lastname,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired) VALUES 
    ('shawdog',
    '$2a$12$V2DtcgxtEdcC95/ciOFPn.jGh6G.royx3ZPuryAnp2J.ldsg5BCfe',
    'admin@admin.com','admin','admin','admin',TRUE,TRUE,TRUE,TRUE),
    ('profesor',
    '$2a$12$V2DtcgxtEdcC95/ciOFPn.jGh6G.royx3ZPuryAnp2J.ldsg5BCfe',
    'user@user.com','profesor','profesor','profesor',TRUE,TRUE,TRUE,TRUE);
INSERT INTO rol_user (user_id,rol_id) VALUES 
    (1,1),
    (1,2),
    (2,1);