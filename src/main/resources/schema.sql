CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa CHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL REFERENCES films (film_id),
    genre_id BIGINT NOT NULL REFERENCES genres (genre_id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    inviter_id BIGINT NOT NULL REFERENCES users (user_id),
    acceptor_id BIGINT NOT NULL REFERENCES users (user_id),
    is_confirmed BOOLEAN NOT NULL,
    min_id BIGINT AS LEAST(inviter_id, acceptor_id),
    max_id BIGINT AS GREATEST(inviter_id, acceptor_id),
    CONSTRAINT uq1 UNIQUE (min_id, max_id),
    PRIMARY KEY (inviter_id, acceptor_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id BIGINT NOT NULL REFERENCES users (user_id),
    film_id BIGINT NOT NULL REFERENCES films (film_id),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS events (
    event_id BIGSERIAL PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users (user_id),
    entity_id BIGINT NOT NULL,
    event_type VARCHAR(30),
    operation VARCHAR(30),
    occurred_on DATETIME NOT NULL
);