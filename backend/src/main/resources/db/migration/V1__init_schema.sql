-- 제휴사
CREATE TABLE affiliations (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    is_paid    BOOLEAN      NOT NULL DEFAULT FALSE
);

-- 차량
CREATE TABLE cars (
    id         BIGSERIAL    PRIMARY KEY,
    car_number VARCHAR(255) NOT NULL UNIQUE
);

-- 고객
CREATE TABLE customers (
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL
);

-- 고객-차량 다대다 관계
CREATE TABLE customer_cars (
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    car_id      BIGINT NOT NULL REFERENCES cars(id),
    PRIMARY KEY (customer_id, car_id)
);

-- 사용자
CREATE TABLE users (
    id             BIGSERIAL    PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    nickname       VARCHAR(255) NOT NULL,
    role           VARCHAR(50)  NOT NULL,
    affiliation_id BIGINT       REFERENCES affiliations(id),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP
);

-- 주차 기록
CREATE TABLE parking_records (
    id                BIGSERIAL    PRIMARY KEY,
    ticket_number     VARCHAR(255) NOT NULL,
    car_number        VARCHAR(255) NOT NULL,
    customer_name     VARCHAR(255) NOT NULL,
    phone_number      VARCHAR(255) NOT NULL,
    parking_area      VARCHAR(255) NOT NULL,
    entry_time        TIMESTAMP    NOT NULL DEFAULT NOW(),
    exit_time         TIMESTAMP,
    status            VARCHAR(50)  NOT NULL,
    affiliation_id    BIGINT,
    registered_by     VARCHAR(255),
    exit_requested_by VARCHAR(255),
    exit_assigned_to  VARCHAR(255),
    updated_at        TIMESTAMP
);
